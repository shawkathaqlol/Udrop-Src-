package me.devkevin.practice.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.util.CC;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.MathUtil;
import me.devkevin.practice.util.StringUtil;
import me.devkevin.practice.util.inventory.InventoryUI;

import java.util.*;
import java.util.function.Function;

public class InventorySnapshot
{
    private final InventoryUI inventoryUI;
    private final ItemStack[] originalInventory;
    private final ItemStack[] originalArmor;
    private final UUID snapshotId;

    public InventorySnapshot(final Player player, final Match match) {
        this.snapshotId = UUID.randomUUID();
        final ItemStack[] contents = player.getInventory().getContents();
        final ItemStack[] armor = player.getInventory().getArmorContents();
        this.originalInventory = contents;
        this.originalArmor = armor;
        final PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        final double health = player.getHealth();
        final double food = player.getFoodLevel();
        final List<String> potionEffectStrings = new ArrayList<String>();
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            final String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            final String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            final String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
            potionEffectStrings.add(CC.PRIMARY + effectName + " " + romanNumeral + CC.SECONDARY + " (" + duration + ")");
        }
        this.inventoryUI = new InventoryUI(player.getName(), true, 6);
        for (int i = 0; i < 9; ++i) {
            this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
            this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
            this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
            this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
        }
        boolean potionMatch = false;
        boolean soupMatch = false;
        for (final ItemStack item : match.getKit().getContents()) {
            if (item != null) {
                if (item.getType() == Material.MUSHROOM_SOUP) {
                    soupMatch = true;
                    break;
                }
                if (item.getType() == Material.POTION && item.getDurability() == 16421) {
                    potionMatch = true;
                    break;
                }
            }
        }
        if (potionMatch) {
            int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();
            this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.POTION, CC.PRIMARY + "Health Potions: " + CC.SECONDARY + potCount, potCount, (short)16421), CC.PRIMARY + "Missed Potions: " + CC.SECONDARY + playerData.getMissedPots())));
        }
        else if (soupMatch) {
            final int soupCount = (int)Arrays.stream(contents).filter(Objects::nonNull).map((Function<? super ItemStack, ?>)ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();
            this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.MUSHROOM_SOUP, CC.PRIMARY + "Remaining Soups: " + CC.SECONDARY + soupCount, soupCount, (short)16421)));
        }
        this.inventoryUI.setItem(48, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + "Hearts: " + CC.SECONDARY + MathUtil.roundToHalves(health / 2.0) + " / 10 \u2764", (int)Math.round(health / 2.0))));
        this.inventoryUI.setItem(49, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, CC.PRIMARY + "Hunger: " + CC.SECONDARY + MathUtil.roundToHalves(food / 2.0) + " / 10 \u2764", (int)Math.round(food / 2.0))));
        this.inventoryUI.setItem(50, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.BREWING_STAND_ITEM, CC.PRIMARY + "Potion Effects", potionEffectStrings.size()), (String[])potionEffectStrings.toArray(new String[0]))));
        this.inventoryUI.setItem(51, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.PRIMARY + "Statistics"), CC.PRIMARY + "Longest Combo: " + CC.SECONDARY + playerData.getLongestCombo() + " Hit" + ((playerData.getLongestCombo() > 1) ? "s" : ""), CC.PRIMARY + "Total Hits: " + CC.SECONDARY + playerData.getHits() + " Hit" + ((playerData.getHits() > 1) ? "s" : ""))));
        if (!match.isParty()) {
            for (int j = 0; j < 2; ++j) {
                this.inventoryUI.setItem((j == 0) ? 53 : 45, new InventoryUI.AbstractClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Other Inventory"), CC.PRIMARY + "Click to view the other inventory")) {
                    @Override
                    public void onClick(final InventoryClickEvent inventoryClickEvent) {
                        final Player clicker = (Player)inventoryClickEvent.getWhoClicked();
                        if (Practice.getInstance().getMatchManager().isRematching(player.getUniqueId())) {
                            clicker.closeInventory();
                            Practice.getInstance().getServer().dispatchCommand((CommandSender)clicker, "inventory " + Practice.getInstance().getMatchManager().getRematcherInventory(player.getUniqueId()));
                        }
                    }
                });
            }
        }
        for (int j = 36; j < 40; ++j) {
            this.inventoryUI.setItem(j, new InventoryUI.EmptyClickableItem(armor[39 - j]));
        }
    }

    public JSONObject toJson() {
        final JSONObject object = new JSONObject();
        final JSONObject inventoryObject = new JSONObject();
        for (int i = 0; i < this.originalInventory.length; ++i) {
            inventoryObject.put((Object)i, (Object)this.encodeItem(this.originalInventory[i]));
        }
        object.put((Object)"inventory", (Object)inventoryObject);
        final JSONObject armourObject = new JSONObject();
        for (int j = 0; j < this.originalArmor.length; ++j) {
            armourObject.put((Object)j, (Object)this.encodeItem(this.originalArmor[j]));
        }
        object.put((Object)"armour", (Object)armourObject);
        return object;
    }

    private JSONObject encodeItem(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put((Object)"material", (Object)itemStack.getType().name());
        object.put((Object)"durability", (Object)itemStack.getDurability());
        object.put((Object)"amount", (Object)itemStack.getAmount());
        final JSONObject enchants = new JSONObject();
        for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put((Object)enchantment.getName(), itemStack.getEnchantments().get(enchantment));
        }
        object.put((Object)"enchants", (Object)enchants);
        return object;
    }

    public InventoryUI getInventoryUI() {
        return this.inventoryUI;
    }

    public ItemStack[] getOriginalInventory() {
        return this.originalInventory;
    }

    public ItemStack[] getOriginalArmor() {
        return this.originalArmor;
    }

    public UUID getSnapshotId() {
        return this.snapshotId;
    }
}
