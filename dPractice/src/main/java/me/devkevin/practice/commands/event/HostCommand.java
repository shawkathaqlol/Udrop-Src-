package me.devkevin.practice.commands.event;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.util.Clickable;

public class HostCommand extends Command {
    private final Practice plugin;

    public HostCommand() {
        super("host");
        this.plugin = Practice.getInstance();
        this.setDescription("Host an event.");
        this.setUsage(ChatColor.RED + "Usage: /host <event>");
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if (!player.hasPermission("practice.host")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final String eventName = args[0];
        if (eventName == null) {
            return true;
        }
        if (this.plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(ChatColor.RED + "That event doesn't exist.");
            player.sendMessage(ChatColor.RED + "Available events: Sumo, OITC, Parkour, Redrover");
            return true;
        }
        if (eventName.toUpperCase().equalsIgnoreCase("REDROVER")) {
            player.sendMessage(ChatColor.RED + "This event is currently disabled.");
            return true;
        }
        if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
            player.sendMessage(ChatColor.RED + "There is a cooldown. Event can't start at this moment.");
            return true;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
        if (event.getState() != EventState.UNANNOUNCED) {
            player.sendMessage(ChatColor.RED + "There is currently an active event.");
            return true;
        }
        final boolean eventBeingHosted = this.plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
        if (eventBeingHosted) {
            player.sendMessage(ChatColor.RED + "There is currently an active event.");
            return true;
        }
        final String toSend = ChatColor.YELLOW.toString() + ChatColor.BOLD + "(Event) " + ChatColor.GREEN + "" + event.getName() + " is starting soon. " + ChatColor.GRAY + "[Join]";
        final Clickable message = new Clickable(toSend, ChatColor.GRAY + "Click to join this event.", "/join " + event.getName());
        this.plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
        event.setLimit(50);
        if (args.length == 2 && player.hasPermission("practice.host.unlimited")) {
            if (!NumberUtils.isNumber(args[1])) {
                player.sendMessage(ChatColor.RED + "That's not a correct amount.");
                return true;
            }
            event.setLimit(Integer.parseInt(args[1]));
        }
        Practice.getInstance().getEventManager().hostEvent(event, player);
        return true;
    }
}
