package me.devkevin.practice.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.Practice;

public class PlayerKit {
    private final String name;
    private final int index;
    private ItemStack[] contents;
    private String displayName;

    public PlayerKit(final String name, final int index, final ItemStack[] contents, final String displayName) {
        this.name = name;
        this.index = index;
        this.contents = contents;
        this.displayName = displayName;
    }

    public void applyToPlayer(final Player player) {
        for (final ItemStack itemStack : this.contents) {
            if (itemStack != null && itemStack.getAmount() <= 0) {
                itemStack.setAmount(1);
            }
        }
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(Practice.getInstance().getKitManager().getKit(this.name).getArmor());
        player.updateInventory();
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
}
