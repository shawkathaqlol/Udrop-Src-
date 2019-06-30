package me.devkevin.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.Practice;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.PlayerUtil;
import me.devkevin.practice.util.timer.impl.EnderpearlTimer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Practice plugin;
    private final Map<UUID, PlayerData> playerData;

    public PlayerManager() {
        this.plugin = Practice.getInstance();
        this.playerData = new ConcurrentHashMap<>();
    }

    public void createPlayerData(final Player player) {
        final PlayerData data = new PlayerData(player.getUniqueId());
        this.playerData.put(data.getUniqueId(), data);
        this.loadData(data);
    }

    private void loadData(final PlayerData playerData) {
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
        FileConfiguration fileConfig = config.getConfig();
        ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
        if (playerKitsSection != null) {
            this.plugin.getKitManager().getKits().forEach((kit) -> {
                ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());
                if (kitSection != null) {
                    kitSection.getKeys(false).forEach((kitKey) -> {
                        Integer kitIndex = Integer.parseInt(kitKey);
                        String displayName = kitSection.getString(kitKey + ".displayName");
                        ItemStack[] contents = (ItemStack[]) ((List) kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);
                        PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);
                        playerData.addPlayerKit(kitIndex, playerKit);
                    });
                }

            });
        }

        ConfigurationSection playerDataSelection = fileConfig.getConfigurationSection("playerdata");


        if(playerDataSelection != null){


            if(playerDataSelection.getConfigurationSection("elo") != null){
                playerDataSelection.getConfigurationSection("elo").getKeys(false).forEach(kit -> {
                    Integer elo = playerDataSelection.getInt("elo." + kit);
                    playerData.setElo(kit, elo);
                });
            }

            if(playerDataSelection.getConfigurationSection("losses") != null){
                playerDataSelection.getConfigurationSection("losses").getKeys(false).forEach(kit -> {
                    Integer elo = playerDataSelection.getInt("losses." + kit);
                    playerData.setLosses(kit, elo);
                });
            }

            if(playerDataSelection.getConfigurationSection("wins") != null){
                playerDataSelection.getConfigurationSection("wins").getKeys(false).forEach(kit -> {
                    Integer elo = playerDataSelection.getInt("wins." + kit);
                    playerData.setWins(kit, elo);
                });
            }

            if(playerDataSelection.getConfigurationSection("partyelo") != null){
                playerDataSelection.getConfigurationSection("partyelo").getKeys(false).forEach(kit -> {
                    Integer elo = playerDataSelection.getInt("partyelo." + kit);
                    playerData.setPartyElo(kit, elo);
                });
            }

            playerData.setOitcEventDeaths(playerDataSelection.getInt("oitcEventDeaths"));
            playerData.setOitcEventWins(playerDataSelection.getInt("oitcEventWins"));
            playerData.setOitcEventDeaths(playerDataSelection.getInt("oitcEventLosses"));
            playerData.setSumoEventWins(playerDataSelection.getInt("sumoEventWins"));
            playerData.setSumoEventLosses(playerDataSelection.getInt("sumoEventLosses"));
            playerData.setParkourEventWins(playerDataSelection.getInt("parkourEventWins"));
            playerData.setParkourEventLosses(playerDataSelection.getInt("parkourEventLosses"));
            playerData.setRedroverEventWins(playerDataSelection.getInt("redroverEventWins"));
            playerData.setRedroverEventLosses(playerDataSelection.getInt("redroverEventLosses"));
            playerData.setRematchID(playerDataSelection.getInt("rematchID"));
        }

        playerData.setPlayerState(PlayerState.SPAWN);
    }

    public void removePlayerData(final UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.saveData(this.playerData.get(uuid));
            this.playerData.remove(uuid);
        });
    }

    public void saveData(final PlayerData playerData) {
        if(playerData != null) {
            Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
            this.plugin.getKitManager().getKits().forEach((kit) -> {
                Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
                if(playerKits != null) {
                    playerKits.forEach((key, value) -> {
                        config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                        config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
                    });
                }
                config.getConfig().set("playerdata.elo." + kit.getName() , playerData.getElo(kit.getName()));
                config.getConfig().set("playerdata.losses." + kit.getName() , playerData.getLosses(kit.getName()));
                config.getConfig().set("playerdata.wins." + kit.getName() , playerData.getWins(kit.getName()));
                config.getConfig().set("playerdata.partyelo." + kit.getName() , playerData.getPartyElo(kit.getName()));
            });
            config.getConfig().set("playerdata.oitcEventDeaths", playerData.getOitcEventDeaths());
            config.getConfig().set("playerdata.oitcEventWins", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.oitcEventLosses", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.sumoEventWins", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.sumoEventLosses", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.parkourEventWins", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.parkourEventLosses", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.redroverEventWins", playerData.getOitcEventWins());
            config.getConfig().set("playerdata.redroverEventLosses", playerData.getOitcEventWins());

            config.save();
        }
    }

    public Collection<PlayerData> getAllData() {
        return this.playerData.values();
    }

    public PlayerData getPlayerData(final UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void giveLobbyItems(final Player player) {
        final boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        final boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
        final boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        final boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
        ItemStack[] items = this.plugin.getItemManager().getSpawnItems();
        if (inTournament) {
            items = this.plugin.getItemManager().getTournamentItems();
        } else if (inEvent) {
            items = this.plugin.getItemManager().getEventItems();
        } else if (inParty) {
            items = this.plugin.getItemManager().getPartyItems();
        }
        player.getInventory().setContents(items);
        if (isRematching && !inParty && !inTournament && !inEvent) {
            player.getInventory().setItem(6, ItemUtil.createItem(Material.BLAZE_POWDER, ChatColor.DARK_GREEN.toString() + "Rematch"));
        }
        player.updateInventory();
    }

    public void sendToSpawnAndReset(final Player player) {
        final PlayerData playerData = this.getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());
        this.giveLobbyItems(player);
        if (!player.isOnline()) {
            return;
        }
        this.plugin.getServer().getOnlinePlayers().forEach(p -> {
            player.hidePlayer(p);
            p.hidePlayer(player);
            return;
        });
        player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
    }

    private void saveConfigPlayerData(final PlayerData playerData) {
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
    }
}
