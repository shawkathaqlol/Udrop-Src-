package me.devkevin.practice.ffa.killstreak;

import org.bukkit.entity.Player;

import java.util.List;

public interface KillStreak {
    void giveKillStreak(final Player p0);

    List<Integer> getStreaks();
}
