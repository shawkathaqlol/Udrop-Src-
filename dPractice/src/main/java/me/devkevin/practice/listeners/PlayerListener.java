package me.devkevin.practice.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.events.parkour.ParkourEvent;
import me.devkevin.practice.ffa.killstreak.KillStreak;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.PlayerUtil;

import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final Practice plugin;

    public PlayerListener() {
        this.plugin = Practice.getInstance();
    }

    @EventHandler
    public void onPlayerInteractSoup(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.isDead() && player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < 19.0) {
            final double newHealth = (player.getHealth() + 7.0 > 20.0) ? 20.0 : (player.getHealth() + 7.0);
            player.setHealth(newHealth);
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                return;
            }
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                final Player player = event.getPlayer();
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
            }
        }
    }

    @EventHandler
    public void onRegenerate(final EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }
        final Player player = (Player) event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isBuild()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getPlayerManager().createPlayerData(player);
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return;
        }
        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            case SPECTATING:
                if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                    break;
                }
                this.plugin.getMatchManager().removeSpectator(player);
                break;
            case EDITING:
                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueueManager().removePlayerFromQueue(player);
                    break;
                }
                if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueueManager().removePartyFromQueue(party);
                    break;
                }
                break;
            case FFA:
                this.plugin.getFfaManager().removePlayer(player);
                break;
            case EVENT: {
                final PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) {
                    practiceEvent.leave(player);
                    break;
                }
                break;
            }
        }
        this.plugin.getTournamentManager().leaveTournament(player);
        this.plugin.getPartyManager().leaveParty(player);
        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return;
        }
        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            case SPECTATING:
                if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                    this.plugin.getEventManager().removeSpectator(player);
                    break;
                }
                this.plugin.getMatchManager().removeSpectator(player);
                break;
            case EDITING:
                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                break;
            case QUEUE:
                if (party == null) {
                    this.plugin.getQueueManager().removePlayerFromQueue(player);
                    break;
                }
                if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueueManager().removePartyFromQueue(party);
                    break;
                }
                break;
            case FFA:
                this.plugin.getFfaManager().removePlayer(player);
                break;
            case EVENT: {
                final PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) {
                    practiceEvent.leave(player);
                    break;
                }
                break;
            }
        }
        this.plugin.getTournamentManager().leaveTournament(player);
        this.plugin.getPartyManager().leaveParty(player);
        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
        }
        if (event.getAction().name().endsWith("_BLOCK")) {
            if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign) {
                final Sign sign = (Sign) event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
                    event.setCancelled(true);
                    final Inventory inventory = this.plugin.getServer().createInventory(null, 54, ChatColor.DARK_GRAY + "Soup Refill");
                    for (int i = 0; i < 54; ++i) {
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }
                    event.getPlayer().openInventory(inventory);
                }
            }
            if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }
        Label_1649:
        {
            if (event.getAction().name().startsWith("RIGHT_")) {
                final ItemStack item = event.getItem();
                final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                Label_1447:
                {
                    switch (playerData.getPlayerState()) {
                        case LOADING:
                            player.sendMessage(ChatColor.RED + "Please wait until your player data is loaded.");
                            break;
                        case FIGHTING: {
                            if (item == null) {
                                return;
                            }
                            final Match match = this.plugin.getMatchManager().getMatch(playerData);
                            switch (item.getType()) {
                                case ENDER_PEARL:
                                    if (match.getMatchState() == MatchState.STARTING) {
                                        event.setCancelled(true);
                                        player.sendMessage(ChatColor.RED + "You can't throw enderpearls in your current state!");
                                        player.updateInventory();
                                        break;
                                    }
                                    break;
                                case ENCHANTED_BOOK: {
                                    final Kit kit = match.getKit();
                                    final PlayerInventory inventory2 = player.getInventory();
                                    final int kitIndex = inventory2.getHeldItemSlot();
                                    if (kitIndex == 8) {
                                        kit.applyToPlayer(player);
                                        break;
                                    }
                                    final Map<Integer, PlayerKit> kits = playerData.getPlayerKits(kit.getName());
                                    final PlayerKit playerKit = kits.get(kitIndex + 1);
                                    if (playerKit != null) {
                                        playerKit.applyToPlayer(player);
                                        break;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case SPAWN:
                            if (item == null) {
                                return;
                            }
                            switch (item.getType()) {
                                case DIAMOND_SWORD:
                                    if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                        return;
                                    }
                                    player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
                                    break;
                                case IRON_SWORD:
                                    if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                        return;
                                    }
                                    player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
                                    break;
                                case BLAZE_POWDER: {
                                    final UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
                                    final Player rematcher = this.plugin.getServer().getPlayer(rematching);
                                    if (rematcher == null) {
                                        player.sendMessage(ChatColor.RED + "That player is not online.");
                                        return;
                                    }
                                    if (this.plugin.getMatchManager().getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
                                        this.plugin.getServer().dispatchCommand(player, "accept " + rematcher.getName());
                                        break;
                                    }
                                    this.plugin.getServer().dispatchCommand(player, "duel " + rematcher.getName());
                                    break;
                                }
                                case IRON_AXE:
                                    this.plugin.getFfaManager().addPlayer(player);
                                    break;
                                case NAME_TAG:
                                    this.plugin.getPartyManager().createParty(player);
                                    break;
                                case BOOK:
                                    player.openInventory(this.plugin.getInventoryManager().getEditorInventory().getCurrentPage());
                                    break;
                                case SKULL_ITEM:
                                    player.performCommand("party info");
                                    break;
                                case WATCH:
                                    player.performCommand("settings");
                                    break;
                                case GOLD_AXE:
                                    if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                        return;
                                    }
                                    player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
                                    break;
                                case PAPER:
                                    if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                        player.sendMessage(ChatColor.RED + "You are not the leader of this party.");
                                        return;
                                    }
                                    player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
                                    break;
                                case NETHER_STAR:
                                    this.plugin.getPartyManager().leaveParty(player);
                                    this.plugin.getTournamentManager().leaveTournament(player);
                                    break;
                            }
                            break;
                        case QUEUE:
                            if (item == null) {
                                return;
                            }
                            if (item.getType() != Material.REDSTONE) {
                                break;
                            }
                            if (party == null) {
                                this.plugin.getQueueManager().removePlayerFromQueue(player);
                                break;
                            }
                            this.plugin.getQueueManager().removePartyFromQueue(party);
                            break;
                        case EVENT: {
                            if (item == null) {
                                return;
                            }
                            final PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                            if (item.getType() == Material.NETHER_STAR) {
                                if (practiceEvent != null) {
                                    practiceEvent.leave(player);
                                    break;
                                }
                                break;
                            } else {
                                if (item.getType() == Material.FIREBALL && practiceEvent != null && practiceEvent instanceof ParkourEvent) {
                                    ((ParkourEvent) practiceEvent).toggleVisibility(player);
                                    break;
                                }
                                break;
                            }
                        }
                        case SPECTATING:
                            if (item == null) {
                                return;
                            }
                            if (item.getType() != Material.NETHER_STAR) {
                                break Label_1447;
                            }
                            if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                                this.plugin.getEventManager().removeSpectator(player);
                                break Label_1447;
                            }
                            if (party == null) {
                                this.plugin.getMatchManager().removeSpectator(player);
                                break Label_1447;
                            }
                            this.plugin.getPartyManager().leaveParty(player);
                            break Label_1447;
                        case EDITING:
                            if (event.getClickedBlock() == null) {
                                return;
                            }
                            switch (event.getClickedBlock().getType()) {
                                case WALL_SIGN:
                                case SIGN:
                                case SIGN_POST:
                                    this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                                    this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                                    break Label_1649;
                                case CHEST: {
                                    final Kit kit2 = this.plugin.getKitManager().getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
                                    if (kit2.getKitEditContents()[0] != null) {
                                        final Inventory editorInventory = this.plugin.getServer().createInventory(null, 36);
                                        editorInventory.setContents(kit2.getKitEditContents());
                                        player.openInventory(editorInventory);
                                        event.setCancelled(true);
                                        break Label_1649;
                                    }
                                    break Label_1649;
                                }
                                case ANVIL:
                                    player.openInventory(this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
                                    event.setCancelled(true);
                                    break Label_1649;
                            }
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Material drop = event.getItemDrop().getItemStack().getType();
        switch (playerData.getPlayerState()) {
            case FFA:
                if (drop != Material.BOWL) {
                    event.setCancelled(true);
                    break;
                }
                event.getItemDrop().remove();
                break;
            case FIGHTING: {
                if (drop == Material.ENCHANTED_BOOK) {
                    event.setCancelled(true);
                    break;
                }
                if (drop == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                    break;
                }
                if (drop == Material.DIAMOND_SWORD || drop == Material.IRON_AXE || drop == Material.DIAMOND_SPADE || drop == Material.BOW) {
                    event.setCancelled(true);
                    break;
                }
                final Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());
                this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
                break;
            }
            default:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(final PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Material drop = event.getItem().getType();
        switch (playerData.getPlayerState()) {
            case FIGHTING:
            case EVENT:
                if (drop.getId() == 373) {
                    this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                        player.setItemInHand(new ItemStack(Material.AIR));
                        player.updateInventory();
                    }, 1L);
                    break;
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getEntitiesToRemove().contains(event.getItem())) {
                match.removeEntityToRemove(event.getItem());
            } else {
                event.setCancelled(true);
            }
        } else if (playerData.getPlayerState() != PlayerState.FFA) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final String chatMessage = event.getMessage();
        if (party != null) {
            if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
                event.setCancelled(true);
                final String message = ChatColor.GOLD + "(Party) " + ChatColor.WHITE + player.getName() + ChatColor.GRAY + ": " + chatMessage.replaceFirst("!", "").replaceFirst("@", "");
                party.broadcast(message);
            }
        } else {
            final PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());
            if (kitRenaming != null) {
                kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes('&', chatMessage));
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully set kit " + ChatColor.YELLOW + kitRenaming.getIndex() + ChatColor.GREEN + "'s name to " + ChatColor.YELLOW + kitRenaming.getDisplayName());
                this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        switch (playerData.getPlayerState()) {
            case EVENT: {
                final PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (currentEvent != null && currentEvent instanceof OITCEvent) {
                    event.setRespawnLocation(player.getLocation());
                    currentEvent.onDeath().accept(player);
                    break;
                }
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        switch (playerData.getPlayerState()) {
            case FIGHTING:
                this.plugin.getMatchManager().removeFighter(player, playerData, true);
                break;
            case EVENT: {
                final PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (currentEvent == null) {
                    break;
                }
                if (currentEvent instanceof OITCEvent) {
                    final OITCEvent oitcEvent = (OITCEvent) currentEvent;
                    final OITCPlayer oitcKiller = oitcEvent.getPlayer(player.getKiller());
                    final OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
                    oitcPlayer.setLastKiller(oitcKiller);
                    PlayerUtil.respawnPlayer(event);
                    break;
                }
                currentEvent.onDeath().accept(player);
                break;
            }
            case FFA: {
                for (final ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
                        this.plugin.getFfaManager().getItemTracker().put(player.getWorld().dropItemNaturally(player.getLocation(), item), System.currentTimeMillis());
                    }
                }
                this.plugin.getFfaManager().getKillStreakTracker().put(player.getUniqueId(), 0);
                String deathMessage;
                if (player.getKiller() == null) {
                    deathMessage = ChatColor.RED + player.getName() + ChatColor.RED + " has been eliminated.";
                } else {
                    deathMessage = ChatColor.RED + player.getName() + ChatColor.RED + " has been eliminated by " + ChatColor.GREEN + player.getKiller().getName();
                    final int ks = this.plugin.getFfaManager().getKillStreakTracker().compute(player.getKiller().getUniqueId(), (k, v) -> ((v == null) ? 0 : v) + 1);
                    for (final KillStreak killStreak : this.plugin.getFfaManager().getKillStreaks()) {
                        if (killStreak.getStreaks().contains(ks)) {
                            killStreak.giveKillStreak(player.getKiller());
                            for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
                                if (data.getPlayerState() == PlayerState.FFA) {
                                    deathMessage = deathMessage + "\n" + ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.GREEN + player.getKiller().getName() + ChatColor.GRAY + " is on a " + ChatColor.RED.toString() + ChatColor.BOLD + ks + ChatColor.GRAY + " kill streak.";
                                }
                            }
                            break;
                        }
                    }
                }
                for (final PlayerData data2 : this.plugin.getPlayerManager().getAllData()) {
                    if (data2.getPlayerState() == PlayerState.FFA) {
                        final Player ffaPlayer = this.plugin.getServer().getPlayer(data2.getUniqueId());
                        ffaPlayer.sendMessage(deathMessage);
                    }
                }
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getFfaManager().removePlayer(event.getEntity()));
                break;
            }
        }
        event.setDroppedExp(0);
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isParkour() || match.getKit().isSumo() || this.plugin.getEventManager().getEventPlaying(player) != null) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
            if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
                final Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
                match.addEntityToRemove(event.getEntity());
            }
        }
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player) event.getEntity().getShooter();
            final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
            if (shooterData != null && shooterData.getPlayerState() == PlayerState.FIGHTING) {
                final Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
                match.removeEntityToRemove(event.getEntity());
                if (event.getEntityType() == EntityType.ARROW) {
                    event.getEntity().remove();
                }
            }
        }
    }
}
