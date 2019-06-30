package me.devkevin.practice.queue;

public class QueueEntry {
    private final QueueType queueType;
    private final String kitName;
    private final int elo;
    private final boolean party;

    public QueueEntry(final QueueType queueType, final String kitName, final int elo, final boolean party) {
        this.queueType = queueType;
        this.kitName = kitName;
        this.elo = elo;
        this.party = party;
    }

    public QueueType getQueueType() {
        return this.queueType;
    }

    public String getKitName() {
        return this.kitName;
    }

    public int getElo() {
        return this.elo;
    }

    public boolean isParty() {
        return this.party;
    }
}
