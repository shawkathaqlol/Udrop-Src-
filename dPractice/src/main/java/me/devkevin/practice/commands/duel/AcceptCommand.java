package me.devkevin.practice.commands.duel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.managers.PartyManager;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchRequest;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AcceptCommand extends Command {
    private final Practice plugin;

    public AcceptCommand() {
        super("accept");
        this.plugin = Practice.getInstance();
        this.setDescription("Accept a player's duel.");
        this.setUsage(ChatColor.RED + "Usage: /accept <player>");
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Unable to accept a duel within your duel.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (player.getName().equals(target.getName())) {
            player.sendMessage(ChatColor.RED + "You can't duel yourself.");
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "That player is currently busy.");
            return true;
        }
        MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());
        if (args.length > 1) {
            final Kit kit = this.plugin.getKitManager().getKit(args[1]);
            if (kit != null) {
                request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
            }
        }
        if (request == null) {
            player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
            return true;
        }
        if (request.getRequester().equals(target.getUniqueId())) {
            final List<UUID> playersA = new ArrayList<UUID>();
            final List<UUID> playersB = new ArrayList<UUID>();
            final PartyManager partyManager = this.plugin.getPartyManager();
            final Party party = partyManager.getParty(player.getUniqueId());
            final Party targetParty = partyManager.getParty(target.getUniqueId());
            if (request.isParty()) {
                if (party == null || targetParty == null || !partyManager.isLeader(target.getUniqueId()) || !partyManager.isLeader(target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "That player is not a party leader.");
                    return true;
                }
                playersA.addAll(party.getMembers());
                playersB.addAll(targetParty.getMembers());
            } else {
                if (party != null || targetParty != null) {
                    player.sendMessage(ChatColor.RED + "That player is already in a party.");
                    return true;
                }
                playersA.add(player.getUniqueId());
                playersB.add(target.getUniqueId());
            }
            final Kit kit2 = this.plugin.getKitManager().getKit(request.getKitName());
            final MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
            final MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);
            final Match match = new Match(request.getArena(), kit2, QueueType.UNRANKED, teamA, teamB);
            final Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
            final Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
            final String teamMatch = match.isPartyMatch() ? "'s Party" : "";
            match.broadcast(ChatColor.YELLOW + "Starting duel match. " + ChatColor.GREEN + "(" + leaderA.getName() + teamMatch + " vs " + leaderB.getName() + teamMatch + ")");
            this.plugin.getMatchManager().createMatch(match);
        }
        return true;
    }
}
