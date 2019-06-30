package me.devkevin.practice.commands.toggle;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;

import java.util.Arrays;

public class SettingsCommand extends Command {
    private final Practice plugin;

    public SettingsCommand() {
        super("settings");
        this.plugin = Practice.getInstance();
        this.setDescription("Toggles multiple settings.");
        this.setUsage(ChatColor.RED + "Usage: /settings");
        this.setAliases(Arrays.asList("options", "toggle"));
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        player.openInventory(playerData.getOptions().getInventory());
        return true;
    }
}
