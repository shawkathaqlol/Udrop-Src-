package me.devkevin.practice.events.oitc;

import org.bukkit.scheduler.BukkitTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;

import java.util.UUID;

public class OITCPlayer extends EventPlayer {
    private OITCState state;
    private int score;
    private int lives;
    private BukkitTask respawnTask;
    private OITCPlayer lastKiller;

    public OITCPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = OITCState.WAITING;
        this.score = 0;
        this.lives = 5;
    }

    public OITCState getState() {
        return this.state;
    }

    public void setState(final OITCState state) {
        this.state = state;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(final int lives) {
        this.lives = lives;
    }

    public BukkitTask getRespawnTask() {
        return this.respawnTask;
    }

    public void setRespawnTask(final BukkitTask respawnTask) {
        this.respawnTask = respawnTask;
    }

    public OITCPlayer getLastKiller() {
        return this.lastKiller;
    }

    public void setLastKiller(final OITCPlayer lastKiller) {
        this.lastKiller = lastKiller;
    }

    public enum OITCState {
        WAITING,
        PREPARING,
        FIGHTING,
        RESPAWNING,
        ELIMINATED
    }
}
