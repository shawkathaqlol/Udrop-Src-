package me.devkevin.practice.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.Practice;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.Kit;

import java.util.*;

public class KitManager {
    private final Practice plugin;
    private final Map<String, Kit> kits;
    private final List<String> rankedKits;
    private final Config config;

    public KitManager() {
        this.plugin = Practice.getInstance();
        this.kits = new HashMap<>();
        this.rankedKits = new ArrayList<>();
        this.config = new Config("kits", this.plugin);
        this.loadKits();
        this.kits.entrySet().stream().filter(kit -> kit.getValue().isEnabled()).filter(kit -> kit.getValue().isRanked()).forEach(kit -> this.rankedKits.add(kit.getKey()));
    }

    private void loadKits() {
        FileConfiguration fileConfig = this.config.getConfig();
        ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");
        if(kitSection != null) {
            kitSection.getKeys(false).forEach((name) -> {
                ItemStack[] contents = (ItemStack[])((List)kitSection.get(name + ".contents")).toArray(new ItemStack[0]);
                ItemStack[] armor = (ItemStack[])((List)kitSection.get(name + ".armor")).toArray(new ItemStack[0]);
                ItemStack[] kitEditContents = (ItemStack[])((List)kitSection.get(name + ".kitEditContents")).toArray(new ItemStack[0]);
                List<String> excludedArenas = kitSection.getStringList(name + ".excludedArenas");
                List<String> arenaWhiteList = kitSection.getStringList(name + ".arenaWhitelist");
                ItemStack icon = (ItemStack)kitSection.get(name + ".icon");
                boolean enabled = kitSection.getBoolean(name + ".enabled");
                boolean ranked = kitSection.getBoolean(name + ".ranked");
                boolean combo = kitSection.getBoolean(name + ".combo");
                boolean sumo = kitSection.getBoolean(name + ".sumo");
                boolean build = kitSection.getBoolean(name + ".build");
                boolean spleef = kitSection.getBoolean(name + ".spleef");
                boolean parkour = kitSection.getBoolean(name + ".parkour");
                Kit kit = new Kit(name, contents, armor, kitEditContents, icon, excludedArenas, arenaWhiteList, enabled, ranked, combo, sumo, build, spleef, parkour);
                this.kits.put(name, kit);
            });
        }
    }

    public void saveKits() {
        FileConfiguration fileConfig = this.config.getConfig();
        fileConfig.set("kits", null);
        this.kits.forEach((kitName, kit) -> {
            if(kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
                fileConfig.set("kits." + kitName + ".contents", kit.getContents());
                fileConfig.set("kits." + kitName + ".armor", kit.getArmor());
                fileConfig.set("kits." + kitName + ".kitEditContents", kit.getKitEditContents());
                fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
                fileConfig.set("kits." + kitName + ".excludedArenas", kit.getExcludedArenas());
                fileConfig.set("kits." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
                fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
                fileConfig.set("kits." + kitName + ".ranked", kit.isRanked());
                fileConfig.set("kits." + kitName + ".combo", kit.isCombo());
                fileConfig.set("kits." + kitName + ".sumo", kit.isSumo());
                fileConfig.set("kits." + kitName + ".build", kit.isBuild());
                fileConfig.set("kits." + kitName + ".spleef", kit.isSpleef());
                fileConfig.set("kits." + kitName + ".parkour", kit.isParkour());
            }

        });
        this.config.save();
    }

    public void deleteKit(final String name) {
        this.kits.remove(name);
    }

    public void createKit(final String name) {
        this.kits.put(name, new Kit(name));
    }

    public Collection<Kit> getKits() {
        return this.kits.values();
    }

    public Kit getKit(final String name) {
        return this.kits.get(name);
    }

    public List<String> getRankedKits() {
        return this.rankedKits;
    }
}
