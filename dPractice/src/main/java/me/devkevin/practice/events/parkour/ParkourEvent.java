package me.devkevin.practice.events.parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.util.BlockUtil;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.PlayerUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {
    private final Map<UUID, ParkourPlayer> players;
    private final ParkourCountdownTask countdownTask;
    private ParkourGameTask gameTask;
    private WaterCheckTask waterCheckTask;
    private List<UUID> visibility;

    public ParkourEvent() {
        super("Parkour");
        this.players = new HashMap<UUID, ParkourPlayer>();
        this.gameTask = null;
        this.countdownTask = new ParkourCountdownTask(this);
    }

    @Override
    public Map<UUID, ParkourPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
    }

    @Override
    public void onStart() {
        (this.gameTask = new ParkourGameTask()).runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        (this.waterCheckTask = new WaterCheckTask()).runTaskTimer(this.getPlugin(), 0L, 10L);
        this.visibility = new ArrayList<>();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player ->this.players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            String message = ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the game.";
            this.sendMessage(message);
            PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            playerData.setParkourEventLosses(playerData.getParkourEventLosses() + 1);
        };
    }

    public void toggleVisibility(final Player player) {
        if (this.visibility.contains(player.getUniqueId())) {
            for (final Player playing : this.getBukkitPlayers()) {
                player.showPlayer(playing);
            }
            this.visibility.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now showing players.");
            return;
        }
        for (final Player playing : this.getBukkitPlayers()) {
            player.hidePlayer(playing);
        }
        this.visibility.add(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "You are now hiding players.");
    }

    private void teleportToSpawnOrCheckpoint(final Player player) {
        final ParkourPlayer parkourPlayer = this.getPlayer(player.getUniqueId());
        if (parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
            player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
            player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Teleporting back to last checkpoint.");
            return;
        }
        player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Teleporting back to the beginning.");
        player.teleport(this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
    }

    private void giveItems(final Player player) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, ItemUtil.createItem(Material.FIREBALL, ChatColor.GREEN.toString() + ChatColor.BOLD + "Toggle Visibility"));
            player.getInventory().setItem(4, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + ChatColor.BOLD + "Leave Event"));
            player.updateInventory();
        });
    }

    private Player getRandomPlayer() {
        if (this.getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
            return null;
        }
        final List<UUID> fighting = this.getByState(ParkourPlayer.ParkourState.INGAME);
        Collections.shuffle(fighting);
        final UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
        return this.getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(final ParkourPlayer.ParkourState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public ParkourGameTask getGameTask() {
        return this.gameTask;
    }

    public WaterCheckTask getWaterCheckTask() {
        return this.waterCheckTask;
    }

    public class ParkourGameTask extends BukkitRunnable {
        private int time;

        public ParkourGameTask() {
            this.time = 303;
        }

        public void run() {
            if (this.time == 303) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers());
            } else if (this.time == 302) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers());
            } else if (this.time == 301) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers());
            } else if (this.time == 300) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", ParkourEvent.this.getBukkitPlayers());
                for (final ParkourPlayer player : ParkourEvent.this.getPlayers().values()) {
                    player.setLastCheckpoint(null);
                    player.setState(ParkourPlayer.ParkourState.INGAME);
                    player.setCheckpointId(0);
                }
                for (final Player player2 : ParkourEvent.this.getBukkitPlayers()) {
                    ParkourEvent.this.teleportToSpawnOrCheckpoint(player2);
                    ParkourEvent.this.giveItems(player2);
                }
            } else if (this.time <= 0) {
                final Player winner = ParkourEvent.this.getRandomPlayer();
                if (winner != null) {
                    final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);
                    for (int i = 0; i <= 2; ++i) {
                        final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                        Bukkit.broadcastMessage(announce);
                    }
                }
                ParkourEvent.this.end();
                this.cancel();
                return;
            }
            if (ParkourEvent.this.getPlayers().size() == 1) {
                final Player winner = Bukkit.getPlayer(ParkourEvent.this.getByState(ParkourPlayer.ParkourState.INGAME).get(0));
                if (winner != null) {
                    final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);
                    for (int i = 0; i <= 2; ++i) {
                        final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                        Bukkit.broadcastMessage(announce);
                    }
                }
                ParkourEvent.this.end();
                this.cancel();
                return;
            }
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers());
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers());
            }
            --this.time;
        }

        public int getTime() {
            return this.time;
        }
    }

    public class WaterCheckTask extends BukkitRunnable {
        public void run() {
            if (ParkourEvent.this.getPlayers().size() <= 1) {
                return;
            }
            ParkourEvent.this.getBukkitPlayers().forEach(player -> {
                if (ParkourEvent.this.getPlayer(player) == null || ParkourEvent.this.getPlayer(player).getState() == ParkourPlayer.ParkourState.INGAME) {
                    if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
                        ParkourEvent.this.teleportToSpawnOrCheckpoint(player);
                    } else if (BlockUtil.isStandingOn(player, Material.STONE_PLATE) || BlockUtil.isStandingOn(player, Material.IRON_PLATE) || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {
                        ParkourPlayer parkourPlayer = ParkourEvent.this.getPlayer(player.getUniqueId());
                        if (parkourPlayer != null) {
                            boolean checkpoint = false;
                            if (parkourPlayer.getLastCheckpoint() == null) {
                                checkpoint = true;
                                parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                            } else if (parkourPlayer.getLastCheckpoint() != null && !BlockUtil.isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
                                checkpoint = true;
                                parkourPlayer.setLastCheckpoint(CustomLocation.fromBukkitLocation(player.getLocation()));
                            }
                            if (checkpoint) {
                                parkourPlayer.setCheckpointId(parkourPlayer.getCheckpointId() + 1);
                                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Checkpoint #" + parkourPlayer.getCheckpointId() + " has been set.");
                            }
                        }
                    } else if (BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
                        for (int i = 0; i <= 2; ++i) {
                            String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + player.getName();
                            Bukkit.broadcastMessage(announce);
                        }
                        ParkourEvent.this.end();
                        this.cancel();
                    }
                }
            });
        }
    }
}
