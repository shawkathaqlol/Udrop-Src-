package me.devkevin.practice.events;

import java.util.UUID;

public class EventPlayer {
    private final UUID uuid;
    private final PracticeEvent event;

    public EventPlayer(final UUID uuid, final PracticeEvent event) {
        this.uuid = uuid;
        this.event = event;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public PracticeEvent getEvent() {
        return this.event;
    }
}
