package me.devkevin.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.managers.PlayerManager;
import me.devkevin.practice.util.ItemUtil;

public class KitCommand extends Command {
    private static final String NO_KIT;
    private static final String NO_ARENA;

    static {
        NO_KIT = ChatColor.RED + "That kit doesn't exist!";
        NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    }

    private final Practice plugin;
    private PlayerManager playerManager;

    public KitCommand() {
        super("kit");
        this.plugin = Practice.getInstance();
        this.setDescription("Kit command.");
        this.setUsage(ChatColor.RED + "Usage: /kit <subcommand> [args]");
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
        final Kit kit = this.plugin.getKitManager().getKit(args[1]);
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
            case 1671308008:
                if (lowerCase.equals("disable")) {
                    n = 2;
                    break;
                }
                break;
            case -1298848381:
                if (lowerCase.equals("enable")) {
                    n = 3;
                    break;
                }
                break;
            case 94843278:
                if (lowerCase.equals("combo")) {
                    n = 4;
                    break;
                }
                break;
            case 3541892:
                if (lowerCase.equals("sumo")) {
                    n = 5;
                    break;
                }
                break;
            case 94094958:
                if (lowerCase.equals("build")) {
                    n = 6;
                    break;
                }
                break;
            case -895862857:
                if (lowerCase.equals("spleef")) {
                    n = 7;
                    break;
                }
                break;
            case -793195742:
                if (lowerCase.equals("parkour")) {
                    n = 8;
                    break;
                }
                break;
            case -938279477:
                if (lowerCase.equals("ranked")) {
                    n = 9;
                    break;
                }
                break;
            case 885730842:
                if (lowerCase.equals("excludearenafromallkitsbut")) {
                    n = 10;
                    break;
                }
                break;
            case -1353843347:
                if (lowerCase.equals("excludearena")) {
                    n = 11;
                    break;
                }
                break;
            case -1695541472:
                if (lowerCase.equals("whitelistarena")) {
                    n = 12;
                    break;
                }
                break;
            case 3226745:
                if (lowerCase.equals("icon")) {
                    n = 13;
                    break;
                }
                break;
            case -905779121:
                if (lowerCase.equals("setinv")) {
                    n = 14;
                    break;
                }
                break;
            case -1249328933:
                if (lowerCase.equals("getinv")) {
                    n = 15;
                    break;
                }
                break;
            case -578951419:
                if (lowerCase.equals("seteditinv")) {
                    n = 16;
                    break;
                }
                break;
            case 1779210641:
                if (lowerCase.equals("geteditinv")) {
                    n = 17;
                    break;
                }
                break;
            case 3198785:
                if (lowerCase.equals("help")) {
                    n = 18;
                    break;
                }
                break;
        }
        switch (n) {
            case 0:
                if (kit == null) {
                    this.plugin.getKitManager().createKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created kit " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ChatColor.RED + "That kit already exists!");
                break;
            case 1:
                if (kit != null) {
                    this.plugin.getKitManager().deleteKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted kit " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 2:
            case 3:
                if (kit != null) {
                    kit.setEnabled(!kit.isEnabled());
                    sender.sendMessage(kit.isEnabled() ? (ChatColor.GREEN + "Successfully enabled kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 4:
                if (kit != null) {
                    kit.setCombo(!kit.isCombo());
                    sender.sendMessage(kit.isCombo() ? (ChatColor.GREEN + "Successfully enabled combo mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled combo mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 5:
                if (kit != null) {
                    kit.setSumo(!kit.isSumo());
                    sender.sendMessage(kit.isSumo() ? (ChatColor.GREEN + "Successfully enabled sumo mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled sumo mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 6:
                if (kit != null) {
                    kit.setBuild(!kit.isBuild());
                    sender.sendMessage(kit.isBuild() ? (ChatColor.GREEN + "Successfully enabled build mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled build mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 7:
                if (kit != null) {
                    kit.setSpleef(!kit.isSpleef());
                    sender.sendMessage(kit.isSpleef() ? (ChatColor.GREEN + "Successfully enabled spleef mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled spleef mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 8:
                if (kit != null) {
                    kit.setParkour(!kit.isParkour());
                    sender.sendMessage(kit.isParkour() ? (ChatColor.GREEN + "Successfully enabled parkour mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled parkour mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 9:
                if (kit != null) {
                    kit.setRanked(!kit.isRanked());
                    sender.sendMessage(kit.isRanked() ? (ChatColor.GREEN + "Successfully enabled ranked mode for kit " + args[1] + ".") : (ChatColor.RED + "Successfully disabled ranked mode for kit " + args[1] + "."));
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 10:
                if (args.length < 2) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        for (final Kit loopKit : this.plugin.getKitManager().getKits()) {
                            if (!loopKit.equals(kit)) {
                                player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
                            }
                        }
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 11:
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.excludeArena(arena.getName());
                        sender.sendMessage(kit.getExcludedArenas().contains(arena.getName()) ? (ChatColor.GREEN + "Arena " + arena.getName() + " is now excluded from kit " + args[1] + ".") : (ChatColor.GREEN + "Arena " + arena.getName() + " is no longer excluded from kit " + args[1] + "."));
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 12:
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    final Arena arena = this.plugin.getArenaManager().getArena(args[2]);
                    if (arena != null) {
                        kit.whitelistArena(arena.getName());
                        sender.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ? (ChatColor.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + args[1] + ".") : (ChatColor.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + args[1] + "."));
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 13:
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getItemInHand().getType() != Material.AIR) {
                    final ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), ChatColor.GREEN + kit.getName());
                    kit.setIcon(icon);
                    sender.sendMessage(ChatColor.GREEN + "Successfully set icon for kit " + args[1] + ".");
                    break;
                }
                player.sendMessage(ChatColor.RED + "You must be holding an item to set the kit icon!");
                break;
            case 14:
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getGameMode() == GameMode.CREATIVE) {
                    sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                    break;
                }
                player.updateInventory();
                kit.setContents(player.getInventory().getContents());
                kit.setArmor(player.getInventory().getArmorContents());
                sender.sendMessage(ChatColor.GREEN + "Successfully set kit contents for " + args[1] + ".");
                break;
            case 15:
                if (kit != null) {
                    player.getInventory().setContents(kit.getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                    sender.sendMessage(ChatColor.GREEN + "Successfully retrieved kit contents from " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 16:
                if (kit == null) {
                    sender.sendMessage(KitCommand.NO_KIT);
                    break;
                }
                if (player.getGameMode() == GameMode.CREATIVE) {
                    sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                    break;
                }
                player.updateInventory();
                kit.setKitEditContents(player.getInventory().getContents());
                sender.sendMessage(ChatColor.GREEN + "Successfully set edit kit contents for " + args[1] + ".");
                break;
            case 17:
                if (kit != null) {
                    player.getInventory().setContents(kit.getKitEditContents());
                    player.updateInventory();
                    sender.sendMessage(ChatColor.GREEN + "Successfully retrieved edit kit contents from " + args[1] + ".");
                    break;
                }
                sender.sendMessage(KitCommand.NO_KIT);
                break;
            case 18:
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RED + "Kit Help");
                sender.sendMessage("");
                sender.sendMessage(ChatColor.RED + "/kit create <name> - Create Kit");
                sender.sendMessage(ChatColor.RED + "/kit delete <name> - Delete Kit");
                sender.sendMessage(ChatColor.RED + "/kit enable <name> - Enable Kit");
                sender.sendMessage(ChatColor.RED + "/kit disable <name> - Disable Kit");
                sender.sendMessage(ChatColor.RED + "/kit combo <name> - Combo Mode Kit");
                sender.sendMessage(ChatColor.RED + "/kit build <name> - Build Mode Kit");
                sender.sendMessage(ChatColor.RED + "/kit sumo <name> - Sumo Mode Kit");
                sender.sendMessage(ChatColor.RED + "/kit spleef <name> - Spleef Mode Kit");
                sender.sendMessage(ChatColor.RED + "/kit parkour <name> - Parkour Mode Kit");
                sender.sendMessage(ChatColor.RED + "/kit ranked <name> - Enable ranked matchs for Kit");
                sender.sendMessage(ChatColor.RED + "/kit whitelistarena <name> - Whitelist arena for Kit");
                sender.sendMessage(ChatColor.RED + "/kit icon <name> - Set Icon for Kit");
                sender.sendMessage(ChatColor.RED + "/kit setinv <name> - Set Inventory for Kit");
                sender.sendMessage("");
                break;
            default:
                sender.sendMessage(this.usageMessage);
                break;
        }
        return true;
    }
}
