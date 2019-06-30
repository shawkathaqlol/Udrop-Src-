package me.devkevin.practice.board;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.cache.StatusCache;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.settings.item.ProfileOptionsItemState;
import me.devkevin.practice.util.PlayerUtil;
import me.devkevin.practice.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PracticeBoard implements BoardAdapter {

    @Getter
    private final Practice plugin;

    public PracticeBoard() {
        this.plugin = Practice.getInstance ();
    }

    public String getTitle(final Player player) {
        return ChatColor.WHITE.toString () + ChatColor.BOLD + "uDrop";
    }

    public List <String> getScoreboard(final Player player , final Board board , final Set <BoardCooldown> cooldowns) {
        final PlayerData playerData = this.plugin.getPlayerManager ().getPlayerData ( player.getUniqueId () );
        if (playerData == null) {
            this.plugin.getLogger ().warning ( player.getName () + "'s player data is null" );
            return null;
        }
        if (playerData.getOptions ().getScoreboard () == ProfileOptionsItemState.DISABLED) {
            return null;
        }
        switch (playerData.getPlayerState ()) {
            case LOADING:
            case EDITING:
            case FFA:
            case SPAWN:
            case EVENT:
            case SPECTATING:
                return this.getLobbyBoard ( player , false );
            case QUEUE:
                return this.getLobbyBoard ( player , true );
            case FIGHTING:
                return this.getGameBoard ( player );
            default:
                return null;
        }
    }


    private List <String> getLobbyBoard(final Player player , final boolean queuing) {
        final List <String> strings = new LinkedList <String> ();
        strings.add ( ChatColor.GRAY.toString () + ChatColor.STRIKETHROUGH + "-------------------" );
        final Party party = this.plugin.getPartyManager ().getParty ( player.getUniqueId () );
        PracticeEvent event = this.plugin.getEventManager ().getEventPlaying ( player );
        if (this.plugin.getEventManager ().getSpectators ().containsKey ( player.getUniqueId () )) {
            event = this.plugin.getEventManager ().getSpectators ().get ( player.getUniqueId () );
        }
        final PlayerData playerData = this.plugin.getPlayerManager ().getPlayerData ( player.getUniqueId () );
        if (event == null) {
            strings.add ( ChatColor.GREEN.toString () + "Online§f: " + ChatColor.WHITE + this.plugin.getServer ().getOnlinePlayers ().size () );
            strings.add ( ChatColor.GREEN.toString () + "In Fights§f: " + ChatColor.WHITE + StatusCache.getInstance ().getFighting () );
            strings.add ( ChatColor.GREEN.toString () + "In Queues§f: " + ChatColor.WHITE + StatusCache.getInstance ().getQueueing () );
            strings.add ("");
            strings.add ( ChatColor.WHITE.toString () + "eu.udrop.club");
        }
        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        return strings;
    }

    private List<String> getGameBoard(final Player player) {
        final List<String> strings = new LinkedList<String>();
        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
        Player opponentPlayer = null;
        if (!match.isPartyMatch() && !match.isFFA()) {
            opponentPlayer = ((match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()) ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0)) : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0)));
            if (opponentPlayer == null) {
                return this.getLobbyBoard(player, false);
            }
            strings.add(ChatColor.GREEN.toString() + "Opponent§f: " + ChatColor.WHITE + opponentPlayer.getName());
            strings.add(ChatColor.GREEN.toString() + "Ping§f: " + ChatColor.WHITE + PlayerUtil.getPing(player) + " ms" + ChatColor.GRAY + " - " + ChatColor.WHITE + PlayerUtil.getPing(opponentPlayer) + " ms");
        } else if (match.isPartyMatch() && !match.isFFA()) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            final MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) : ((playerData.getTeamID() == 0) ? match.getTeams().get(1) : match.getTeams().get(0));
            final MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());
        } else if (match.isFFA()) {
            final int alive = match.getTeams().get(0).getAlivePlayers().size() - 1;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData != null && playerData.getOptions().getScoreboard() == ProfileOptionsItemState.SHOW_PING) {
            strings.add("");
            if (opponentPlayer != null && !match.isPartyMatch() && !match.isFFA()) {
                final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponentPlayer.getUniqueId());
                if (opponentData != null) {
                }
            }
        }
        strings.add ( ChatColor.WHITE.toString () + "eu.udrop.club");
        strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
        return strings;
    }
}


