package me.devkevin.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.util.ItemUtil;

public class ItemManager
{
    private final ItemStack[] spawnItems;
    private final ItemStack[] queueItems;
    private final ItemStack[] partyItems;
    private final ItemStack[] tournamentItems;
    private final ItemStack[] eventItems;
    private final ItemStack[] specItems;
    private final ItemStack[] partySpecItems;
    private final ItemStack defaultBook;

    public ItemManager()
    {
        this.spawnItems = new ItemStack[] { ItemUtil.createItem(Material.BOOK, ChatColor.RED + "Edit Kit"), ItemUtil.createItem (Material.EMERALD, ChatColor.GREEN + "View LeaderBoards"), null, null, ItemUtil.createItem(Material.NAME_TAG, ChatColor.YELLOW + "Create a party"), null, null, ItemUtil.createItem(Material.IRON_SWORD, ChatColor.BLUE + "Unranked Queue"), ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked Queue") };
        this.queueItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString() + "Leave Queue") };
        this.specItems = new ItemStack[] { null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Spectator Mode"), null, null, null, null };
        this.partySpecItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Party") };
        this.tournamentItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Tournament") };
        this.eventItems = new ItemStack[] { null, null, null, null, null, null, null, null, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event") };
        this.partyItems = new ItemStack[] { ItemUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN.toString() + "Join 2v2 Unranked Queue"), ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.YELLOW.toString() + "Join 2v2 Ranked Queue"), null, ItemUtil.createItem(Material.GOLD_AXE, ChatColor.AQUA.toString() + "Start Party Events"), ItemUtil.createItem(Material.DIAMOND_AXE, ChatColor.DARK_AQUA.toString() + "Fight Other Party"), null, ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.AQUA.toString() + "View Party Members"), ItemUtil.createItem(Material.BOOK, ChatColor.GOLD.toString() + "Edit Kits"), ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Party") };
        this.defaultBook = ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.YELLOW.toString() + "Default Kit");
    }

    public ItemStack[] getSpawnItems()
    {
        return this.spawnItems;
    }

    public ItemStack[] getQueueItems()
    {
        return this.queueItems;
    }

    public ItemStack[] getPartyItems()
    {
        return this.partyItems;
    }

    public ItemStack[] getTournamentItems()
    {
        return this.tournamentItems;
    }

    public ItemStack[] getEventItems()
    {
        return this.eventItems;
    }

    public ItemStack[] getSpecItems()
    {
        return this.specItems;
    }

    public ItemStack[] getPartySpecItems()
    {
        return this.partySpecItems;
    }

    public ItemStack getDefaultBook()
    {
        return this.defaultBook;
    }
}

