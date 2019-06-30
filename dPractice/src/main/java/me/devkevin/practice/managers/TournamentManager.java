package me.devkevin.practice.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.runnable.TournamentRunnable;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.tournament.TournamentState;
import me.devkevin.practice.tournament.TournamentTeam;
import me.devkevin.practice.util.TeamUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TournamentManager {
    private final Practice plugin;
    private final Map<UUID, Integer> players;
    private final Map<UUID, Integer> matches;
    private final Map<Integer, Tournament> tournaments;
    private final Map<Tournament, BukkitRunnable> runnables;

    public TournamentManager() {
        this.plugin = Practice.getInstance();
        this.players = new HashMap<>();
        this.matches = new HashMap<>();
        this.tournaments = new HashMap<>();
        this.runnables = new HashMap<>();
    }

    public boolean isInTournament(final UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public Tournament getTournament(final UUID uuid) {
        final Integer id = this.players.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }

    public Tournament getTournamentFromMatch(final UUID uuid) {
        final Integer id = this.matches.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }

    public void createTournament(final CommandSender commandSender, final int id, final int teamSize, final int size, final String kitName) {
        final Tournament tournament = new Tournament(id, teamSize, size, kitName);
        this.tournaments.put(id, tournament);
        final BukkitRunnable bukkitRunnable = new TournamentRunnable(tournament);
        bukkitRunnable.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        this.runnables.put(tournament, bukkitRunnable);
        commandSender.sendMessage(ChatColor.GREEN + "Successfully created tournament.");
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            player.performCommand("tournament alert " + id);
        }
    }

    private void playerLeft(final Tournament tournament, final Player player) {
        final TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        player.sendMessage(ChatColor.YELLOW.toString() + "(Tournament) " + ChatColor.GRAY + "You left the tournament.");
        this.players.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(ChatColor.YELLOW.toString() + "(Tournament) " + ChatColor.WHITE + "" + player.getName() + " left the tournament. (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")");
        if (team != null) {
            team.killPlayer(player.getUniqueId());
            if (team.getAlivePlayers().size() == 0) {
                tournament.killTeam(team);
                if (tournament.getAliveTeams().size() == 1) {
                    final TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);
                    final String names = TeamUtil.getNames(tournamentTeam);
                    for (int i = 0; i <= 2; ++i) {
                        final String announce = ChatColor.YELLOW + "(Tournament) " + ChatColor.GREEN.toString() + "Winner: " + names + ".";
                        Bukkit.broadcastMessage(announce);
                    }
                    for (final UUID playerUUID : tournamentTeam.getAlivePlayers()) {
                        this.players.remove(playerUUID);
                        final Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                        this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                    }
                    this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
                }
            } else if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
        }
    }

    private void teamEliminated(final Tournament tournament, final TournamentTeam winnerTeam, final TournamentTeam losingTeam) {
        for (final UUID playerUUID : losingTeam.getAlivePlayers()) {
            final Player player = this.plugin.getServer().getPlayer(playerUUID);
            tournament.removePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW.toString() + "(Tournament) " + ChatColor.GRAY + "You have been eliminated. " + ChatColor.GRAY);
            this.players.remove(player.getUniqueId());
        }
        final String word = (losingTeam.getAlivePlayers().size() > 1) ? "have" : "has";
        final boolean isParty = tournament.getTeamSize() > 1;
        final String announce = ChatColor.YELLOW + "(Tournament) " + ChatColor.RED + (isParty ? (losingTeam.getLeaderName() + "'s Party") : losingTeam.getLeaderName()) + ChatColor.GRAY + " " + word + " been eliminated by " + ChatColor.GREEN + (isParty ? (winnerTeam.getLeaderName() + "'s Party") : winnerTeam.getLeaderName()) + ".";
        final String alive = ChatColor.YELLOW + "(Tournament) " + ChatColor.GRAY + "Players: (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")";
        tournament.broadcast(announce);
        tournament.broadcast(alive);
    }

    public void leaveTournament(final Player player) {
        final Tournament tournament = this.getTournament(player.getUniqueId());
        if (tournament == null) {
            return;
        }
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                for (final UUID memberUUID : party.getMembers()) {
                    final Player member = this.plugin.getServer().getPlayer(memberUUID);
                    this.playerLeft(tournament, member);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
            }
        } else {
            this.playerLeft(tournament, player);
        }
    }

    private void playerJoined(final Tournament tournament, final Player player) {
        tournament.addPlayer(player.getUniqueId());
        this.players.put(player.getUniqueId(), tournament.getId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(ChatColor.YELLOW.toString() + "(Tournament) " + ChatColor.WHITE + "" + player.getName() + " joined the tournament. (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")");
    }

    public void joinTournament(final Integer id, final Player player) {
        final Tournament tournament = this.tournaments.get(id);
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                if (party.getMembers().size() + tournament.getPlayers().size() <= tournament.getSize()) {
                    if (party.getMembers().size() != tournament.getTeamSize() || party.getMembers().size() == 1) {
                        player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                    } else {
                        for (final UUID memberUUID : party.getMembers()) {
                            final Player member = this.plugin.getServer().getPlayer(memberUUID);
                            this.playerJoined(tournament, member);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
            }
        } else {
            this.playerJoined(tournament, player);
        }
        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }

    public Tournament getTournament(final Integer id) {
        return this.tournaments.get(id);
    }

    public void removeTournament(final Integer id, final boolean force) {
        final Tournament tournament = this.tournaments.get(id);
        if (tournament == null) {
            return;
        }
        if (force) {
            Iterator players = this.players.keySet().iterator();
            while (players.hasNext()) {
                UUID uuid = (UUID)players.next();
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "The tournament has force ended.");
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                                if (tournament.getTournamentState() == TournamentState.FIGHTING) {
                                    this.plugin.getMatchManager().removeFighter(player, this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), false);
                                }
                                this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                            }
                            , 2L);
                }
                players.remove();
            }
        }
        if (this.runnables.containsKey(tournament)) {
            this.runnables.get(tournament).cancel();
        }
        this.tournaments.remove(id);
    }

    public void addTournamentMatch(final UUID matchId, final Integer tournamentId) {
        this.matches.put(matchId, tournamentId);
    }

    public void removeTournamentMatch(final Match match) {
        final Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) {
            return;
        }
        tournament.removeMatch(match.getMatchId());
        this.matches.remove(match.getMatchId());
        final MatchTeam losingTeam = (match.getWinningTeamId() == 0) ? match.getTeams().get(1) : match.getTeams().get(0);
        final TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));
        tournament.killTeam(losingTournamentTeam);
        final MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());
        final TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));
        this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
        if (tournament.getMatches().size() == 0) {
            if (tournament.getAliveTeams().size() > 1) {
                tournament.setTournamentState(TournamentState.STARTING);
                tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                tournament.setCountdown(16);
            } else {
                final String names = TeamUtil.getNames(winningTournamentTeam);
                for (int i = 0; i <= 2; ++i) {
                    final String announce = ChatColor.YELLOW + "(Tournament) " + ChatColor.GREEN.toString() + "Winner: " + names + ".";
                    Bukkit.broadcastMessage(announce);
                }
                for (final UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                    this.players.remove(playerUUID);
                    final Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                    this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                }
                this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
            }
        }
    }

    public Map<Integer, Tournament> getTournaments() {
        return this.tournaments;
    }
}
