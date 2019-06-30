package me.devkevin.practice.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.event.match.MatchEndEvent;
import me.devkevin.practice.event.match.MatchStartEvent;
import me.devkevin.practice.inventory.InventorySnapshot;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.runnable.MatchRunnable;
import me.devkevin.practice.util.CC;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.EloUtil;
import me.devkevin.practice.util.PlayerUtil;

import java.util.*;

public class MatchListener implements Listener {
    private final Practice plugin;

    public MatchListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler
    public void onMatchStart(final MatchStartEvent event) {
        final Match match = event.getMatch();
        final Kit kit = match.getKit();
        if (!kit.isEnabled()) {
            match.broadcast(ChatColor.RED + "This kit is currently disabled.");
            this.plugin.getMatchManager().removeMatch(match);
            return;
        }
        if (kit.isBuild() || kit.isSpleef()) {
            if (match.getArena().getAvailableArenas().size() <= 0) {
                match.broadcast(ChatColor.RED + "There are no arenas available.");
                this.plugin.getMatchManager().removeMatch(match);
                return;
            }
            match.setStandaloneArena(match.getArena().getAvailableArena());
            this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
        }
        final Set<Player> matchPlayers = new HashSet<>();
        match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
            matchPlayers.add(player);
            this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
            playerData.setCurrentMatchID(match.getMatchId());
            playerData.setTeamID(team.getTeamID());
            playerData.setMissedPots(0);
            playerData.setLongestCombo(0);
            playerData.setCombo(0);
            playerData.setHits(0);
            PlayerUtil.clearPlayer(player);
            CustomLocation locationA = ((match.getStandaloneArena() != null) ? match.getStandaloneArena().getA() : match.getArena().getA());
            CustomLocation locationB = ((match.getStandaloneArena() != null) ? match.getStandaloneArena().getB() : match.getArena().getB());
            player.teleport((team.getTeamID() == 1) ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
            if (kit.isBuild()) {
            }
            if (kit.isCombo()) {
                player.setMaximumNoDamageTicks(3);
            }
            if (!match.isRedrover()) {
                this.plugin.getMatchManager().giveKits(player, kit);
                playerData.setPlayerState(PlayerState.FIGHTING);
            } else {
                this.plugin.getMatchManager().addRedroverSpectator(player, match);
            }
        }));
        for (final Player player2 : matchPlayers) {
            for (final Player online : this.plugin.getServer().getOnlinePlayers()) {
                online.hidePlayer(player2);
                player2.hidePlayer(online);
            }
        }
        for (final Player player2 : matchPlayers) {
            for (final Player other : matchPlayers) {
                player2.showPlayer(other);
            }
        }
        new MatchRunnable(match).runTaskTimer(this.plugin, 20L, 20L);
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Match match = event.getMatch();
        Clickable inventories = new Clickable(CC.GOLD + "Inventories (click to view): ");

        match.setMatchState(MatchState.ENDING);
        match.setWinningTeamId(event.getWinningTeam().getTeamID());
        match.setCountdown(4);

        if (match.isFFA()) {
            Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
            String winnerMessage = CC.PRIMARY + "Winner: " + CC.SECONDARY + winner.getName();

            event.getWinningTeam().players().forEach(player -> {
                if (!match.hasSnapshot(player.getUniqueId())) {
                    match.addSnapshot(player);
                }
                inventories.add((player.getUniqueId() == winner.getUniqueId() ? CC.YELLOW : CC.YELLOW)
                                + player.getName() + " ",
                        CC.PRIMARY + "View Inventory",
                        "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
            });
            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }

            match.broadcast(winnerMessage);
            match.broadcast(inventories);
        } else if (match.isRedrover()) {
            match.broadcast(CC.SECONDARY + event.getWinningTeam().getLeaderName() + CC.PRIMARY + " has won the redrover!");
        } else {
            Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();
            match.getTeams().forEach(team -> team.players().forEach(player -> {
                if (!match.hasSnapshot(player.getUniqueId())) {
                    match.addSnapshot(player);
                }

                inventorySnapshotMap
                        .put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

                boolean onWinningTeam =
                        this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() ==
                                event.getWinningTeam().getTeamID();
                inventories.add((onWinningTeam ? CC.YELLOW : CC.YELLOW)
                                + player.getName() + " ",
                        CC.PRIMARY + "View inventory",
                        "/inventory " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
            }));
            for (InventorySnapshot snapshot : match.getSnapshots().values()) {
                this.plugin.getInventoryManager().addSnapshot(snapshot);
            }

            String winnerMessage = CC.PRIMARY + (match.isParty() ? "Winning Team: " : "Winner: ")
                    + CC.SECONDARY + event.getWinningTeam().getLeaderName();

            match.broadcast(winnerMessage);
            match.broadcast(inventories);

            if (match.getType().isRanked()) {
                String kitName = match.getKit().getName();

                Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
                PlayerData winnerLeaderData = this.plugin.getPlayerManager()
                        .getPlayerData(winnerLeader.getUniqueId());
                Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
                PlayerData loserLeaderData = this.plugin.getPlayerManager()
                        .getPlayerData(loserLeader.getUniqueId());

                String eloMessage;

                int[] preElo = new int[2];
                int[] newElo = new int[2];
                int winnerElo = 0;
                int loserElo = 0;;
                int newWinnerElo;
                int newLoserElo;

                if (event.getWinningTeam().getPlayers().size() == 2) {
                    Player winnerMember = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(1));
                    PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());

                    Player loserMember = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(1));
                    PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());

                    winnerElo = winnerLeaderData.getPartyElo(kitName);
                    loserElo = loserLeaderData.getPartyElo(kitName);

                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;

                    newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;

                    winnerMemberData.setPartyElo(kitName, newWinnerElo);
                    loserMemberData.setPartyElo(kitName, newLoserElo);

                    eloMessage = CC.YELLOW + "Elo Change: " + CC.GREEN + winnerLeader.getName() + ", " +
                            winnerMember.getName() + " " + newWinnerElo +
                            " (+" + (newWinnerElo - winnerElo) + ") " + CC.RED + loserLeader.getName() + "," +
                            " " +
                            loserMember.getName() + " " +
                            newLoserElo + " (" + (newLoserElo - loserElo) + ")";
                } else {
                    if (match.getType() == QueueType.RANKED) {
                        winnerElo = winnerLeaderData.getElo(kitName);
                        loserElo = loserLeaderData.getElo(kitName);

                    }

                    preElo[0] = winnerElo;
                    preElo[1] = loserElo;

                    newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
                    newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

                    newElo[0] = newWinnerElo;
                    newElo[1] = newLoserElo;

                    eloMessage = CC.YELLOW + "Elo Changes: " + CC.GREEN + winnerLeader.getName() + " " + newWinnerElo +
                            " (+" + (newWinnerElo - winnerElo) + ") " +
                            CC.RED + loserLeader.getName() + " " + newLoserElo + " (" +
                            (newLoserElo - loserElo) + ")";

                    if (match.getType() == QueueType.RANKED) {
                        winnerLeaderData.setElo(kitName, newWinnerElo);
                        loserLeaderData.setElo(kitName, newLoserElo);

                        winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
                        loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
                    }
                }

                match.broadcast(eloMessage);
            }
            this.plugin.getMatchManager().saveRematches(match);
        }
    }
}
