package me.devkevin.practice.events.redrover;

import org.bukkit.scheduler.BukkitTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;

import java.util.UUID;

public class RedroverPlayer extends EventPlayer {
    private RedroverState state;
    private RedroverPlayer fightPlayer;
    private BukkitTask fightTask;

    public RedroverPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = RedroverState.WAITING;
    }

    public RedroverState getState() {
        return this.state;
    }

    public void setState(final RedroverState state) {
        this.state = state;
    }

    public RedroverPlayer getFightPlayer() {
        return this.fightPlayer;
    }

    public void setFightPlayer(final RedroverPlayer fightPlayer) {
        this.fightPlayer = fightPlayer;
    }

    public BukkitTask getFightTask() {
        return this.fightTask;
    }

    public void setFightTask(final BukkitTask fightTask) {
        this.fightTask = fightTask;
    }

    public enum RedroverState {
        WAITING,
        PREPARING,
        FIGHTING
    }
}
