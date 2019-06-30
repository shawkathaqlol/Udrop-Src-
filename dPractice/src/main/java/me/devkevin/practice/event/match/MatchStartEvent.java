package me.devkevin.practice.event.match;

import me.devkevin.practice.match.Match;

public class MatchStartEvent extends MatchEvent {
    public MatchStartEvent(final Match match) {
        super(match);
    }
}
