package me.devkevin.practice.util.timer.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.devkevin.practice.util.timer.Timer;

import java.util.Optional;
import java.util.UUID;

public class TimerPauseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final boolean paused;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private boolean cancelled;

    public TimerPauseEvent(final Timer timer, final boolean paused) {
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.paused = paused;
    }

    public TimerPauseEvent(final UUID userUUID, final Timer timer, final boolean paused) {
        this.userUUID = Optional.ofNullable(userUUID);
        this.timer = timer;
        this.paused = paused;
    }

    public static HandlerList getHandlerList() {
        return TimerPauseEvent.HANDLERS;
    }

    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public HandlerList getHandlers() {
        return TimerPauseEvent.HANDLERS;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
