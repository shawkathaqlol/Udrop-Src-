package me.devkevin.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.util.Clickable;

import java.util.UUID;

public class StatusEventCommand extends Command {
    private final Practice plugin;

    public StatusEventCommand() {
        super("status");
        this.plugin = Practice.getInstance();
        this.setDescription("Show an event or tournament status.");
        this.setUsage(ChatColor.RED + "Usage: /status");
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }
        if (this.plugin.getTournamentManager().getTournaments().size() == 0) {
            player.sendMessage(ChatColor.RED + "There is no available tournaments.");
            return true;
        }
        for (final Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
            if (tournament == null) {
                player.sendMessage(ChatColor.RED + "This tournament doesn't exist.");
                return true;
            }
            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW.toString() + "Tournament (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") " + ChatColor.GOLD.toString() + tournament.getKitName());
            if (tournament.getMatches().size() == 0) {
                player.sendMessage(ChatColor.RED + "There is no available matches.");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                return true;
            }
            for (final UUID matchUUID : tournament.getMatches()) {
                final Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                final MatchTeam teamA = match.getTeams().get(0);
                final MatchTeam teamB = match.getTeams().get(1);
                final String teamANames = (tournament.getTeamSize() > 1) ? (teamA.getLeaderName() + "'s Party") : teamA.getLeaderName();
                final String teamBNames = (tournament.getTeamSize() > 1) ? (teamB.getLeaderName() + "'s Party") : teamB.getLeaderName();
                final Clickable clickable = new Clickable(ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.GOLD.toString() + teamANames + " vs " + teamBNames + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "[Click to Spectate]", ChatColor.GRAY + "Click to spectate", "/spectate " + teamA.getLeaderName());
                clickable.sendToPlayer(player);
            }
            player.sendMessage(" ");
            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        }
        return true;
    }
}
