package me.devkevin.practice.commands.time;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.settings.item.ProfileOptionsItemState;

public class DayCommand extends Command {
    public DayCommand() {
        super("day");
        this.setDescription("Set player time to day.");
        this.setUsage(ChatColor.RED + "Usage: /day");
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        ((Player) sender).setPlayerTime(6000L, true);
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(((Player) sender).getUniqueId());
        playerData.getOptions().setTime(ProfileOptionsItemState.DAY);
        return true;
    }
}
