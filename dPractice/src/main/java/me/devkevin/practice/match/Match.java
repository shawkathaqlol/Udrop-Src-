package me.devkevin.practice.match;

import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import me.devkevin.practice.util.TimeUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.StandaloneArena;
import me.devkevin.practice.inventory.InventorySnapshot;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.Clickable;

import java.util.*;
import java.util.stream.Stream;

public class Match {

    @Getter private final Practice plugin;
    @Getter private final Map<UUID, InventorySnapshot> snapshots;
    @Getter private final Set<Entity> entitiesToRemove;
    @Getter private final Set<BlockState> originalBlockChanges;
    @Getter private final Set<Location> placedBlockLocations;
    @Getter private final Set<UUID> spectators;
    @Getter private final Set<Integer> runnables;
    @Getter private final Set<UUID> haveSpectated;
    @Getter private final List<MatchTeam> teams;
    @Getter private final UUID matchId;
    @Getter private final QueueType type;
    @Getter private final Arena arena;
    @Getter private final Kit kit;
    @Getter private final boolean redrover;
    @Getter private StandaloneArena standaloneArena;
    @Getter private MatchState matchState;
    @Getter private int winningTeamId;
    @Getter private int countdown;


    public Match(final Arena arena, final Kit kit, final QueueType type, final MatchTeam... teams) {
        this(arena, kit, type, false, teams);
    }

    public Match(final Arena arena, final Kit kit, final QueueType type, final boolean redrover, final MatchTeam... teams) {
        this.plugin = Practice.getInstance();
        this.snapshots = new HashMap<UUID, InventorySnapshot>();
        this.entitiesToRemove = new HashSet<Entity>();
        this.originalBlockChanges = Sets.newConcurrentHashSet();
        this.placedBlockLocations = Sets.newConcurrentHashSet();
        this.spectators = (Set<UUID>) new ConcurrentSet();
        this.runnables = new HashSet<Integer>();
        this.haveSpectated = new HashSet<UUID>();
        this.matchId = UUID.randomUUID();
        this.matchState = MatchState.STARTING;
        this.countdown = 6;
        this.arena = arena;
        this.kit = kit;
        this.type = type;
        this.redrover = redrover;
        this.teams = Arrays.asList(teams);
    }

    public void addSpectator(final UUID uuid) {
        this.spectators.add(uuid);
    }

    public void removeSpectator(final UUID uuid) {
        this.spectators.remove(uuid);
    }

    public void addHaveSpectated(final UUID uuid) {
        this.haveSpectated.add(uuid);
    }

    public boolean haveSpectated(final UUID uuid) {
        return this.haveSpectated.contains(uuid);
    }

    public void addSnapshot(final Player player) {
        this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
    }

    public boolean hasSnapshot(final UUID uuid) {
        return this.snapshots.containsKey(uuid);
    }

    public InventorySnapshot getSnapshot(final UUID uuid) {
        return this.snapshots.get(uuid);
    }

    public void addEntityToRemove(final Entity entity) {
        this.entitiesToRemove.add(entity);
    }

    public void removeEntityToRemove(final Entity entity) {
        this.entitiesToRemove.remove(entity);
    }

    public void clearEntitiesToRemove() {
        this.entitiesToRemove.clear();
    }

    public void addRunnable(final int id) {
        this.runnables.add(id);
    }

    public void addOriginalBlockChange(final BlockState blockState) {
        this.originalBlockChanges.add(blockState);
    }

    public void removeOriginalBlockChange(final BlockState blockState) {
        this.originalBlockChanges.remove(blockState);
    }

    public void addPlacedBlockLocation(final Location location) {
        this.placedBlockLocations.add(location);
    }

    public void removePlacedBlockLocation(final Location location) {
        this.placedBlockLocations.remove(location);
    }

    public void broadcastWithSound(final String message, final Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10.0f, 1.0f);
        }));
        this.spectatorPlayers().forEach(spectator -> {
            spectator.sendMessage(message);
            spectator.playSound(spectator.getLocation(), sound, 10.0f, 1.0f);
        });
    }

    public void broadcast(final String message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
    }

    public void broadcast(final Clickable message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
        this.spectatorPlayers().forEach(message::sendToPlayer);
    }

    public Stream<Player> spectatorPlayers() {
        return this.spectators.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int decrementCountdown() {
        return --this.countdown;
    }

    public boolean isParty() {
        return this.isFFA() || (this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1);
    }

    public boolean isPartyMatch() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() >= 2 || this.teams.get(1).getPlayers().size() >= 2;
    }

    public boolean isFFA() {
        return this.teams.size() == 1;
    }

    public Map<UUID, InventorySnapshot> getSnapshots() {
        return this.snapshots;
    }

    public Set<Entity> getEntitiesToRemove() {
        return this.entitiesToRemove;
    }

    public Set<BlockState> getOriginalBlockChanges() {
        return this.originalBlockChanges;
    }

    public Set<Location> getPlacedBlockLocations() {
        return this.placedBlockLocations;
    }

    public Set<UUID> getSpectators() {
        return this.spectators;
    }

    public Set<Integer> getRunnables() {
        return this.runnables;
    }

    public List<MatchTeam> getTeams() {
        return this.teams;
    }

    public UUID getMatchId() {
        return this.matchId;
    }

    public QueueType getType() {
        return this.type;
    }

    public Arena getArena() {
        return this.arena;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isRedrover() {
        return this.redrover;
    }

    public StandaloneArena getStandaloneArena() {
        return this.standaloneArena;
    }

    public void setStandaloneArena(final StandaloneArena standaloneArena) {
        this.standaloneArena = standaloneArena;
    }

    public MatchState getMatchState() {
        return this.matchState;
    }

    public void setMatchState(final MatchState matchState) {
        this.matchState = matchState;
    }

    public int getWinningTeamId() {
        return this.winningTeamId;
    }

    public void setWinningTeamId(final int winningTeamId) {
        this.winningTeamId = winningTeamId;
    }

    public int getCountdown() {
        return this.countdown;
    }

    public void setCountdown(final int countdown) {
        this.countdown = countdown;
    }
}
