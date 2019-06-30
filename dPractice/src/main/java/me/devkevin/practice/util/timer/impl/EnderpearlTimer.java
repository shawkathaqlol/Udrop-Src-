package me.devkevin.practice.util.timer.impl;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import me.devkevin.practice.util.timer.PlayerTimer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {
    public EnderpearlTimer() {
        super("Enderpearl", TimeUnit.SECONDS.toMillis(15L));
    }

    @Override
    protected void handleExpiry(final Player player, final UUID playerUUID) {
        super.handleExpiry(player, playerUUID);
        if (player == null) {
            return;
        }
        player.sendMessage(ChatColor.RED + "(!) You can use Enderpearls again.");
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) || !event.hasItem()) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getItem().getType() == Material.ENDER_PEARL) {
            final long cooldown = this.getRemaining(player);
            if (cooldown > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Enderpearl cooldown: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(cooldown, true, true));
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onPearlLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
            final Player player = (Player) event.getEntity().getShooter();
            this.setCooldown(player, player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportEvent(final PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        final Player player = event.getPlayer();
        if (this.getRemaining(player) != 0L && event.isCancelled()) {
            this.clearCooldown(player);
        }
        event.getTo().setX((double) event.getTo().getBlockX() + 0.5);
        event.getTo().setZ((double) event.getTo().getBlockZ() + 0.5);
        if (event.getTo().getBlock().getType() != Material.AIR) {
            event.getTo().setY(event.getTo().getY() - (event.getTo().getY() - event.getTo().getBlockY()));
        }
    }
}
