package me.devkevin.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.util.StringUtil;

public class ResetStatsCommand extends Command {
    private final Practice plugin;

    public ResetStatsCommand() {
        super("reset");
        this.plugin = Practice.getInstance();
        this.setUsage(ChatColor.RED + "Usage: /reset [player]");
    }

    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            if (!player.hasPermission("practice.admin")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            }
        }
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /reset <player>");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            playerData.setElo(kit.getName(), 1000);
            playerData.setLosses(kit.getName(), 0);
            playerData.setWins(kit.getName(), 0);
        }
        commandSender.sendMessage(ChatColor.GREEN + target.getName() + "'s stats have been wiped.");
        return true;
    }
}
