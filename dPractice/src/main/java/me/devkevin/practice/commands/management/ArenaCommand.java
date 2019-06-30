package me.devkevin.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.runnable.ArenaCommandRunnable;

public class ArenaCommand extends Command {
    private static final String NO_ARENA;

    static {
        NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    }

    private final Practice plugin;

    public ArenaCommand() {
        super("arena");
        this.plugin = Practice.getInstance();
        this.setDescription("Arenas command.");
        this.setUsage(ChatColor.RED + "Usage: /arena <subcommand> [args]");
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
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Arena arena = this.plugin.getArenaManager().getArena(args[1]);
        final String lowerCase = args[0].toLowerCase();
        int n = -1;
        switch (lowerCase.hashCode()) {
            case -1352294148:
                if (lowerCase.equals("create")) {
                    n = 0;
                    break;
                }
                break;
            case -1335458389:
                if (lowerCase.equals("delete")) {
                    n = 1;
                    break;
                }
                break;
            case 97:
                if (lowerCase.equals("a")) {
                    n = 2;
                    break;
                }
                break;
            case 98:
                if (lowerCase.equals("b")) {
                    n = 3;
                    break;
                }
                break;
            case 108114:
                if (lowerCase.equals("min")) {
                    n = 4;
                    break;
                }
                break;
            case 107876:
                if (lowerCase.equals("max")) {
                    n = 5;
                    break;
                }
                break;
            case 1671308008:
                if (lowerCase.equals("disable")) {
                    n = 6;
                    break;
                }
                break;
            case -1298848381:
                if (lowerCase.equals("enable")) {
                    n = 7;
                    break;
                }
                break;
            case 1810371957:
                if (lowerCase.equals("generate")) {
                    n = 8;
                    break;
                }
                break;
            case 3522941:
                if (lowerCase.equals("save")) {
                    n = 9;
                    break;
                }
                break;
            case -1081434779:
                if (lowerCase.equals("manage")) {
                    n = 10;
                    break;
                }
                break;
            case 3198785:
                if (lowerCase.equals("help")) {
                    n = 11;
                    break;
                }
                break;
        }
        switch (n) {
            case 0:
                if (arena == null) {
                    this.plugin.getArenaManager().createArena(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ChatColor.RED + "That arena already exists!");
                break;
            case 1:
                if (arena != null) {
                    this.plugin.getArenaManager().deleteArena(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 2:
                if (arena != null) {
                    final Location location = player.getLocation();
                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX((double) location.getBlockX() + 0.5);
                        location.setY((double) location.getBlockY() + 3.0);
                        location.setZ((double) location.getBlockZ() + 0.5);
                    }
                    arena.setA(CustomLocation.fromBukkitLocation(location));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set position A for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 3:
                if (arena != null) {
                    final Location location = player.getLocation();
                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX((double) location.getBlockX() + 0.5);
                        location.setY((double) location.getBlockY() + 3.0);
                        location.setZ((double) location.getBlockZ() + 0.5);
                    }
                    arena.setB(CustomLocation.fromBukkitLocation(location));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set position B for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 4:
                if (arena != null) {
                    arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 5:
                if (arena != null) {
                    arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 6:
            case 7:
                if (arena != null) {
                    arena.setEnabled(!arena.isEnabled());
                    sender.sendMessage(arena.isEnabled() ? (ChatColor.GREEN + "Successfully enabled arena " + args[1] + ".") : (ChatColor.RED + "Successfully disabled arena " + args[1] + "."));
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            case 8:
                if (args.length == 3) {
                    final int arenas = Integer.parseInt(args[2]);
                    this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                    this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                    break;
                }
                sender.sendMessage(ChatColor.RED + "Usage: /arena generate <arena> <arenas>");
                break;
            case 9:
                this.plugin.getArenaManager().reloadArenas();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the arenas.");
                break;
            case 10:
                this.plugin.getArenaManager().openArenaSystemUI(player);
                break;
            case 11:
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RED + "Arena Help");
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RED + "/arena create <name> - Create Arena");
                sender.sendMessage(ChatColor.RED + "/arena delete <name> - Delete Arena");
                sender.sendMessage(ChatColor.RED + "/arena a <name> - Set A position for Arena");
                sender.sendMessage(ChatColor.RED + "/arena b <name> - Set B position for Arena");
                sender.sendMessage(ChatColor.RED + "/arena min <name> - Set bottom corner location");
                sender.sendMessage(ChatColor.RED + "/arena max <name> - Set top corner location");
                sender.sendMessage(ChatColor.RED + "/arena enable <name> - Enable Arena");
                sender.sendMessage(ChatColor.RED + "/arena disable <name> - Disable Arena");
                sender.sendMessage(ChatColor.RED + "/arena generate <name> - Generate Arena");
                sender.sendMessage(ChatColor.RED + "/arena save <name> - Save Arena");
                sender.sendMessage(ChatColor.RED + "/arena manage <name> - Open Arena GUI");
                sender.sendMessage("");
                break;
            default:
                sender.sendMessage(this.usageMessage);
                break;
        }
        return true;
    }
}
