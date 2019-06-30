package me.devkevin.practice.events.sumo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.util.PlayerUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SumoEvent extends PracticeEvent<SumoPlayer> {
    final HashSet<String> fighting;
    private final Map<UUID, SumoPlayer> players;
    private final SumoCountdownTask countdownTask;
    private WaterCheckTask waterCheckTask;

    public SumoEvent() {
        super("Sumo");
        this.players = new HashMap<>();
        this.fighting = new HashSet<>();
        this.countdownTask = new SumoCountdownTask(this);
    }

    @Override
    public Map<UUID, SumoPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getSumoLocation());
    }

    @Override
    public void onStart() {
        (this.waterCheckTask = new WaterCheckTask()).runTaskTimer(this.getPlugin(), 0L, 10L);
        this.selectPlayers();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            SumoPlayer data = this.getPlayer(player);
            if (data != null && data.getFighting() != null) {
                if (data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {
                    SumoPlayer killerData = data.getFighting();
                    Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
                    data.getFightTask().cancel();
                    killerData.getFightTask().cancel();
                    PlayerData playerData = this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId());
                    if (playerData != null) {
                        playerData.setSumoEventLosses(playerData.getSumoEventLosses() + 1);
                    }

                    data.setState(SumoPlayer.SumoState.ELIMINATED);
                    killerData.setState(SumoPlayer.SumoState.WAITING);
                    PlayerUtil.clearPlayer(player);
                    this.getPlugin().getPlayerManager().giveLobbyItems(player);
                    PlayerUtil.clearPlayer(killer);
                    this.getPlugin().getPlayerManager().giveLobbyItems(killer);
                    if (this.getSpawnLocations().size() == 1) {
                        player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                        killer.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                    }

                    this.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + ChatColor.GRAY + " has been eliminated" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName()));
                    if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
                        Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));
                        PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                        winnerData.setSumoEventWins(winnerData.getSumoEventWins() + 1);

                        for (int i = 0; i <= 2; ++i) {
                            String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                            Bukkit.broadcastMessage(announce);
                        }

                        this.fighting.clear();
                        this.end();
                    } else {
                        this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), this::selectPlayers, 60L);
                    }
                }

            }
        };
    }


    private CustomLocation[] getSumoLocations() {
        final CustomLocation[] array = {this.getPlugin().getSpawnManager().getSumoFirst(), this.getPlugin().getSpawnManager().getSumoSecond()};
        return array;
    }

    private void selectPlayers() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
            final Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));
            final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
            winnerData.setSumoEventWins(winnerData.getSumoEventWins() + 1);
            for (int i = 0; i <= 2; ++i) {
                final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                Bukkit.broadcastMessage(announce);
            }
            this.fighting.clear();
            this.end();
            return;
        }
        final Player picked1 = this.getRandomPlayer();
        final Player picked2 = this.getRandomPlayer();
        if (picked1 == null || picked2 == null) {
            this.selectPlayers();
            return;
        }
        this.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Selecting random players...");
        this.fighting.clear();
        final SumoPlayer picked1Data = this.getPlayer(picked1);
        final SumoPlayer picked2Data = this.getPlayer(picked2);
        picked1Data.setFighting(picked2Data);
        picked2Data.setFighting(picked1Data);
        this.fighting.add(picked1.getName());
        this.fighting.add(picked2.getName());
        PlayerUtil.clearPlayer(picked1);
        PlayerUtil.clearPlayer(picked2);
        picked1.teleport(this.getSumoLocations()[0].toBukkitLocation());
        picked2.teleport(this.getSumoLocations()[1].toBukkitLocation());
        for (final Player other : this.getBukkitPlayers()) {
            if (other != null) {
                other.showPlayer(picked1);
                other.showPlayer(picked2);
            }
        }
        for (final UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators().keySet()) {
            final Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectatorUUID != null) {
                spectator.showPlayer(picked1);
                spectator.showPlayer(picked2);
            }
        }
        picked1.showPlayer(picked2);
        picked2.showPlayer(picked1);
        this.sendMessage(ChatColor.YELLOW + "Starting event match. " + ChatColor.GREEN + "(" + picked1.getName() + " vs " + picked2.getName() + ")");
        final BukkitTask task = new SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(this.getPlugin(), 0L, 20L);
        picked1Data.setFightTask(task);
        picked2Data.setFightTask(task);
    }

    private Player getRandomPlayer() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 0) {
            return null;
        }
        final List<UUID> waiting = this.getByState(SumoPlayer.SumoState.WAITING);
        Collections.shuffle(waiting);
        final UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));
        this.getPlayer(uuid).setState(SumoPlayer.SumoState.PREPARING);
        return this.getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(final SumoPlayer.SumoState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public HashSet<String> getFighting() {
        return this.fighting;
    }

    public WaterCheckTask getWaterCheckTask() {
        return this.waterCheckTask;
    }

    public class SumoFightTask extends BukkitRunnable {
        private final Player player;
        private final Player other;
        private final SumoPlayer playerSumo;
        private final SumoPlayer otherSumo;
        private int time;

        public SumoFightTask(final Player player, final Player other, final SumoPlayer playerSumo, final SumoPlayer otherSumo) {
            this.time = 90;
            this.player = player;
            this.other = other;
            this.playerSumo = playerSumo;
            this.otherSumo = otherSumo;
        }

        public void run() {
            if (this.player == null || this.other == null || !this.player.isOnline() || !this.other.isOnline()) {
                this.cancel();
                return;
            }
            if (this.time == 90) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", this.player, this.other);
            } else if (this.time == 89) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", this.player, this.other);
            } else if (this.time == 88) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", this.player, this.other);
            } else if (this.time == 87) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The match has started, good luck!", this.player, this.other);
                this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
                this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
            } else if (this.time <= 0) {
                final List<Player> players = Arrays.asList(this.player, this.other);
                final Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> SumoEvent.this.onDeath().accept(pl));
                this.cancel();
                return;
            }
            if (Arrays.asList(30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other);
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The match is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other);
            }
            --this.time;
        }

        public Player getPlayer() {
            return this.player;
        }

        public Player getOther() {
            return this.other;
        }

        public SumoPlayer getPlayerSumo() {
            return this.playerSumo;
        }

        public SumoPlayer getOtherSumo() {
            return this.otherSumo;
        }

        public int getTime() {
            return this.time;
        }
    }

    public class WaterCheckTask extends BukkitRunnable {
        public void run() {
            if (SumoEvent.this.getPlayers().size() <= 1) {
                return;
            }
            SumoEvent.this.getBukkitPlayers().forEach(player -> {
                if (SumoEvent.this.getPlayer(player) == null || (SumoEvent.this.getPlayer(player)).getState() == SumoPlayer.SumoState.FIGHTING) {
                    Block legs = player.getLocation().getBlock();
                    Block head = legs.getRelative(BlockFace.UP);
                    if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                        SumoEvent.this.onDeath().accept(player);
                    }
                }
            });
        }
    }
}
