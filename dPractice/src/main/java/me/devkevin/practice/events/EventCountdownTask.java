package me.devkevin.practice.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.Clickable;

public abstract class EventCountdownTask extends BukkitRunnable {
    private final PracticeEvent event;
    private final int countdownTime;
    private int timeUntilStart;
    private boolean ended;

    public EventCountdownTask(final PracticeEvent event, final int countdownTime) {
        this.event = event;
        this.countdownTime = countdownTime;
        this.timeUntilStart = countdownTime;
    }

    public void run() {
        if (this.isEnded()) {
            return;
        }
        if (this.timeUntilStart <= 0) {
            if (this.canStart()) {
                Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), () -> this.event.start());
            } else {
                Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), () -> this.onCancel());
            }
            this.ended = true;
            return;
        }
        if (this.shouldAnnounce(this.timeUntilStart)) {
            final String toSend = ChatColor.YELLOW.toString() + ChatColor.BOLD + "[*] " + ChatColor.GREEN + "" + this.event.getName() + " is starting soon. " + ChatColor.GRAY + "[Join]";
            final Clickable message = new Clickable(toSend, ChatColor.GRAY + "Click to join this event.", "/join " + this.event.getName());
            Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
        }
        --this.timeUntilStart;
    }

    public abstract boolean shouldAnnounce(final int p0);

    public abstract boolean canStart();

    public abstract void onCancel();

    private String getTime(int time) {
        final StringBuilder timeStr = new StringBuilder();
        int minutes = 0;
        if (time % 60 == 0) {
            minutes = time / 60;
            time = 0;
        } else {
            while (time - 60 > 0) {
                ++minutes;
                time -= 60;
            }
        }
        if (minutes > 0) {
            timeStr.append(minutes).append("m");
        }
        if (time > 0) {
            timeStr.append((minutes > 0) ? " " : "").append(time).append("s");
        }
        return timeStr.toString();
    }

    public PracticeEvent getEvent() {
        return this.event;
    }

    public int getCountdownTime() {
        return this.countdownTime;
    }

    public int getTimeUntilStart() {
        return this.timeUntilStart;
    }

    public void setTimeUntilStart(final int timeUntilStart) {
        this.timeUntilStart = timeUntilStart;
    }

    public boolean isEnded() {
        return this.ended;
    }

    public void setEnded(final boolean ended) {
        this.ended = ended;
    }
}
