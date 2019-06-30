package me.devkevin.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;

public class EventManagerCommand extends Command {
    private final Practice plugin;

    public EventManagerCommand() {
        super("eventmanager");
        this.plugin = Practice.getInstance();
        this.setDescription("Manage an event.");
        this.setUsage(ChatColor.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
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
        if (args.length < 2) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final String action = args[0];
        final String eventName = args[1];
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(ChatColor.RED + "That event doesn't exist.");
            return true;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
            event.getCountdownTask().setTimeUntilStart(5);
            player.sendMessage(ChatColor.RED + "Event was force started.");
        } else if (action.toUpperCase().equalsIgnoreCase("END") && event.getState() == EventState.STARTED) {
            event.end();
            player.sendMessage(ChatColor.RED + "Event was cancelled.");
        } else if (action.toUpperCase().equalsIgnoreCase("STATUS")) {
            final String[] message = {ChatColor.YELLOW + "Event: " + ChatColor.WHITE + event.getName(), ChatColor.YELLOW + "Host: " + ChatColor.WHITE + ((event.getHost() == null) ? "Player Left" : event.getHost().getName()), ChatColor.YELLOW + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(), ChatColor.YELLOW + "State: " + ChatColor.WHITE + event.getState().name()};
            player.sendMessage(message);
        } else if (action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {
            this.plugin.getEventManager().setCooldown(0L);
            player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
        } else {
            player.sendMessage(this.usageMessage);
        }
        return true;
    }
}
