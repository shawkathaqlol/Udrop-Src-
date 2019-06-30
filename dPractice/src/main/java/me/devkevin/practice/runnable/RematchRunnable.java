package me.devkevin.practice.runnable;

import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

import java.util.UUID;

public class RematchRunnable implements Runnable {
    private final Practice plugin;
    private final UUID playerUUID;

    public RematchRunnable(final UUID playerUUID) {
        this.plugin = Practice.getInstance();
        this.playerUUID = playerUUID;
    }

    @Override
    public void run() {
        final Player player = this.plugin.getServer().getPlayer(this.playerUUID);
        if (player != null) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData != null && playerData.getPlayerState() == PlayerState.SPAWN && this.plugin.getMatchManager().isRematching(player.getUniqueId()) && this.plugin.getPartyManager().getParty(player.getUniqueId()) == null) {
                player.getInventory().setItem(3, null);
                player.updateInventory();
                playerData.setRematchID(-1);
            }
            this.plugin.getMatchManager().removeRematch(this.playerUUID);
        }
    }
}
