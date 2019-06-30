package me.devkevin.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

public class InventoryListener implements Listener {
    private final Practice plugin;

    public InventoryListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.SPAWN || (playerData.getPlayerState() == PlayerState.EVENT && player.getItemInHand() != null && player.getItemInHand().getType() == Material.COMPASS)) {
                event.setCancelled(true);
            }
        }
    }
}
