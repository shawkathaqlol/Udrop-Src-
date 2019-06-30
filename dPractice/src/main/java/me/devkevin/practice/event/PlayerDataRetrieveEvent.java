package me.devkevin.practice.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.devkevin.practice.player.PlayerData;

public class PlayerDataRetrieveEvent extends Event {
    private static final HandlerList HANDLERS;

    static {
        HANDLERS = new HandlerList();
    }

    private final PlayerData playerData;

    public PlayerDataRetrieveEvent(final PlayerData playerData) {
        this.playerData = playerData;
    }

    public static HandlerList getHandlerList() {
        return PlayerDataRetrieveEvent.HANDLERS;
    }

    public HandlerList getHandlers() {
        return PlayerDataRetrieveEvent.HANDLERS;
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }
}
