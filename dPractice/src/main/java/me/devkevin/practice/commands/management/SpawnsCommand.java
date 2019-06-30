package me.devkevin.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;

public class SpawnsCommand extends Command {
    private final Practice plugin;

    public SpawnsCommand() {
        super("setspawn");
        this.plugin = Practice.getInstance();
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "Usage: /setspawn <subcommand>");
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
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final String lowerCase = args[0].toLowerCase();
        int n = -1;
        switch (lowerCase.hashCode()) {
            case -1575557072:
                if (lowerCase.equals("spawnlocation")) {
                    n = 0;
                    break;
                }
                break;
            case 2066201847:
                if (lowerCase.equals("spawnmin")) {
                    n = 1;
                    break;
                }
                break;
            case 2066201609:
                if (lowerCase.equals("spawnmax")) {
                    n = 2;
                    break;
                }
                break;
            case 1255015458:
                if (lowerCase.equals("editorlocation")) {
                    n = 3;
                    break;
                }
                break;
            case -1851297339:
                if (lowerCase.equals("editormin")) {
                    n = 4;
                    break;
                }
                break;
            case -1851297577:
                if (lowerCase.equals("editormax")) {
                    n = 5;
                    break;
                }
                break;
            case 1066657593:
                if (lowerCase.equals("sumolocation")) {
                    n = 6;
                    break;
                }
                break;
            case 1575442860:
                if (lowerCase.equals("sumofirst")) {
                    n = 7;
                    break;
                }
                break;
            case 1962122488:
                if (lowerCase.equals("sumosecond")) {
                    n = 8;
                    break;
                }
                break;
            case -1857569714:
                if (lowerCase.equals("sumomin")) {
                    n = 9;
                    break;
                }
                break;
            case -1857569952:
                if (lowerCase.equals("sumomax")) {
                    n = 10;
                    break;
                }
                break;
            case -522070914:
                if (lowerCase.equals("oitclocation")) {
                    n = 11;
                    break;
                }
                break;
            case -1450059799:
                if (lowerCase.equals("oitcmin")) {
                    n = 12;
                    break;
                }
                break;
            case -1450060037:
                if (lowerCase.equals("oitcmax")) {
                    n = 13;
                    break;
                }
                break;
            case 1644049077:
                if (lowerCase.equals("oitcspawnpoints")) {
                    n = 14;
                    break;
                }
                break;
            case -14407465:
                if (lowerCase.equals("parkourlocation")) {
                    n = 15;
                    break;
                }
                break;
            case -2030239287:
                if (lowerCase.equals("parkourgamelocation")) {
                    n = 16;
                    break;
                }
                break;
            case 815820546:
                if (lowerCase.equals("parkourmax")) {
                    n = 17;
                    break;
                }
                break;
            case 815820784:
                if (lowerCase.equals("parkourmin")) {
                    n = 18;
                    break;
                }
                break;
            case 666183946:
                if (lowerCase.equals("redroverlocation")) {
                    n = 19;
                    break;
                }
                break;
            case 68997499:
                if (lowerCase.equals("redroverfirst")) {
                    n = 20;
                    break;
                }
                break;
            case -1788010743:
                if (lowerCase.equals("redroversecond")) {
                    n = 21;
                    break;
                }
                break;
            case -1698243619:
                if (lowerCase.equals("redrovermin")) {
                    n = 22;
                    break;
                }
                break;
            case -1698243857:
                if (lowerCase.equals("redrovermax")) {
                    n = 23;
                    break;
                }
                break;
        }
        switch (n) {
            case 0:
                this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the spawn location.");
                break;
            case 1:
                this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the spawn min.");
                break;
            case 2:
                this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the spawn max.");
                break;
            case 3:
                this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the editor location.");
                break;
            case 4:
                this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the editor min.");
                break;
            case 5:
                this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the editor max.");
                break;
            case 6:
                this.plugin.getSpawnManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location.");
                break;
            case 7:
                this.plugin.getSpawnManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location A.");
                break;
            case 8:
                this.plugin.getSpawnManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location B.");
                break;
            case 9:
                this.plugin.getSpawnManager().setSumoMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the sumo min.");
                break;
            case 10:
                this.plugin.getSpawnManager().setSumoMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the sumo max.");
                break;
            case 11:
                this.plugin.getSpawnManager().setOitcLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the OITC location.");
                break;
            case 12:
                this.plugin.getSpawnManager().setOitcMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the OITC min.");
                break;
            case 13:
                this.plugin.getSpawnManager().setOitcMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the OITC max.");
                break;
            case 14:
                this.plugin.getSpawnManager().getOitcSpawnpoints().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the OITC spawn-point #" + this.plugin.getSpawnManager().getOitcSpawnpoints().size() + ".");
                break;
            case 15:
                this.plugin.getSpawnManager().setParkourLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the parkour location.");
                break;
            case 16:
                this.plugin.getSpawnManager().setParkourGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the parkour Game location.");
                break;
            case 17:
                this.plugin.getSpawnManager().setParkourMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the parkour max location.");
                break;
            case 18:
                this.plugin.getSpawnManager().setParkourMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the parkour min location.");
                break;
            case 19:
                this.plugin.getSpawnManager().setRedroverLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location.");
                break;
            case 20:
                this.plugin.getSpawnManager().setRedroverFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location A.");
                break;
            case 21:
                this.plugin.getSpawnManager().setRedroverSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location B.");
                break;
            case 22:
                this.plugin.getSpawnManager().setRedroverMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the redrover min.");
                break;
            case 23:
                this.plugin.getSpawnManager().setRedroverMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(ChatColor.GREEN + "Successfully set the redrover max.");
                break;
        }
        return false;
    }
}
