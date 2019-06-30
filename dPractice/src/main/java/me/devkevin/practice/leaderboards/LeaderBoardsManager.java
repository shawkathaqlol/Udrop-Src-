package me.devkevin.practice.leaderboards;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import me.devkevin.practice.file.Config;
import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import me.devkevin.practice.kit.Kit;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Objects;
import org.bukkit.OfflinePlayer;
import java.util.Collection;
import java.util.Arrays;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import com.google.common.collect.Lists;
import me.devkevin.practice.Practice;
import com.google.common.collect.Maps;
import me.devkevin.practice.player.OfflinePlayerData;
import java.util.List;
import java.util.Map;
import org.bukkit.event.Listener;

public class LeaderBoardsManager implements Listener {

    @Getter
    private Map<String, List<OfflinePlayerData>> topsElo = Maps.newHashMap();

    public LeaderBoardsManager(){

        Practice.getInstance().getKitManager().getKits().forEach(kit -> {
            topsElo.put(kit.getName(), Lists.newArrayList());
        });

        topsElo.put("Global", Lists.newArrayList());

        Bukkit.getPluginManager().registerEvents(this, Practice.getInstance ());

        initOrderList();
    }

    public void initOrderList(){

        List<OfflinePlayer> offLines = Lists.newArrayList();

        offLines.addAll(Arrays.asList(Bukkit.getOfflinePlayers()));

        offLines.forEach(offlinePlayer -> {
            topsElo.get("Global").add(getOfflinePlayerData(offlinePlayer));
            Practice.getInstance().getKitManager().getKits().forEach(kit ->
                    topsElo.get(kit.getName()).add(getOfflinePlayerData(offlinePlayer)));
        });
    }

    public void updateList(){

        topsElo.get("Global").sort( (playerdata1, playerdata2) -> {

            Integer elo1 = playerdata1.getGlobalStats("ELO");
            Integer elo2 = playerdata2.getGlobalStats("ELO");

            Integer losses1 = playerdata1.getGlobalStats("LOSSES");
            Integer losses2 = playerdata2.getGlobalStats("LOSSES");

            if( Objects.equals ( elo1 , elo2 ) && losses1 < losses2 ){
                return -1;
            }else if( Objects.equals ( elo1 , elo2 ) && losses1 > losses2 ){
                return 1;
            }else if ( elo1 < elo2 ) {
                return 1;
            }else if ( elo1 > elo2 ) {
                return - 1;
            }

            return 0;

        } );

        Practice.getInstance().getKitManager().getKits().forEach( kit -> {
            topsElo.get(kit.getName()).sort( (playerdata1, playerdata2) -> {

                Integer elo1 = playerdata1.getElo(kit.getName());
                Integer elo2 = playerdata2.getElo(kit.getName());

                Integer losses1 = playerdata1.getLosses(kit.getName());
                Integer losses2 = playerdata2.getLosses(kit.getName());

                if( Objects.equals ( elo1 , elo2 ) && losses1 < losses2 ){
                    return -1;
                }else if( Objects.equals ( elo1 , elo2 ) && losses1 > losses2 ){
                    return 1;
                }else if ( elo1 < elo2 ) {
                    return 1;
                }else if ( elo1 > elo2 ) {
                    return - 1;
                }

                return 0;

            } );
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().name().startsWith("RIGHT_")) {

            if(event.getItem() != null){

                ItemStack itemStack = event.getItem();

                if(itemStack.getType() == Material.EMERALD){
                    openInventory(player);
                }
            }
        }
    }

    public void openInventory(Player player){

        updateList();
        player.getInventory ();

        Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.GREEN + "Viewing LeaderBoard");


        ItemStack global = new ItemStack(Material.SUGAR);

        ItemMeta globalitemMeta = global.getItemMeta();

        List<String> globallore = Lists.newArrayList();

        List<OfflinePlayerData> globalplayerDataList = topsElo.get("Global");

        globalitemMeta.setDisplayName (ChatColor.GREEN + "Top Global");


        for (int i = 0; i < 10; ++i) {
            final int index = i + 1;
            if (globalplayerDataList.get(i) == null) {
                if (globalplayerDataList.size() < 10) {
                    return;
                }
                continue;
            }
            OfflinePlayerData playerData = globalplayerDataList.get(i);
            OfflinePlayer player2 = Bukkit.getOfflinePlayer(playerData.getUniqueId());
            globallore.add(transltate("&f#" + index + " &a"  + player2.getName() + " &f(" + playerData.getGlobalStats("ELO") + ")"));
        }
        globalitemMeta.setLore(globallore);
        global.setItemMeta(globalitemMeta);
        inventory.addItem(global);


        for (Kit kit : Practice.getInstance().getKitManager().getKits()){

            ItemStack itemStack = kit.getIcon().clone();

            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = Lists.newArrayList();

            List<OfflinePlayerData> playerDataList = topsElo.get(kit.getName());

            for (int j = 0; j < 10; ++j) {
                final int index2 = j + 1;
                if (playerDataList.get(j) == null) {
                    if (globalplayerDataList.size() < 10) {
                        return;
                    }
                    continue;
                }
                OfflinePlayerData playerData = playerDataList.get(j);
                OfflinePlayer player2 = Bukkit.getOfflinePlayer(playerData.getUniqueId());
                lore.add(transltate("&f#" + index2 + " &a"  + player2.getName() + " &f(" + playerData.getElo(kit.getName()) + ")"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.addItem(itemStack);
        }

        player.openInventory(inventory);
    }

    public String transltate(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public OfflinePlayerData getOfflinePlayerData(OfflinePlayer offlinePlayer){

        OfflinePlayerData playerData = new OfflinePlayerData(offlinePlayer.getUniqueId());

        Config config = new Config("/players/" + playerData.getUniqueId().toString(), Practice.getInstance ());
        FileConfiguration fileConfig = config.getConfig();

        ConfigurationSection playerDataSelection = fileConfig.getConfigurationSection("playerdata");
        if (playerDataSelection != null) {
            if (playerDataSelection.getConfigurationSection("elo") != null) {
                playerDataSelection.getConfigurationSection("elo").getKeys(true).forEach((kit) -> {
                    int elo = playerDataSelection.getInt("elo." + kit);
                    playerData.setElo(kit, elo);
                });
            }

            if (playerDataSelection.getConfigurationSection("losses") != null) {
                playerDataSelection.getConfigurationSection("losses").getKeys(true).forEach((kit) -> {
                    int elo = playerDataSelection.getInt("losses." + kit);
                    playerData.setLosses(kit, elo);
                });
            }

            if (playerDataSelection.getConfigurationSection("wins") != null) {
                playerDataSelection.getConfigurationSection("wins").getKeys(false).forEach((kit) -> {
                    int elo = playerDataSelection.getInt("wins." + kit);
                    playerData.setWins(kit, elo);
                });
            }

        }

        return playerData;
    }

}
