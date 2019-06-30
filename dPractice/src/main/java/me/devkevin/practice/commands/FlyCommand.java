package me.devkevin.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

import java.util.Arrays;

public class FlyCommand extends Command {
    private final Practice plugin;

    public FlyCommand() {
        super("fly");
        this.plugin = Practice.getInstance();
        this.setDescription("Toggles flight.");
        this.setUsage(ChatColor.RED + "Usage: /fly");
        this.setAliases(Arrays.asList("flight"));
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if (!player.hasPermission("practice.fly")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }
        player.setAllowFlight(!player.getAllowFlight());
        if (player.getAllowFlight()) {
            player.sendMessage(ChatColor.YELLOW + "Your flight has been enabled.");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Your flight has been disabled.");
        }
        return true;
    }
}
