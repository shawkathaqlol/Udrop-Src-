package me.devkevin.practice.event.match;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.devkevin.practice.match.Match;

public class MatchEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final Match match;

    public MatchEvent(final Match match) {
        this.match = match;
    }

    public static HandlerList getHandlerList() {
        return MatchEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return MatchEvent.HANDLERS;
    }

    public Match getMatch() {
        return this.match;
    }
}
