package me.devkevin.practice.cache;

import me.devkevin.practice.Practice;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

public class StatusCache extends Thread {
    private static StatusCache instance;
    private int fighting;
    private int queueing;

    public StatusCache() {
        StatusCache.instance = this;
    }

    public static StatusCache getInstance() {
        return StatusCache.instance;
    }

    @Override
    public void run() {
        while (true) {
            int fighting = 0;
            int queueing = 0;
            for (final PlayerData playerData : Practice.getInstance().getPlayerManager().getAllData()) {
                if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                    ++fighting;
                }
                if (playerData.getPlayerState() == PlayerState.QUEUE) {
                    ++queueing;
                }
            }
            this.fighting = fighting;
            this.queueing = queueing;
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getFighting() {
        return this.fighting;
    }

    public void setFighting(final int fighting) {
        this.fighting = fighting;
    }

    public int getQueueing() {
        return this.queueing;
    }

    public void setQueueing(final int queueing) {
        this.queueing = queueing;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StatusCache)) {
            return false;
        }
        final StatusCache other = (StatusCache) o;
        return other.canEqual(this) && this.getFighting() == other.getFighting() && this.getQueueing() == other.getQueueing();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof StatusCache;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getFighting();
        result = result * 59 + this.getQueueing();
        return result;
    }

    @Override
    public String toString() {
        return "StatusCache(fighting=" + this.getFighting() + ", queueing=" + this.getQueueing() + ")";
    }
}
