package me.devkevin.practice.runnable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;

public class MatchResetRunnable extends BukkitRunnable {
    private final Practice plugin;
    private final Match match;

    public MatchResetRunnable(final Match match) {
        this.plugin = Practice.getInstance();
        this.match = match;
    }

    public void run() {
        int count = 0;
        if (this.match.getKit().isBuild()) {
            for (final Location location : this.match.getPlacedBlockLocations()) {
                if (++count > 15) {
                    break;
                }
                location.getBlock().setType(Material.AIR);
                this.match.removePlacedBlockLocation(location);
            }
        } else {
            for (final BlockState blockState : this.match.getOriginalBlockChanges()) {
                if (++count > 15) {
                    break;
                }
                blockState.getLocation().getBlock().setType(blockState.getType());
                this.match.removeOriginalBlockChange(blockState);
            }
        }
        if (count < 15) {
            this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
            this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
            this.cancel();
        }
    }
}
