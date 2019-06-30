package me.devkevin.practice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.StringUtil;

import java.util.*;

public class PartyCommand extends Command {
    private static final String NOT_LEADER;
    private static final String[] HELP_MESSAGE;

    static {
        NOT_LEADER = ChatColor.RED + "You are not the leader of the party!";
        HELP_MESSAGE = new String[]{ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Commands:", ChatColor.GOLD + "(*) /party help " + ChatColor.GRAY + "- Displays the help menu", ChatColor.GOLD + "(*) /party create " + ChatColor.GRAY + "- Creates a party instance", ChatColor.GOLD + "(*) /party leave " + ChatColor.GRAY + "- Leave your current party", ChatColor.GOLD + "(*) /party info " + ChatColor.GRAY + "- Displays your party information", ChatColor.GOLD + "(*) /party join (player) " + ChatColor.GRAY + "- Join a party (invited or unlocked)", "", ChatColor.RED + "Leader Commands:", ChatColor.GOLD + "(*) /party open " + ChatColor.GRAY + "- Open your party for others to join", ChatColor.GOLD + "(*) /party lock " + ChatColor.GRAY + "- Lock your party for others to join", ChatColor.GOLD + "(*) /party setlimit (amount) " + ChatColor.GRAY + "- Set a limit to your party", ChatColor.GOLD + "(*) /party invite (player) " + ChatColor.GRAY + "- Invites a player to your party", ChatColor.GOLD + "(*) /party kick (player) " + ChatColor.GRAY + "- Kicks a player from your party", ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"};
    }

    private final Practice plugin;

    public PartyCommand() {
        super("party");
        this.plugin = Practice.getInstance();
        this.setDescription("Party Command.");
        this.setUsage(ChatColor.RED + "Usage: /party <subcommand> [player]");
        this.setAliases(Collections.singletonList("p"));
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final String subCommand = (args.length < 1) ? "help" : args[0];
        final String lowerCase = subCommand.toLowerCase();
        int n = -1;
        switch (lowerCase.hashCode()) {
            case -1352294148:
                if (lowerCase.equals("create")) {
                    n = 0;
                    break;
                }
                break;
            case 102846135:
                if (lowerCase.equals("leave")) {
                    n = 1;
                    break;
                }
                break;
            case 104433:
                if (lowerCase.equals("inv")) {
                    n = 2;
                    break;
                }
                break;
            case -1183699191:
                if (lowerCase.equals("invite")) {
                    n = 3;
                    break;
                }
                break;
            case -1423461112:
                if (lowerCase.equals("accept")) {
                    n = 4;
                    break;
                }
                break;
            case 3267882:
                if (lowerCase.equals("join")) {
                    n = 5;
                    break;
                }
                break;
            case 3291718:
                if (lowerCase.equals("kick")) {
                    n = 6;
                    break;
                }
                break;
            case 1427242137:
                if (lowerCase.equals("setlimit")) {
                    n = 7;
                    break;
                }
                break;
            case 3417674:
                if (lowerCase.equals("open")) {
                    n = 8;
                    break;
                }
                break;
            case 3327275:
                if (lowerCase.equals("lock")) {
                    n = 9;
                    break;
                }
                break;
            case 3237038:
                if (lowerCase.equals("info")) {
                    n = 10;
                    break;
                }
                break;
        }
        switch (n) {
            case 0:
                if (party != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a party.");
                    break;
                }
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    break;
                }
                this.plugin.getPartyManager().createParty(player);
                break;
            case 1:
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    break;
                }
                this.plugin.getPartyManager().leaveParty(player);
                break;
            case 2:
            case 3: {
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
                    break;
                }
                if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
                    break;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party invite (player)");
                    break;
                }
                if (party.isOpen()) {
                    player.sendMessage(ChatColor.RED + "This party is open, so anyone can join.");
                    break;
                }
                if (party.getMembers().size() >= party.getLimit()) {
                    player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                    break;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(PartyCommand.NOT_LEADER);
                    return true;
                }
                final Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                    return true;
                }
                final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (target.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(ChatColor.RED + "You can't invite yourself.");
                } else if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
                    player.sendMessage(ChatColor.RED + "That player is already in a party.");
                } else if (targetData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "That player is currently busy.");
                } else if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You have already sent a party invitation to this player, please wait.");
                } else {
                    this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());
                    final Clickable partyInvite = new Clickable(ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " has invited you to their party! " + ChatColor.GRAY + "[Click to Accept]", ChatColor.GRAY + "Click to accept", "/party accept " + sender.getName());
                    partyInvite.sendToPlayer(target);
                    party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + target.getName() + " has been invited to the party.");
                }
                break;
            }
            case 4: {
                if (party != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a party.");
                    break;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party accept <player>.");
                    break;
                }
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    break;
                }
                final Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                    return true;
                }
                final Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                if (targetParty == null) {
                    player.sendMessage(ChatColor.RED + "That player is not in a party.");
                } else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
                    player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                } else if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
                    player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
                } else {
                    this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                }
                break;
            }
            case 5: {
                if (party != null) {
                    player.sendMessage(ChatColor.RED + "You are already in a party.");
                    break;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party join <player>.");
                    break;
                }
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    break;
                }
                final Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                    return true;
                }
                final Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
                    player.sendMessage(ChatColor.RED + "You can't join this party.");
                } else {
                    this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
                }
                break;
            }
            case 6: {
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party kick <player>.");
                    break;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(PartyCommand.NOT_LEADER);
                    return true;
                }
                final Player target = this.plugin.getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                    return true;
                }
                final Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
                if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
                    player.sendMessage(ChatColor.RED + "That player is not in your party.");
                } else {
                    this.plugin.getPartyManager().leaveParty(target);
                }
                break;
            }
            case 7:
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /party setlimit <amount>.");
                    break;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(PartyCommand.NOT_LEADER);
                    return true;
                }
                try {
                    final int limit = Integer.parseInt(args[1]);
                    if (limit < 2 || limit > 50) {
                        player.sendMessage(ChatColor.RED + "That is not a valid limit.");
                    } else {
                        party.setLimit(limit);
                        player.sendMessage(ChatColor.GREEN + "You have set the party player limit to " + ChatColor.YELLOW + limit + " players.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "That is not a number.");
                }
                break;
            case 8:
            case 9:
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                if (party.getLeader() != player.getUniqueId()) {
                    player.sendMessage(PartyCommand.NOT_LEADER);
                    return true;
                }
                party.setOpen(!party.isOpen());
                party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + "Your party is now " + ChatColor.BOLD + (party.isOpen() ? "OPEN" : "LOCKED"));
                break;
            case 10: {
                if (party == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a party.");
                    break;
                }
                final List<UUID> members = new ArrayList<>(party.getMembers());
                members.remove(party.getLeader());
                final StringBuilder builder = new StringBuilder(ChatColor.GOLD + "Members (" + party.getMembers().size() + "): ");
                members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(ChatColor.GRAY).append(member.getName()).append(","));
                final String[] information = {ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Information:", ChatColor.GOLD + "Leader: " + ChatColor.GRAY + this.plugin.getServer().getPlayer(party.getLeader()).getName(), ChatColor.GOLD + builder.toString(), ChatColor.GOLD + "Party State: " + ChatColor.GRAY + (party.isOpen() ? "Open" : "Locked"), ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"};
                player.sendMessage(information);
                break;
            }
            default:
                player.sendMessage(PartyCommand.HELP_MESSAGE);
                break;
        }
        return true;
    }
}
