package me.devkevin.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.queue.QueueEntry;
import me.devkevin.practice.queue.QueueType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    private final Map<UUID, QueueEntry> queued;
    private final Map<UUID, Long> playerQueueTime;
    private final Practice plugin;
    private boolean rankedEnabled;

    public QueueManager() {
        this.queued = new ConcurrentHashMap<UUID, QueueEntry>();
        this.playerQueueTime = new HashMap<UUID, Long>();
        this.plugin = Practice.getInstance();
        this.rankedEnabled = true;
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> this.queued.forEach((key, value) -> {
            if (value.isParty()) {
                this.findMatch(this.plugin.getPartyManager().getParty(key), value.getKitName(), value.getElo(), value.getQueueType());
            } else {
                this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(), value.getElo(), value.getQueueType());
            }
        }), 20L, 20L);
    }

    public void addPlayerToQueue(final Player player, final PlayerData playerData, final String kitName, final QueueType type) {
        if (type != QueueType.UNRANKED && !this.rankedEnabled) {
            player.closeInventory();
            return;
        }
        playerData.setPlayerState(PlayerState.QUEUE);
        final int elo = (type == QueueType.RANKED) ? playerData.getElo(kitName) : 0;
        final QueueEntry entry = new QueueEntry(type, kitName, elo, false);
        this.queued.put(playerData.getUniqueId(), entry);
        this.giveQueueItems(player);
        final String unrankedMessage = ChatColor.YELLOW + "You Joined " + ChatColor.GREEN + "Unranked " + kitName + ChatColor.YELLOW + " queue.";
        final String rankedMessage = ChatColor.YELLOW + "You Joined " + ChatColor.GREEN + "Ranked " + kitName + ChatColor.YELLOW + " queue. " + ChatColor.GREEN + "[" + elo + "]";
        player.sendMessage((type == QueueType.UNRANKED) ? unrankedMessage : rankedMessage);
        this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());

        if (!this.findMatch(player, kitName, elo, type) && type.isRanked()) {
            player.sendMessage(ChatColor.YELLOW + "Searching in elo range " + ChatColor.GREEN
                    + (playerData.getEloRange() == -1
                    ? "Unrestricted"
                    : "[" + Math.max(elo - playerData.getEloRange() / 2, 0)
                    + " -> " + Math.max(elo + playerData.getEloRange() / 2, 0) + "]"));
        }
    }

    private void giveQueueItems(final Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getItemManager().getQueueItems());
        player.updateInventory();
    }

    public QueueEntry getQueueEntry(final UUID uuid) {
        return this.queued.get(uuid);
    }

    public long getPlayerQueueTime(final UUID uuid) {
        return this.playerQueueTime.get(uuid);
    }

    public int getQueueSize(final String ladder, final QueueType type) {
        return (int) this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type).filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
    }

    private boolean findMatch(final Player player, final String kitName, final int elo, final QueueType type) {
        final long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return false;
        }
        int eloRange = playerData.getEloRange();
        int pingRange = -1;
        final int seconds = Math.round(queueTime / 1000L);
        if (seconds > 5 && type != QueueType.UNRANKED) {
            if (pingRange != -1) {
                pingRange += (seconds - 5) * 25;
            }
            if (eloRange != -1) {
                eloRange += seconds * 50;
                if (eloRange >= 3000) {
                    eloRange = 3000;
                }
            }
        }
        if (eloRange == -1) {
            eloRange = Integer.MAX_VALUE;
        }
        if (pingRange == -1) {
            pingRange = Integer.MAX_VALUE;
        }
        final int ping = 0;
        for (final UUID opponent : this.queued.keySet()) {
            if (opponent == player.getUniqueId()) {
                continue;
            }
            final QueueEntry queueEntry = this.queued.get(opponent);
            if (!queueEntry.getKitName().equals(kitName)) {
                continue;
            }
            if (queueEntry.getQueueType() != type) {
                continue;
            }
            if (queueEntry.isParty()) {
                continue;
            }
            final Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
            final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
            if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
                continue;
            }
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                continue;
            }
            final int eloDiff = Math.abs(queueEntry.getElo() - elo);
            if (type.isRanked()) {
                if (eloDiff > eloRange) {
                    continue;
                }
                final long opponentQueueTime = System.currentTimeMillis() - this.playerQueueTime.get(opponentPlayer.getUniqueId());
                int opponentEloRange = -1;
                int opponentPingRange = -1;
                final int opponentSeconds = Math.round(opponentQueueTime / 1000L);
                if (opponentSeconds > 5) {
                    if (opponentPingRange != -1) {
                        opponentPingRange += (opponentSeconds - 5) * 25;
                    }
                    if (opponentEloRange != -1) {
                        opponentEloRange += opponentSeconds * 50;
                        if (opponentEloRange >= 3000) {
                            opponentEloRange = 3000;
                        }
                    }
                }
                if (opponentEloRange == -1) {
                    opponentEloRange = Integer.MAX_VALUE;
                }
                if (opponentPingRange == -1) {
                    opponentPingRange = Integer.MAX_VALUE;
                }
                if (eloDiff > opponentEloRange) {
                    continue;
                }
                final int pingDiff = Math.abs(0 - ping);
                if (type == QueueType.RANKED) {
                    if (pingDiff > opponentPingRange) {
                        continue;
                    }
                    if (pingDiff > pingRange) {
                        continue;
                    }
                }
            }
            final Kit kit = this.plugin.getKitManager().getKit(kitName);
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            String playerFoundMatchMessage;
            String matchedFoundMatchMessage;
            if (type.isRanked()) {
                playerFoundMatchMessage = ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " with " + ChatColor.YELLOW + "" + this.queued.get(player.getUniqueId()).getElo() + " elo";
                matchedFoundMatchMessage = ChatColor.YELLOW + opponentPlayer.getName() + ChatColor.GREEN + " with " + ChatColor.YELLOW + "" + this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo";
            } else {
                playerFoundMatchMessage = ChatColor.YELLOW + player.getName() + ".";
                matchedFoundMatchMessage = ChatColor.YELLOW + opponentPlayer.getName() + ".";
            }
            player.sendMessage(ChatColor.GREEN + "Starting duel against " + matchedFoundMatchMessage);
            opponentPlayer.sendMessage(ChatColor.GREEN + "Starting duel against " + playerFoundMatchMessage);
            final MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), 0);
            final MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), 1);
            final Match match = new Match(arena, kit, type, teamA, teamB);
            this.plugin.getMatchManager().createMatch(match);
            this.queued.remove(player.getUniqueId());
            this.queued.remove(opponentPlayer.getUniqueId());
            this.playerQueueTime.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void removePlayerFromQueue(final Player player) {
        final QueueEntry entry = this.queued.get(player.getUniqueId());
        this.queued.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.sendMessage(ChatColor.RED + "You have left the " + entry.getQueueType().getName() + " " + entry.getKitName() + " queue.");
    }

    public void addPartyToQueue(final Player leader, final Party party, final String kitName, final QueueType type) {
        if (type.isRanked() && !this.rankedEnabled) {
            leader.closeInventory();
        } else if (party.getMembers().size() != 2) {
            leader.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
            leader.closeInventory();
        } else {
            party.getMembers().stream().map(this.plugin.getPlayerManager()::getPlayerData).forEach(member -> member.setPlayerState(PlayerState.QUEUE));
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(leader.getUniqueId());
            final int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;
            this.queued.put(playerData.getUniqueId(), new QueueEntry(type, kitName, elo, true));
            this.giveQueueItems(leader);
            final String unrankedMessage = ChatColor.GREEN + "Your party has been added to the " + ChatColor.YELLOW + "Unranked 2v2 " + kitName + ChatColor.GREEN + " queue.";
            final String rankedMessage = ChatColor.GREEN + "Your party has been added to the " + ChatColor.YELLOW + "Ranked 2v2 " + kitName + ChatColor.GREEN + " queue with " + ChatColor.YELLOW + elo + " elo" + ChatColor.YELLOW + ".";
            party.broadcast(type.isRanked() ? rankedMessage : unrankedMessage);
            this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());
            this.findMatch(party, kitName, elo, type);
        }
    }

    private void findMatch(final Party partyA, final String kitName, final int elo, final QueueType type) {
        if (!this.playerQueueTime.containsKey(partyA.getLeader())) {
            return;
        }
        final long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());
        if (playerData == null) {
            return;
        }
        int eloRange = playerData.getEloRange();
        final int seconds = Math.round(queueTime / 1000L);
        if (seconds > 5 && type.isRanked()) {
            eloRange += seconds * 50;
            if (eloRange >= 1000) {
                eloRange = 1000;
            }
        }

        int finalEloRange = eloRange;
        UUID opponent = this.queued.entrySet().stream()
                .filter((entry) -> entry.getKey() != partyA.getLeader())
                .filter((entry) -> this.plugin.getPlayerManager().getPlayerData(entry.getKey()).getPlayerState() == PlayerState.QUEUE)
                .filter((entry) -> (entry.getValue()).isParty()).filter((entry) -> (entry.getValue()).getQueueType() == type)
                .filter((entry) -> !type.isRanked() || Math.abs((entry.getValue()).getElo() - elo) < finalEloRange)
                .filter((entry) -> (entry.getValue()).getKitName().equals(kitName))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);

        if (opponent == null) {
            return;
        }
        final PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
        if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            return;
        }
        final Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(opponent);
        final Party partyB = this.plugin.getPartyManager().getParty(opponent);
        final Kit kit = this.plugin.getKitManager().getKit(kitName);
        final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
        String partyAFoundMatchMessage;
        String partyBFoundMatchMessage;
        if (type.isRanked()) {
            partyAFoundMatchMessage = ChatColor.YELLOW + leaderB.getName() + "'s Party" + ChatColor.GREEN + " with " + ChatColor.YELLOW + "" + this.queued.get(leaderB.getUniqueId()).getElo() + " elo";
            partyBFoundMatchMessage = ChatColor.YELLOW + leaderA.getName() + "'s Party" + ChatColor.GREEN + " with " + ChatColor.YELLOW + "" + this.queued.get(leaderA.getUniqueId()).getElo() + " elo";
        } else {
            partyAFoundMatchMessage = ChatColor.YELLOW + leaderB.getName() + ChatColor.GREEN + "'s Party.";
            partyBFoundMatchMessage = ChatColor.YELLOW + leaderA.getName() + ChatColor.GREEN + "'s Party.";
        }
        partyA.broadcast(ChatColor.GREEN + "Starting duel against " + partyAFoundMatchMessage);
        partyB.broadcast(ChatColor.GREEN + "Starting duel against " + partyBFoundMatchMessage);
        final List<UUID> playersA = new ArrayList<UUID>(partyA.getMembers());
        final List<UUID> playersB = new ArrayList<UUID>(partyB.getMembers());
        final MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
        final MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);
        final Match match = new Match(arena, kit, type, teamA, teamB);
        this.plugin.getMatchManager().createMatch(match);
        this.queued.remove(partyA.getLeader());
        this.queued.remove(partyB.getLeader());
    }

    public void removePartyFromQueue(final Party party) {
        final QueueEntry entry = this.queued.get(party.getLeader());
        this.queued.remove(party.getLeader());
        party.members().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset);
        final String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";
        party.broadcast(ChatColor.GREEN.toString()  + "You party has left the " + type + " " + ChatColor.YELLOW + entry.getKitName() + " queue.");
    }

    public boolean isRankedEnabled() {
        return this.rankedEnabled;
    }

    public void setRankedEnabled(final boolean rankedEnabled) {
        this.rankedEnabled = rankedEnabled;
    }
}
