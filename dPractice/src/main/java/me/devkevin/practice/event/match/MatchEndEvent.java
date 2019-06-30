package me.devkevin.practice.event.match;

import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;

public class MatchEndEvent extends MatchEvent {
    private final MatchTeam winningTeam;
    private final MatchTeam losingTeam;

    public MatchEndEvent(final Match match, final MatchTeam winningTeam, final MatchTeam losingTeam) {
        super(match);
        this.winningTeam = winningTeam;
        this.losingTeam = losingTeam;
    }

    public MatchTeam getWinningTeam() {
        return this.winningTeam;
    }

    public MatchTeam getLosingTeam() {
        return this.losingTeam;
    }
}
