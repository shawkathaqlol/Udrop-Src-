package me.devkevin.practice.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public static HandlerList getHandlerList() {
        return BaseEvent.handlers;
    }

    public HandlerList getHandlers() {
        return BaseEvent.handlers;
    }

    public boolean call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
        return this instanceof Cancellable && ((Cancellable) this).isCancelled();
    }
}
