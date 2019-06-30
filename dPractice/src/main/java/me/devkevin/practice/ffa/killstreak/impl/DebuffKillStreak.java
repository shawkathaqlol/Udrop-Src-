package me.devkevin.practice.ffa.killstreak.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.ffa.killstreak.KillStreak;
import me.devkevin.practice.util.PlayerUtil;

import java.util.Arrays;
import java.util.List;

public class DebuffKillStreak implements KillStreak {
    private static final ItemStack SLOWNESS;
    private static final ItemStack POISON;

    static {
        SLOWNESS = new ItemStack(Material.POTION, 1, (short) 16394);
        POISON = new ItemStack(Material.POTION, 1, (short) 16388);
    }

    @Override
    public void giveKillStreak(final Player player) {
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.SLOWNESS.clone());
        PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, DebuffKillStreak.POISON.clone());
    }

    @Override
    public List<Integer> getStreaks() {
        return Arrays.asList(7, 25);
    }
}
