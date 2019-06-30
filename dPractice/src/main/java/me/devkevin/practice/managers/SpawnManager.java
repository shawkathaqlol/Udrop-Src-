package me.devkevin.practice.managers;

import org.bukkit.configuration.file.FileConfiguration;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;

import java.util.ArrayList;
import java.util.List;

public class SpawnManager {
    private final Practice plugin;
    private CustomLocation spawnLocation;
    private CustomLocation spawnMin;
    private CustomLocation spawnMax;
    private CustomLocation editorLocation;
    private CustomLocation editorMin;
    private CustomLocation editorMax;
    private CustomLocation sumoLocation;
    private CustomLocation sumoFirst;
    private CustomLocation sumoSecond;
    private CustomLocation sumoMin;
    private CustomLocation sumoMax;
    private CustomLocation oitcLocation;
    private List<CustomLocation> oitcSpawnpoints;
    private CustomLocation oitcMin;
    private CustomLocation oitcMax;
    private CustomLocation parkourLocation;
    private CustomLocation parkourGameLocation;
    private CustomLocation parkourMin;
    private CustomLocation parkourMax;
    private CustomLocation redroverLocation;
    private CustomLocation redroverFirst;
    private CustomLocation redroverSecond;
    private CustomLocation redroverMin;
    private CustomLocation redroverMax;

    public SpawnManager() {
        this.plugin = Practice.getInstance();
        this.oitcSpawnpoints = new ArrayList<>();
        this.loadConfig();
    }

    private void loadConfig() {
        final FileConfiguration config = this.plugin.getMainConfig().getConfig();
        if (config.contains("spawnLocation")) {
            this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
            this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
            this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
        }
        if (config.contains("editorLocation")) {
            this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
            this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
            this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
        }
        if (config.contains("sumoLocation")) {
            this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
            this.sumoMin = CustomLocation.stringToLocation(config.getString("sumoMin"));
            this.sumoMax = CustomLocation.stringToLocation(config.getString("sumoMax"));
            this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
            this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
        }
        if (config.contains("oitcLocation")) {
            this.oitcLocation = CustomLocation.stringToLocation(config.getString("oitcLocation"));
            this.oitcMin = CustomLocation.stringToLocation(config.getString("oitcMin"));
            this.oitcMax = CustomLocation.stringToLocation(config.getString("oitcMax"));
            for (final String spawnpoint : config.getStringList("oitcSpawnpoints")) {
                this.oitcSpawnpoints.add(CustomLocation.stringToLocation(spawnpoint));
            }
        }
        if (config.contains("redroverLocation")) {
            this.redroverLocation = CustomLocation.stringToLocation(config.getString("redroverLocation"));
            this.redroverMin = CustomLocation.stringToLocation(config.getString("redroverMin"));
            this.redroverMax = CustomLocation.stringToLocation(config.getString("redroverMax"));
            this.redroverFirst = CustomLocation.stringToLocation(config.getString("redroverFirst"));
            this.redroverSecond = CustomLocation.stringToLocation(config.getString("redroverSecond"));
        }
        if (config.contains("parkourLocation")) {
            this.parkourLocation = CustomLocation.stringToLocation(config.getString("parkourLocation"));
            this.parkourGameLocation = CustomLocation.stringToLocation(config.getString("parkourGameLocation"));
            this.parkourMin = CustomLocation.stringToLocation(config.getString("parkourMin"));
            this.parkourMax = CustomLocation.stringToLocation(config.getString("parkourMax"));
        }
    }

