package me.devkevin.practice.commands.duel;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.parkour.ParkourEvent;
import me.devkevin.practice.events.redrover.RedroverEvent;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.StringUtil;

import java.util.Arrays;

public class SpectateCommand extends Command {
    private final Practice plugin;

    public SpectateCommand() {
        super("spectate");
        this.plugin = Practice.getInstance();
        this.setDescription("Spectate a player's match.");
        this.setUsage(ChatColor.RED + "Usage: /spectate <player>");
        this.setAliases(Arrays.asList("spec"));
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
        final Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
        if (targetData.getPlayerState() == PlayerState.EVENT) {
            final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);
            if (event == null) {
                player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
                return true;
            }
            if (event instanceof SumoEvent) {
                player.performCommand("eventspectate Sumo");
            } else if (event instanceof OITCEvent) {
                player.performCommand("eventspectate OITC");
            } else if (event instanceof ParkourEvent) {
                player.performCommand("eventspectate Parkour");
            } else if (event instanceof RedroverEvent) {
                player.performCommand("eventspectate Redrover");
            }
            return true;
        } else {
            if (targetData.getPlayerState() != PlayerState.FIGHTING) {
                player.sendMessage(ChatColor.RED + "That player is currently not in a fight.");
                return true;
            }
            final Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
            if (!targetMatch.isParty()) {
                if (!targetData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                    player.sendMessage(ChatColor.RED + "That player has ignored spectators.");
                    return true;
                }
                final MatchTeam team = targetMatch.getTeams().get(0);
                final MatchTeam team2 = targetMatch.getTeams().get(1);
                final PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData((team.getPlayers().get(0) == target.getUniqueId()) ? team2.getPlayers().get(0) : team.getPlayers().get(0));
                if (otherPlayerData != null && !otherPlayerData.getOptions().isSpectators() && !player.hasPermission("practice.staff")) {
                    player.sendMessage(ChatColor.RED + "That player has ignored spectators.");
                    return true;
                }
            }
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                final Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
                if (match.equals(targetMatch)) {
                    player.sendMessage(ChatColor.RED + "You are already spectating this player.");
                    return true;
                }
                match.removeSpectator(player.getUniqueId());
            }
            player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + target.getName() + ChatColor.GREEN + ".");
            this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
            return true;
        }
    }
}
