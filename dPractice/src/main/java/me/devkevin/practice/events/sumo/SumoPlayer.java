package me.devkevin.practice.events.sumo;

import org.bukkit.scheduler.BukkitTask;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;

import java.util.UUID;

public class SumoPlayer extends EventPlayer {
    private SumoState state;
    private BukkitTask fightTask;
    private SumoPlayer fighting;

    public SumoPlayer(final UUID uuid, final PracticeEvent event) {
        super(uuid, event);
        this.state = SumoState.WAITING;
    }

    public SumoState getState() {
        return this.state;
    }

    public void setState(final SumoState state) {
        this.state = state;
    }

    public BukkitTask getFightTask() {
        return this.fightTask;
    }

    public void setFightTask(final BukkitTask fightTask) {
        this.fightTask = fightTask;
    }

    public SumoPlayer getFighting() {
        return this.fighting;
    }

    public void setFighting(final SumoPlayer fighting) {
        this.fighting = fighting;
    }

    public enum SumoState {
        WAITING,
        PREPARING,
        FIGHTING,
        ELIMINATED
    }
}