    public void saveConfig() {
        final FileConfiguration config = this.plugin.getMainConfig().getConfig();
        if(spawnLocation != null)
            config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
        if(spawnMin != null)
            config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
        if(spawnMax != null)
            config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));
        if(editorLocation != null)
            config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
        if(editorMin != null)
            config.set("editorMin", CustomLocation.locationToString(this.editorMin));
        if(editorMax != null)
            config.set("editorMax", CustomLocation.locationToString(this.editorMax));
        if(sumoLocation != null)
            config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
        if(sumoMin != null)
            config.set("sumoMin", CustomLocation.locationToString(this.sumoMin));
        if(sumoMax != null)
            config.set("sumoMax", CustomLocation.locationToString(this.sumoMax));
        if(sumoFirst != null)
            config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
        if(sumoSecond != null)
            config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));
        if(oitcLocation != null)
            config.set("oitcLocation", CustomLocation.locationToString(this.oitcLocation));
        if(oitcMin != null)
            config.set("oitcMin", CustomLocation.locationToString(this.oitcMin));
        if(oitcMax != null)
            config.set("oitcMax", CustomLocation.locationToString(this.oitcMax));
        if(oitcSpawnpoints != null)
            config.set("oitcSpawnpoints", this.fromLocations(this.oitcSpawnpoints));
        if(parkourLocation != null)
            config.set("parkourLocation", CustomLocation.locationToString(this.parkourLocation));
        if(parkourGameLocation != null)
            config.set("parkourGameLocation", CustomLocation.locationToString(this.parkourGameLocation));
        if(parkourMin != null)
            config.set("parkourMin", CustomLocation.locationToString(this.parkourMin));
        if(parkourMax != null)
            config.set("parkourMax", CustomLocation.locationToString(this.parkourMax));
        if(redroverLocation != null)
            config.set("redroverLocation", CustomLocation.locationToString(this.redroverLocation));
        if(redroverMin != null)
            config.set("redroverMin", CustomLocation.locationToString(this.redroverMin));
        if(redroverMax != null)
            config.set("redroverMax", CustomLocation.locationToString(this.redroverMax));
        if(redroverFirst != null)
            config.set("redroverFirst", CustomLocation.locationToString(this.redroverFirst));
        if(redroverSecond != null)
            config.set("redroverSecond", CustomLocation.locationToString(this.redroverSecond));
        this.plugin.getMainConfig().save();
    }

    private List<String> fromLocations(final List<CustomLocation> locations) {
        final List<String> toReturn = new ArrayList<>();
        for (final CustomLocation location : locations) {
            toReturn.add(CustomLocation.locationToString(location));
        }
        return toReturn;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public CustomLocation getSpawnLocation() {
        return this.spawnLocation;
    }

    public void setSpawnLocation(final CustomLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public CustomLocation getSpawnMin() {
        return this.spawnMin;
    }

    public void setSpawnMin(final CustomLocation spawnMin) {
        this.spawnMin = spawnMin;
    }

    public CustomLocation getSpawnMax() {
        return this.spawnMax;
    }

    public void setSpawnMax(final CustomLocation spawnMax) {
        this.spawnMax = spawnMax;
    }

    public CustomLocation getEditorLocation() {
        return this.editorLocation;
    }

    public void setEditorLocation(final CustomLocation editorLocation) {
        this.editorLocation = editorLocation;
    }

    public CustomLocation getEditorMin() {
        return this.editorMin;
    }

    public void setEditorMin(final CustomLocation editorMin) {
        this.editorMin = editorMin;
    }

    public CustomLocation getEditorMax() {
        return this.editorMax;
    }

    public void setEditorMax(final CustomLocation editorMax) {
        this.editorMax = editorMax;
    }

    public CustomLocation getSumoLocation() {
        return this.sumoLocation;
    }

    public void setSumoLocation(final CustomLocation sumoLocation) {
        this.sumoLocation = sumoLocation;
    }

    public CustomLocation getSumoFirst() {
        return this.sumoFirst;
    }

    public void setSumoFirst(final CustomLocation sumoFirst) {
        this.sumoFirst = sumoFirst;
    }

    public CustomLocation getSumoSecond() {
        return this.sumoSecond;
    }

    public void setSumoSecond(final CustomLocation sumoSecond) {
        this.sumoSecond = sumoSecond;
    }

    public CustomLocation getSumoMin() {
        return this.sumoMin;
    }

    public void setSumoMin(final CustomLocation sumoMin) {
        this.sumoMin = sumoMin;
    }

    public CustomLocation getSumoMax() {
        return this.sumoMax;
    }

    public void setSumoMax(final CustomLocation sumoMax) {
        this.sumoMax = sumoMax;
    }

    public CustomLocation getOitcLocation() {
        return this.oitcLocation;
    }

    public void setOitcLocation(final CustomLocation oitcLocation) {
        this.oitcLocation = oitcLocation;
    }

    public List<CustomLocation> getOitcSpawnpoints() {
        return this.oitcSpawnpoints;
    }

    public void setOitcSpawnpoints(final List<CustomLocation> oitcSpawnpoints) {
        this.oitcSpawnpoints = oitcSpawnpoints;
    }

    public CustomLocation getOitcMin() {
        return this.oitcMin;
    }

    public void setOitcMin(final CustomLocation oitcMin) {
        this.oitcMin = oitcMin;
    }

    public CustomLocation getOitcMax() {
        return this.oitcMax;
    }

    public void setOitcMax(final CustomLocation oitcMax) {
        this.oitcMax = oitcMax;
    }

    public CustomLocation getParkourLocation() {
        return this.parkourLocation;
    }

    public void setParkourLocation(final CustomLocation parkourLocation) {
        this.parkourLocation = parkourLocation;
    }

    public CustomLocation getParkourGameLocation() {
        return this.parkourGameLocation;
    }

    public void setParkourGameLocation(final CustomLocation parkourGameLocation) {
        this.parkourGameLocation = parkourGameLocation;
    }

    public CustomLocation getParkourMin() {
        return this.parkourMin;
    }

    public void setParkourMin(final CustomLocation parkourMin) {
        this.parkourMin = parkourMin;
    }

    public CustomLocation getParkourMax() {
        return this.parkourMax;
    }

    public void setParkourMax(final CustomLocation parkourMax) {
        this.parkourMax = parkourMax;
    }

    public CustomLocation getRedroverLocation() {
        return this.redroverLocation;
    }

    public void setRedroverLocation(final CustomLocation redroverLocation) {
        this.redroverLocation = redroverLocation;
    }

    public CustomLocation getRedroverFirst() {
        return this.redroverFirst;
    }

    public void setRedroverFirst(final CustomLocation redroverFirst) {
        this.redroverFirst = redroverFirst;
    }

    public CustomLocation getRedroverSecond() {
        return this.redroverSecond;
    }

    public void setRedroverSecond(final CustomLocation redroverSecond) {
        this.redroverSecond = redroverSecond;
    }

    public CustomLocation getRedroverMin() {
        return this.redroverMin;
    }

    public void setRedroverMin(final CustomLocation redroverMin) {
        this.redroverMin = redroverMin;
    }

    public CustomLocation getRedroverMax() {
        return this.redroverMax;
    }

    public void setRedroverMax(final CustomLocation redroverMax) {
        this.redroverMax = redroverMax;
    }
}
