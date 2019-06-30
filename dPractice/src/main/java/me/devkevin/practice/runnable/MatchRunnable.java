package me.devkevin.practice.runnable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;

public class MatchRunnable extends BukkitRunnable {
    private final Practice plugin;
    private final Match match;

    public MatchRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }

    public void run() {
        switch (this.match.getMatchState()) {
            case STARTING:
                if (this.match.decrementCountdown() != 0) {
                    this.match.broadcast(ChatColor.GREEN + "Starting in " + ChatColor.YELLOW + this.match.getCountdown() + ChatColor.GREEN + " seconds!");
                    break;
                }
                this.match.setMatchState(MatchState.FIGHTING);
                this.match.broadcast(ChatColor.GREEN + "The match has started, good luck!");
                if (this.match.isRedrover()) {
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            case SWITCHING:
                if (this.match.decrementCountdown() == 0) {
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.clearEntitiesToRemove();
                    this.match.setMatchState(MatchState.FIGHTING);
                    this.plugin.getMatchManager().pickPlayer(this.match);
                    break;
                }
                break;
            case ENDING:
                if (this.match.decrementCountdown() == 0) {
                    this.plugin.getTournamentManager().removeTournamentMatch(this.match);
                    this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask(id));
                    this.match.getEntitiesToRemove().forEach(Entity::remove);
                    this.match.getTeams().forEach(team -> team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
                    this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
                    this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
                    this.match.getOriginalBlockChanges().forEach(blockState -> blockState.getLocation().getBlock().setType(blockState.getType()));
                    if (this.match.getKit().isBuild() || this.match.getKit().isSpleef()) {
                        this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
                        this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
                    }
                    this.plugin.getMatchManager().removeMatch(this.match);
                    new MatchResetRunnable(this.match).runTaskTimer(this.plugin, 20L, 20L);
                    this.cancel();
                    break;
                }
                break;
        }
    }
}
