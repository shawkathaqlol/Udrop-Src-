package me.devkevin.practice.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.event.EventStartEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.events.parkour.ParkourEvent;
import me.devkevin.practice.events.redrover.RedroverEvent;
import me.devkevin.practice.events.redrover.RedroverPlayer;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.sumo.SumoPlayer;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.PlayerUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class PracticeEvent<K extends EventPlayer> {
    private final Practice plugin;
    private final String name;
    private int limit;
    private Player host;
    private EventState state;

    public PracticeEvent(final String name) {
        this.plugin = Practice.getInstance();
        this.limit = 30;
        this.state = EventState.UNANNOUNCED;
        this.name = name;
    }

    public void startCountdown() {
        if (this.getCountdownTask().isEnded()) {
            this.getCountdownTask().setTimeUntilStart(this.getCountdownTask().getCountdownTime());
            this.getCountdownTask().setEnded(false);
        } else {
            this.getCountdownTask().runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        }
    }

    public void sendMessage(final String message) {
        this.getBukkitPlayers().forEach(player -> player.sendMessage(message));
    }

    public Set<Player> getBukkitPlayers() {
        return this.getPlayers().keySet().stream().filter(uuid -> this.plugin.getServer().getPlayer(uuid) != null).map(this.plugin.getServer()::getPlayer).collect(Collectors.toSet());
    }

    public void join(final Player player) {
        if (this.getPlayers().size() >= this.limit) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.EVENT);
        PlayerUtil.clearPlayer(player);
        if (this.onJoin() != null) {
            this.onJoin().accept(player);
        }
        if (this.getSpawnLocations().size() == 1) {
            player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            final List<CustomLocation> spawnLocations = new ArrayList<CustomLocation>(this.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        this.plugin.getPlayerManager().giveLobbyItems(player);
        for (final Player other : this.getBukkitPlayers()) {
            other.showPlayer(player);
            player.showPlayer(other);
        }
        this.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the event. (" + this.getPlayers().size() + " player" + ((this.getPlayers().size() == 1) ? "" : "s") + ")");
    }

    public void leave(final Player player) {
        if (this instanceof OITCEvent) {
            final OITCEvent oitcEvent = (OITCEvent) this;
            final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
            oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
        }
        if (this.onDeath() != null) {
            this.onDeath().accept(player);
        }
        this.getPlayers().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public void start() {
        new EventStartEvent(this).call();
        this.setState(EventState.STARTED);
        this.onStart();
        this.plugin.getEventManager().setCooldown(0L);
    }

    public void end() {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> this.plugin.getPlayerManager().sendToSpawnAndReset(player)), 2L);
        this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + 300000L);
        if (this instanceof SumoEvent) {
            final SumoEvent sumoEvent = (SumoEvent) this;
            for (final SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {
                if (sumoPlayer.getFightTask() != null) {
                    sumoPlayer.getFightTask().cancel();
                }
            }
            if (sumoEvent.getWaterCheckTask() != null) {
                sumoEvent.getWaterCheckTask().cancel();
            }
        } else if (this instanceof OITCEvent) {
            final OITCEvent oitcEvent = (OITCEvent) this;
            if (oitcEvent.getGameTask() != null) {
                oitcEvent.getGameTask().cancel();
            }
        } else if (this instanceof RedroverEvent) {
            final RedroverEvent redroverEvent = (RedroverEvent) this;
            for (final RedroverPlayer redroverPlayer : redroverEvent.getPlayers().values()) {
                if (redroverPlayer.getFightTask() != null) {
                    redroverPlayer.getFightTask().cancel();
                }
            }
            if (redroverEvent.getGameTask() != null) {
                redroverEvent.getGameTask().cancel();
            }
        } else if (this instanceof ParkourEvent) {
            final ParkourEvent parkourEvent = (ParkourEvent) this;
            if (parkourEvent.getGameTask() != null) {
                parkourEvent.getGameTask().cancel();
            }
            if (parkourEvent.getWaterCheckTask() != null) {
                parkourEvent.getWaterCheckTask().cancel();
            }
        }
        this.getPlayers().clear();
        this.setState(EventState.UNANNOUNCED);
        final Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();
        while (iterator.hasNext()) {
            final UUID spectatorUUID = iterator.next();
            final Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectator != null) {
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getPlayerManager().sendToSpawnAndReset(spectator));
                iterator.remove();
            }
        }
        this.plugin.getEventManager().getSpectators().clear();
        this.getCountdownTask().setEnded(true);
    }

    public K getPlayer(final Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    public K getPlayer(final UUID uuid) {
        return this.getPlayers().get(uuid);
    }

    public abstract Map<UUID, K> getPlayers();

    public abstract EventCountdownTask getCountdownTask();

    public abstract List<CustomLocation> getSpawnLocations();

    public abstract void onStart();

    public abstract Consumer<Player> onJoin();

    public abstract Consumer<Player> onDeath();

    public Practice getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.name;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public Player getHost() {
        return this.host;
    }

    public void setHost(final Player host) {
        this.host = host;
    }

    public EventState getState() {
        return this.state;
    }

    public void setState(final EventState state) {
        this.state = state;
    }
}
