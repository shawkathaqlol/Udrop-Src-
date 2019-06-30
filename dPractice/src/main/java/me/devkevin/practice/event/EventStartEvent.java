package me.devkevin.practice.event;

import me.devkevin.practice.events.PracticeEvent;

public class EventStartEvent extends BaseEvent {
    private final PracticeEvent event;

    public EventStartEvent(final PracticeEvent event) {
        this.event = event;
    }

    public PracticeEvent getEvent() {
        return this.event;
    }
}
