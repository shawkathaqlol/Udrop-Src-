package me.devkevin.practice;

import com.bizarrealex.aether.Aether;
import gg.ragemc.spigot.RageSpigot;
import lombok.Getter;
import me.devkevin.practice.board.PracticeBoard;
import me.devkevin.practice.leaderboards.LeaderBoardsManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import me.devkevin.practice.cache.StatusCache;
import me.devkevin.practice.commands.FlyCommand;
import me.devkevin.practice.commands.InvCommand;
import me.devkevin.practice.commands.PartyCommand;
import me.devkevin.practice.commands.duel.AcceptCommand;
import me.devkevin.practice.commands.duel.DuelCommand;
import me.devkevin.practice.commands.duel.SpectateCommand;
import me.devkevin.practice.commands.event.*;
import me.devkevin.practice.commands.management.*;
import me.devkevin.practice.commands.time.DayCommand;
import me.devkevin.practice.commands.time.NightCommand;
import me.devkevin.practice.commands.time.SunsetCommand;
import me.devkevin.practice.commands.toggle.SettingsCommand;
import me.devkevin.practice.commands.warp.WarpCommand;
import me.devkevin.practice.ffa.FFAManager;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.handler.CustomMovementHandler;
import me.devkevin.practice.listeners.*;
import me.devkevin.practice.managers.*;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.runnable.ExpBarRunnable;
import me.devkevin.practice.runnable.SaveDataRunnable;
import me.devkevin.practice.settings.ProfileOptionsListeners;
import me.devkevin.practice.util.inventory.UIListener;
import me.devkevin.practice.util.timer.TimerManager;
import me.devkevin.practice.util.timer.impl.EnderpearlTimer;

import java.util.Arrays;
import java.util.Iterator;

public class Practice extends JavaPlugin {


    @Getter private static Practice instance;
    @Getter private Config mainConfig;
    @Getter private InventoryManager inventoryManager;
    @Getter private EditorManager editorManager;
    @Getter private PlayerManager playerManager;
    @Getter private ArenaManager arenaManager;
    @Getter private MatchManager matchManager;
    @Getter private PartyManager partyManager;
    @Getter private QueueManager queueManager;
    @Getter private EventManager eventManager;
    @Getter private ItemManager itemManager;
    @Getter private KitManager kitManager;
    @Getter private FFAManager ffaManager;
    @Getter private SpawnManager spawnManager;
    @Getter private TournamentManager tournamentManager;
    @Getter private ChunkManager chunkManager;
    @Getter private TimerManager timerManager;

    public static Practice getInstance() {
        return Practice.instance;
    }

    public void test() {
        for (final PlayerData playerData : this.playerManager.getAllData()) {
            this.playerManager.saveData(playerData);
        }
    }

    @Override
    public void onDisable() {
        for (final PlayerData playerData : this.playerManager.getAllData()) {
            this.playerManager.saveData(playerData);
        }
        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
        this.spawnManager.saveConfig();
    }

    @Override
    public void onEnable() {
        Practice.instance = this;
        this.mainConfig = new Config("config", this);
        RageSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());
        this.registerCommands();
        this.registerListeners();
        new Aether (this, new PracticeBoard ());
        this.registerManagers();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 6000L, 6000L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);
        new StatusCache().start();
        new LeaderBoardsManager ();
    }

    private void registerCommands() {
        Arrays.asList(new SettingsCommand(), new ResetStatsCommand(), new JoinEventCommand(), new LeaveEventCommand(), new StatusEventCommand(), new HostCommand(), new EventManagerCommand(), new AcceptCommand(), new SunsetCommand(), new ArenaCommand(), new NightCommand(), new FlyCommand(), new PartyCommand(), new DuelCommand(), new SpectateCommand(), new DayCommand(), new KitCommand(), new SpectateEventCommand(), new InvCommand(), new SpawnsCommand(), new WarpCommand(), new TournamentCommand()).forEach(command -> this.registerCommand(command, this.getName()));
    }

    private void registerListeners() {
        Arrays.asList(new EntityListener(), new PlayerListener(), new MatchListener(), new WorldListener(), new EnderpearlListener(this), new UIListener(), new ProfileOptionsListeners(), new InventoryListener()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.spawnManager = new SpawnManager();
        this.arenaManager = new ArenaManager();
        this.chunkManager = new ChunkManager();
        this.editorManager = new EditorManager();
        this.itemManager = new ItemManager();
        this.kitManager = new KitManager();
        this.matchManager = new MatchManager();
        this.partyManager = new PartyManager();
        this.playerManager = new PlayerManager();
        this.queueManager = new QueueManager();
        this.inventoryManager = new InventoryManager();
        this.eventManager = new EventManager();
        this.tournamentManager = new TournamentManager();
        this.timerManager = new TimerManager(this);
        if (this.timerManager.getTimer(EnderpearlTimer.class) == null) {
            this.timerManager.registerTimer(new EnderpearlTimer());
        }
    }

    public void registerCommand(final Command cmd, final String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }

    private void registerCommand(final Command cmd) {
        this.registerCommand(cmd, this.getName());
    }

    private void removeCrafting(final Material material) {
        final Iterator<Recipe> iterator = this.getServer().recipeIterator();
        while (iterator.hasNext()) {
            final Recipe recipe = iterator.next();
            if (recipe != null && recipe.getResult().getType() == material) {
                iterator.remove();
            }
        }
    }

    public Config getMainConfig() {
        return this.mainConfig;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public EditorManager getEditorManager() {
        return this.editorManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    public MatchManager getMatchManager() {
        return this.matchManager;
    }

    public PartyManager getPartyManager() {
        return this.partyManager;
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public FFAManager getFfaManager() {
        return this.ffaManager;
    }

    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }

    public TournamentManager getTournamentManager() {
        return this.tournamentManager;
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public TimerManager getTimerManager() {
        return this.timerManager;
    }
}
