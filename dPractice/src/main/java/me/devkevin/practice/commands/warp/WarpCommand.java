package me.devkevin.practice.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

public class WarpCommand extends Command {
    private final Practice plugin;

    public WarpCommand() {
        super("spawn");
        this.plugin = Practice.getInstance();
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "Usage: /spawn [args]");
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }
        if (args.length == 0) {
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            return true;
        }
        return true;
    }
}
