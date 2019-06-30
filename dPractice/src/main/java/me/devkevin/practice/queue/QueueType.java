package me.devkevin.practice.queue;

public enum QueueType {
    UNRANKED("Unranked"),
    RANKED("Ranked");

    private final String name;

    QueueType(final String name) {
        this.name = name;
    }

    public boolean isRanked() {
        return this != QueueType.UNRANKED;
    }

    public String getName() {
        return this.name;
    }
}
