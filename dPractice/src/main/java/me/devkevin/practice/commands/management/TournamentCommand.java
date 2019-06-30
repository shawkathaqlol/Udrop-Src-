package me.devkevin.practice.commands.management;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.util.Clickable;

public class TournamentCommand extends Command {
    private static final String[] HELP_ADMIN_MESSAGE;
    private static final String[] HELP_REGULAR_MESSAGE;

    static {
        HELP_ADMIN_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Tournament Commands:", ChatColor.GOLD + "(*) /tournament start " + ChatColor.GRAY + "- Start a Tournament", ChatColor.GOLD + "(*) /tournament stop " + ChatColor.GRAY + "- Stop a Tournament", ChatColor.GOLD + "(*) /tournament alert " + ChatColor.GRAY + "- Alert a Tournament", ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"};
        HELP_REGULAR_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Tournament Commands:", ChatColor.GOLD + "(*) /join <id> " + ChatColor.GRAY + "- Join a Tournament", ChatColor.GOLD + "(*) /leave " + ChatColor.GRAY + "- Leave a Tournament", ChatColor.GOLD + "(*) /status " + ChatColor.GRAY + "- Status of a Tournament", ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"};
    }

    private final Practice plugin;

    public TournamentCommand() {
        super("tournament");
        this.plugin = Practice.getInstance();
        this.setUsage(ChatColor.RED + "Usage: /tournament [args]");
    }

    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        final Player player = (Player) commandSender;
        if (args.length == 0) {
            commandSender.sendMessage(player.hasPermission("practice.admin") ? TournamentCommand.HELP_ADMIN_MESSAGE : TournamentCommand.HELP_REGULAR_MESSAGE);
            return true;
        }
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        final String lowerCase = args[0].toLowerCase();
        int n = -1;
        switch (lowerCase.hashCode()) {
            case 109757538:
                if (lowerCase.equals("start")) {
                    n = 0;
                    break;
                }
                break;
            case 3540994:
                if (lowerCase.equals("stop")) {
                    n = 1;
                    break;
                }
                break;
            case 92899676:
                if (lowerCase.equals("alert")) {
                    n = 2;
                    break;
                }
                break;
        }
        switch (n) {
            case 0:
                if (args.length == 5) {
                    try {
                        final int id = Integer.parseInt(args[1]);
                        final int teamSize = Integer.parseInt(args[3]);
                        final int size = Integer.parseInt(args[4]);
                        final String kitName = args[2];
                        if (size % teamSize != 0) {
                            commandSender.sendMessage(ChatColor.RED + "Tournament size & team sizes are invalid. Please try again.");
                            return true;
                        }
                        if (this.plugin.getTournamentManager().getTournament(Integer.valueOf(id)) != null) {
                            commandSender.sendMessage(ChatColor.RED + "This tournament already exists.");
                            return true;
                        }
                        final Kit kit = this.plugin.getKitManager().getKit(kitName);
                        if (kit == null) {
                            commandSender.sendMessage(ChatColor.RED + "That kit does not exist.");
                            return true;
                        }
                        this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
                    }
                    break;
                }
                commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
                break;
            case 1:
                if (args.length == 2) {
                    final int id = Integer.parseInt(args[1]);
                    final Tournament tournament = this.plugin.getTournamentManager().getTournament(Integer.valueOf(id));
                    if (tournament != null) {
                        this.plugin.getTournamentManager().removeTournament(id, true);
                        commandSender.sendMessage(ChatColor.RED + "Successfully removed tournament " + id + ".");
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "This tournament does not exist.");
                    }
                    break;
                }
                commandSender.sendMessage(ChatColor.RED + "Usage: /tournament stop <id>");
                break;
            case 2:
                if (args.length == 2) {
                    final int id = Integer.parseInt(args[1]);
                    final Tournament tournament = this.plugin.getTournamentManager().getTournament(Integer.valueOf(id));
                    if (tournament != null) {
                        final String toSend = ChatColor.YELLOW.toString() + "(Tournament) " + ChatColor.GREEN + " " + tournament.getKitName() + " (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") is starting soon. " + ChatColor.GRAY + "[Click to Join]";
                        final Clickable message = new Clickable(toSend, ChatColor.GRAY + "Click to join this tournament.", "/join " + id);
                        Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
                    }
                    break;
                }
                commandSender.sendMessage(ChatColor.RED + "Usage: /tournament alert <id>");
                break;
            default:
                commandSender.sendMessage(player.hasPermission("practice.admin") ? TournamentCommand.HELP_ADMIN_MESSAGE : TournamentCommand.HELP_REGULAR_MESSAGE);
                break;
        }
        return false;
    }
}
