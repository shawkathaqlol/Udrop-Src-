package me.devkevin.practice.events.oitc;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.PlayerUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OITCEvent extends PracticeEvent<OITCPlayer> {
    private final Map<UUID, OITCPlayer> players;
    private final OITCCountdownTask countdownTask;
    private OITCGameTask gameTask;
    private List<CustomLocation> respawnLocations;

    public OITCEvent() {
        super("OITC");
        this.players = new HashMap<UUID, OITCPlayer>();
        this.gameTask = null;
        this.countdownTask = new OITCCountdownTask(this);
    }

    @Override
    public Map<UUID, OITCPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getOitcLocation());
    }

    @Override
    public void onStart() {
        this.respawnLocations = new ArrayList<>();
        (this.gameTask = new OITCGameTask()).runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return (player) -> {
            OITCPlayer data = this.getPlayer(player);
            if(data != null) {
                if(data.getState() != OITCPlayer.OITCState.WAITING) {
                    if(data.getState() == OITCPlayer.OITCState.FIGHTING || data.getState() == OITCPlayer.OITCState.PREPARING || data.getState() == OITCPlayer.OITCState.RESPAWNING) {
                        String deathMessage = ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + "(" + data.getScore() + ")" + ChatColor.GRAY + " has been eliminated from the game.";
                        if(data.getLastKiller() != null) {
                            OITCPlayer playerData = data.getLastKiller();
                            Player respawnTask = this.getPlugin().getServer().getPlayer(playerData.getUuid());
                            int count = playerData.getScore() + 1;
                            playerData.setScore(count);
                            respawnTask.getInventory().setItem(6, ItemUtil.createItem(Material.GLOWSTONE_DUST, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills", playerData.getScore() == 0?1:playerData.getScore()));
                            if(respawnTask.getInventory().contains(Material.ARROW)) {
                                respawnTask.getInventory().getItem(8).setAmount(respawnTask.getInventory().getItem(8).getAmount() + 2);
                            } else {
                                respawnTask.getInventory().setItem(8, new ItemStack(Material.ARROW, 2));
                            }

                            respawnTask.updateInventory();
                            respawnTask.playSound(respawnTask.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                            FireworkEffect fireworkEffect = FireworkEffect.builder().withColor(Color.fromRGB(127, 56, 56)).withFade(Color.fromRGB(127, 56, 56)).with(FireworkEffect.Type.BALL).build();
                            PlayerUtil.sendFirework(fireworkEffect, player.getLocation().add(0.0D, 1.5D, 0.0D));
                            PlayerData playerData1 = this.getPlugin().getPlayerManager().getPlayerData(respawnTask.getUniqueId());
                            playerData1.setOitcEventKills(playerData1.getOitcEventKills() + 1);
                            data.setLastKiller(null);
                            deathMessage = ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + "(" + data.getScore() + ")" + ChatColor.GRAY + " has been killed" + (respawnTask == null?".":" by " + ChatColor.GREEN + respawnTask.getName() + "(" + count + ")");
                            if(count == 25) {
                                PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(respawnTask.getUniqueId());
                                winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);

                                for(int i = 0; i <= 2; ++i) {
                                    String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + respawnTask.getName();
                                    Bukkit.broadcastMessage(announce);
                                }

                                this.gameTask.cancel();
                                this.end();
                            }
                        }

                        if(data.getLastKiller() == null) {
                            data.setLives(data.getLives() - 1);
                            PlayerData var12 = this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId());
                            var12.setOitcEventDeaths(var12.getOitcEventDeaths() + 1);
                            if(data.getLives() == 0) {
                                var12.setOitcEventLosses(var12.getOitcEventLosses() + 1);
                                this.getPlayers().remove(player.getUniqueId());
                                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "You have been eliminated from the game.");
                                this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
                                    this.getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                                    if(this.getPlayers().size() >= 2) {
                                        this.getPlugin().getEventManager().addSpectatorOITC(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                                    }

                                });
                            } else {
                                BukkitTask respawnTask = new RespawnTask(player, data).runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
                                data.setRespawnTask(respawnTask);
                            }
                        }

                        this.sendMessage(deathMessage);
                    }

                }
            }
        };
    }

    public void teleportNextLocation(final Player player) {
        player.teleport(this.getGameLocations().remove(ThreadLocalRandom.current().nextInt(this.getGameLocations().size())).toBukkitLocation());
    }

    private List<CustomLocation> getGameLocations() {
        if (this.respawnLocations != null && this.respawnLocations.size() == 0) {
            this.respawnLocations.addAll(this.getPlugin().getSpawnManager().getOitcSpawnpoints());
        }
        return this.respawnLocations;
    }

    private void giveRespawnItems(final Player player, final OITCPlayer oitcPlayer) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Wood Sword"));
            player.getInventory().setItem(1, ItemUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow"));
            player.getInventory().setItem(6, ItemUtil.createItem(Material.GLOWSTONE_DUST, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills", (oitcPlayer.getScore() == 0) ? 1 : oitcPlayer.getScore()));
            player.getInventory().setItem(7, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString() + ChatColor.BOLD + "Lives", oitcPlayer.getLives()));
            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
            player.updateInventory();
        });
    }

    private Player getWinnerPlayer() {
        if (this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 0) {
            return null;
        }
        final List<OITCPlayer> fighting = this.sortedScores();
        return this.getPlugin().getServer().getPlayer(fighting.get(0).getUuid());
    }

    private List<UUID> getByState(final OITCPlayer.OITCState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public List<OITCPlayer> sortedScores() {
        final List<OITCPlayer> list = new ArrayList<OITCPlayer>(this.players.values());
        list.sort(new SortComparator().reversed());
        return list;
    }

    public OITCGameTask getGameTask() {
        return this.gameTask;
    }

    public class RespawnTask extends BukkitRunnable {
        private final Player player;
        private final OITCPlayer oitcPlayer;
        private int time;

        public RespawnTask(final Player player, final OITCPlayer oitcPlayer) {
            this.time = 5;
            this.player = player;
            this.oitcPlayer = oitcPlayer;
        }

        public void run() {
            if (this.oitcPlayer.getLives() == 0) {
                this.cancel();
                return;
            }
            if (this.time > 0) {
                this.player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Respawning in " + this.time + "...");
            }
            if (this.time == 5) {
                OITCEvent.this.getPlugin().getServer().getScheduler().runTask(OITCEvent.this.getPlugin(), () -> {
                    PlayerUtil.clearPlayer(this.player);
                    OITCEvent.this.getBukkitPlayers().forEach(member -> member.hidePlayer(this.player));
                    OITCEvent.this.getBukkitPlayers().forEach(this.player::hidePlayer);
                    this.player.setGameMode(GameMode.SPECTATOR);
                    return;
                });
                this.oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);
            } else if (this.time <= 0) {
                this.player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Respawning...");
                this.player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.RED.toString() + ChatColor.BOLD + this.oitcPlayer.getLives() + " " + ((this.oitcPlayer.getLives() == 1) ? "LIFE" : "LIVES") + " REMAINING");
                OITCEvent.this.getPlugin().getServer().getScheduler().runTaskLater(OITCEvent.this.getPlugin(), () -> {
                    OITCEvent.this.giveRespawnItems(this.player, this.oitcPlayer);
                    this.player.teleport(OITCEvent.this.getGameLocations().remove(ThreadLocalRandom.current().nextInt(OITCEvent.this.getGameLocations().size())).toBukkitLocation());
                    OITCEvent.this.getBukkitPlayers().forEach(member -> member.showPlayer(this.player));
                    OITCEvent.this.getBukkitPlayers().forEach(this.player::showPlayer);
                    return;
                }, 2L);
                this.oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);
                this.cancel();
            }
            --this.time;
        }

        public Player getPlayer() {
            return this.player;
        }

        public OITCPlayer getOitcPlayer() {
            return this.oitcPlayer;
        }

        public int getTime() {
            return this.time;
        }
    }

    public class OITCGameTask extends BukkitRunnable {
        private int time;

        public OITCGameTask() {
            this.time = 303;
        }

        public void run() {
            if (this.time == 303) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers());
            } else if (this.time == 302) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers());
            } else if (this.time == 301) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers());
            } else if (this.time == 300) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", OITCEvent.this.getBukkitPlayers());
                for (final OITCPlayer player : OITCEvent.this.getPlayers().values()) {
                    player.setScore(0);
                    player.setLives(5);
                    player.setState(OITCPlayer.OITCState.FIGHTING);
                }
                for (final Player player2 : OITCEvent.this.getBukkitPlayers()) {
                    final OITCPlayer oitcPlayer = OITCEvent.this.getPlayer(player2.getUniqueId());
                    if (oitcPlayer != null) {
                        OITCEvent.this.teleportNextLocation(player2);
                        OITCEvent.this.giveRespawnItems(player2, oitcPlayer);
                    }
                }
            } else if (this.time <= 0) {
                final Player winner = OITCEvent.this.getWinnerPlayer();
                if (winner != null) {
                    final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);
                    for (int i = 0; i <= 2; ++i) {
                        final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                        Bukkit.broadcastMessage(announce);
                    }
                }
                OITCEvent.this.gameTask.cancel();
                OITCEvent.this.end();
                this.cancel();
                return;
            }
            if (OITCEvent.this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 1 || OITCEvent.this.getPlayers().size() == 1) {
                final Player winner = Bukkit.getPlayer(OITCEvent.this.getByState(OITCPlayer.OITCState.FIGHTING).get(0));
                final PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);
                for (int i = 0; i <= 2; ++i) {
                    final String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
                    Bukkit.broadcastMessage(announce);
                }
                this.cancel();
                OITCEvent.this.end();
            }
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers());
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers());
            }
            --this.time;
        }

        public int getTime() {
            return this.time;
        }
    }

    private class SortComparator implements Comparator<OITCPlayer> {
        @Override
        public int compare(final OITCPlayer p1, final OITCPlayer p2) {
            return Integer.compare(p1.getScore(), p2.getScore());
        }
    }
}
