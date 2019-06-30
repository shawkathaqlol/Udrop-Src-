package gg.ragemc.spigot;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import gg.ragemc.spigot.knockback.KnockbackProfile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class RageSpigotConfig {

    private static final String HEADER = "This is the main configuration file for the Spigot.\n"
            + "Modify with extreme caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;

    private KnockbackProfile currentKb;
    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    private boolean hidePlayersFromTab;
    private boolean firePlayerMoveEvent;
    private boolean fireLeftClickAir;
    private boolean fireLeftClickBlock;
	private boolean entityActivation;
	private boolean invalidArmAnimationKick;
	private boolean mobAIEnabled;
	private boolean enabled1_8Stuff;
	private boolean doChunkUnload;
	private boolean blockOperations;

    public RageSpigotConfig() {
        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load settings.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().header(RageSpigotConfig.HEADER);
        this.config.options().copyDefaults(true);

        this.loadConfig();
    }

    private void loadConfig() {
	    this.hidePlayersFromTab = this.getBoolean("hide-players-from-tab", true);
	    this.firePlayerMoveEvent = this.getBoolean("fire-player-move-event", false);
	    this.fireLeftClickAir = this.getBoolean("fire-left-click-air", false);
	    this.fireLeftClickBlock = this.getBoolean("fire-left-click-block", false);
	    this.entityActivation = this.getBoolean("entity-activation", false);
	    this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", false);
	    this.mobAIEnabled = this.getBoolean("mob-ai", true);
	    this.enabled1_8Stuff = this.getBoolean("1-8-enabled", false);
	    this.doChunkUnload = this.getBoolean("do-chunk-unload", true);
	    this.blockOperations = this.getBoolean("block-operations", false);

	    try {
		    this.config.save(this.configFile);
        } catch (IOException ex) {
		    Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
	    }
    }

    public void save(){
        try {
            this.config.save(this.configFile);
            Bukkit.getLogger().log(Level.INFO, this.configFile + " save");
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
        }
    }

    public KnockbackProfile getCurrentKb() {
        return this.currentKb;
    }

    public void setCurrentKb(KnockbackProfile kb) {
        this.currentKb = kb;
    }

    public KnockbackProfile getKbProfileByName(String name) {
        for (KnockbackProfile profile : this.kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public Set<KnockbackProfile> getKbProfiles() {
        return this.kbProfiles;
    }

    public void set(String path, Object val) {
        this.config.set(path, val);
    }

	public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, (double) def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }

	public boolean isHidePlayersFromTab() {
        return this.hidePlayersFromTab;
    }

	public boolean isFirePlayerMoveEvent() {
        return this.firePlayerMoveEvent;
    }

	public boolean isFireLeftClickAir() {
        return this.fireLeftClickAir;
    }

	public boolean isFireLeftClickBlock() {
        return this.fireLeftClickBlock;
    }

    public boolean isEntityActivation() {
    	return this.entityActivation;
    }

	public boolean isInvalidArmAnimationKick() {
		return this.invalidArmAnimationKick;
	}

	public boolean isMobAIEnabled() {
		return this.mobAIEnabled;
	}

	public boolean is18Enabled() {
		return this.enabled1_8Stuff;
	}

	public boolean isDoChunkUnload() {
    	return this.doChunkUnload;
	}

	public boolean isBlockOperations() {
        return this.blockOperations;
    }

    public YamlConfiguration getConfig(){
        return this.config;
    }

    public File getConfigFile(){
        return this.configFile;
    }
}
