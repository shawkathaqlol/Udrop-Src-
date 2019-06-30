package me.devkevin.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.StandaloneArena;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.inventory.InventoryUI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    private final Practice plugin;
    private final Config config;
    private final Map<String, Arena> arenas;
    private final Map<StandaloneArena, UUID> arenaMatchUUIDs;
    private int generatingArenaRunnables;

    public ArenaManager() {
        this.plugin = Practice.getInstance();
        this.config = new Config("arenas", this.plugin);
        this.arenas = new HashMap<>();
        this.arenaMatchUUIDs = new HashMap<>();
        this.loadArenas();
    }

    private void loadArenas() {
        FileConfiguration fileConfig = this.config.getConfig();
        ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection != null) {
            arenaSection.getKeys(false).forEach((name) -> {
                String a = arenaSection.getString(name + ".a");
                String b = arenaSection.getString(name + ".b");
                String min = arenaSection.getString(name + ".min");
                String max = arenaSection.getString(name + ".max");
                CustomLocation locA = CustomLocation.stringToLocation(a);
                CustomLocation locB = CustomLocation.stringToLocation(b);
                CustomLocation locMin = CustomLocation.stringToLocation(min);
                CustomLocation locMax = CustomLocation.stringToLocation(max);
                ArrayList<StandaloneArena> standaloneArenas = new ArrayList<>();
                ConfigurationSection saSection = arenaSection.getConfigurationSection(name + ".standaloneArenas");
                if (saSection != null) {
                    saSection.getKeys(false).forEach((id) -> {
                        String saA = saSection.getString(id + ".a");
                        String saB = saSection.getString(id + ".b");
                        String saMin = saSection.getString(id + ".min");
                        String saMax = saSection.getString(id + ".max");
                        CustomLocation locSaA = CustomLocation.stringToLocation(saA);
                        CustomLocation locSaB = CustomLocation.stringToLocation(saB);
                        CustomLocation locSaMin = CustomLocation.stringToLocation(saMin);
                        CustomLocation locSaMax = CustomLocation.stringToLocation(saMax);
                        standaloneArenas.add(new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax));
                    });
                }

                boolean enabled = arenaSection.getBoolean(name + ".enabled", false);
                Arena arena = new Arena(name, standaloneArenas, standaloneArenas, locA, locB, locMin, locMax, enabled);
                this.arenas.put(name, arena);
            });
        }

    }

    public void saveArenas() {
        FileConfiguration fileConfig = this.config.getConfig();
        fileConfig.set("arenas", null);
        this.arenas.forEach((arenaName, arena) -> {
            String a = CustomLocation.locationToString(arena.getA());
            String b = CustomLocation.locationToString(arena.getB());
            String min = CustomLocation.locationToString(arena.getMin());
            String max = CustomLocation.locationToString(arena.getMax());
            String arenaRoot = "arenas." + arenaName;
            fileConfig.set(arenaRoot + ".a", a);
            fileConfig.set(arenaRoot + ".b", b);
            fileConfig.set(arenaRoot + ".min", min);
            fileConfig.set(arenaRoot + ".max", max);
            fileConfig.set(arenaRoot + ".enabled", arena.isEnabled());
            fileConfig.set(arenaRoot + ".standaloneArenas", null);
            int i = 0;
            if (arena.getStandaloneArenas() != null) {
                for (Iterator var9 = arena.getStandaloneArenas().iterator(); var9.hasNext(); ++i) {
                    StandaloneArena saArena = (StandaloneArena) var9.next();
                    String saA = CustomLocation.locationToString(saArena.getA());
                    String saB = CustomLocation.locationToString(saArena.getB());
                    String saMin = CustomLocation.locationToString(saArena.getMin());
                    String saMax = CustomLocation.locationToString(saArena.getMax());
                    String standAloneRoot = arenaRoot + ".standaloneArenas." + i;
                    fileConfig.set(standAloneRoot + ".a", saA);
                    fileConfig.set(standAloneRoot + ".b", saB);
                    fileConfig.set(standAloneRoot + ".min", saMin);
                    fileConfig.set(standAloneRoot + ".max", saMax);
                }
            }

        });
        this.config.save();

    }

    public void reloadArenas() {
        this.saveArenas();
        this.arenas.clear();
        this.loadArenas();
    }

    public void openArenaSystemUI(final Player player) {
        if (this.arenas.size() == 0) {
            player.sendMessage(ChatColor.RED + "There's no arenas.");
            return;
        }
        final InventoryUI inventory = new InventoryUI("Arena System", true, 6);
        for (final Arena arena : this.arenas.values()) {
            final ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + arena.getName() + ChatColor.GRAY + " (" + (arena.isEnabled() ? (ChatColor.GREEN.toString() + ChatColor.BOLD + "ENABLED") : (ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED")) + ChatColor.GRAY + ")");
            ItemUtil.reloreItem(item, ChatColor.GRAY + "Arenas: " + ChatColor.GREEN + ((arena.getStandaloneArenas().size() == 0) ? "Single Arena (Invisible Players)" : (arena.getStandaloneArenas().size() + " Arenas")), ChatColor.GRAY + "Standalone Arenas: " + ChatColor.GREEN + ((arena.getAvailableArenas().size() == 0) ? "None" : (arena.getAvailableArenas().size() + " Arenas Available")), "", ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT CLICK " + ChatColor.GRAY + "Teleport to Arena", ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT CLICK " + ChatColor.GRAY + "Generate Standalone Arenas");
            inventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                @Override
                public void onClick(final InventoryClickEvent event) {
                    final Player player = (Player) event.getWhoClicked();
                    if (event.getClick() == ClickType.LEFT) {
                        player.teleport(arena.getA().toBukkitLocation());
                    } else {
                        final InventoryUI generateInventory = new InventoryUI("Generate Arenas", true, 1);
                        final int[] array = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};
                        for (final int batch : array) {
                            final ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RED.toString() + ChatColor.BOLD + batch + " ARENAS");
                            generateInventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                                @Override
                                public void onClick(final InventoryClickEvent event) {
                                    final Player player = (Player) event.getWhoClicked();
                                    player.performCommand("arena generate " + arena.getName() + " " + batch);
                                    player.sendMessage(ChatColor.GREEN + "Generating " + batch + " arenas, please check console for progress.");
                                    player.closeInventory();
                                }
                            });
                        }
                        player.openInventory(generateInventory.getCurrentPage());
                    }
                }
            });
        }
        player.openInventory(inventory.getCurrentPage());
    }

    public void createArena(final String name) {
        this.arenas.put(name, new Arena(name));
    }

    public void deleteArena(final String name) {
        this.arenas.remove(name);
    }

    public Arena getArena(final String name) {
        return this.arenas.get(name);
    }

    public Arena getRandomArena(final Kit kit) {
        final List<Arena> enabledArenas = new ArrayList<>();
        for (final Arena arena : this.arenas.values()) {
            if (!arena.isEnabled()) {
                continue;
            }
            if (kit.getExcludedArenas().contains(arena.getName())) {
                continue;
            }
            if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                continue;
            }
            enabledArenas.add(arena);
        }
        if (enabledArenas.size() == 0) {
            return null;
        }
        return enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
    }

    public void removeArenaMatchUUID(final StandaloneArena arena) {
        this.arenaMatchUUIDs.remove(arena);
    }

    public UUID getArenaMatchUUID(final StandaloneArena arena) {
        return this.arenaMatchUUIDs.get(arena);
    }

    public void setArenaMatchUUID(final StandaloneArena arena, final UUID matchUUID) {
        this.arenaMatchUUIDs.put(arena, matchUUID);
    }

    public Map<String, Arena> getArenas() {
        return this.arenas;
    }

    public Map<StandaloneArena, UUID> getArenaMatchUUIDs() {
        return this.arenaMatchUUIDs;
    }

    public int getGeneratingArenaRunnables() {
        return this.generatingArenaRunnables;
    }

    public void setGeneratingArenaRunnables(final int generatingArenaRunnables) {
        this.generatingArenaRunnables = generatingArenaRunnables;
    }
}
