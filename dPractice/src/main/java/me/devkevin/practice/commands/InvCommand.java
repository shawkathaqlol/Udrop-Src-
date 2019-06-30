package me.devkevin.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.inventory.InventorySnapshot;

import java.util.UUID;
import java.util.regex.Pattern;

public class InvCommand extends Command {
    private static final Pattern UUID_PATTERN;
    private static final String INVENTORY_NOT_FOUND;

    static {
        UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
        INVENTORY_NOT_FOUND = ChatColor.RED + "Cannot find the requested inventory. Maybe it expired?";
    }

    private final Practice plugin;

    public InvCommand() {
        super("inventory");
        this.plugin = Practice.getInstance();
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length == 0) {
            return true;
        }
        if (!args[0].matches(InvCommand.UUID_PATTERN.pattern())) {
            sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
            return true;
        }
        final InventorySnapshot snapshot = this.plugin.getInventoryManager().getSnapshot(UUID.fromString(args[0]));
        if (snapshot == null) {
            sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
        } else {
            ((Player) sender).openInventory(snapshot.getInventoryUI().getCurrentPage());
        }
        return true;
    }
}
