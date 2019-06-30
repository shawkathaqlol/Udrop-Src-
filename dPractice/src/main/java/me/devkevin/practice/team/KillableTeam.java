package me.devkevin.practice.team;

import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class KillableTeam {
    protected final Practice plugin;
    private final List<UUID> players;
    private final List<UUID> alivePlayers;
    private final String leaderName;
    private UUID leader;

    public KillableTeam(final UUID leader, final List<UUID> players) {
        this.plugin = Practice.getInstance();
        this.alivePlayers = new ArrayList<UUID>();
        this.leader = leader;
        this.leaderName = this.plugin.getServer().getPlayer(leader).getName();
        this.players = players;
        this.alivePlayers.addAll(players);
    }

    public void killPlayer(final UUID playerUUID) {
        this.alivePlayers.remove(playerUUID);
    }

    public Stream<Player> alivePlayers() {
        return this.alivePlayers.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Stream<Player> players() {
        return this.players.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public List<UUID> getAlivePlayers() {
        return this.alivePlayers;
    }

    public String getLeaderName() {
        return this.leaderName;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public void setLeader(final UUID leader) {
        this.leader = leader;
    }
}
