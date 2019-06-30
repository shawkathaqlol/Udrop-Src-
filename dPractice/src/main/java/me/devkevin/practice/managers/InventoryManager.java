package me.devkevin.practice.managers;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.inventory.InventorySnapshot;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.StringUtil;
import me.devkevin.practice.util.inventory.InventoryUI;

import java.util.*;

public class InventoryManager {
    private static final String MORE_PLAYERS;

    static {
        MORE_PLAYERS = ChatColor.RED + "There must be at least 2 players in your party to do this.";
    }

    private final Practice plugin;
    private final InventoryUI unrankedInventory;
    private final InventoryUI rankedInventory;
    private final InventoryUI editorInventory;
    private final InventoryUI duelInventory;
    private final InventoryUI partySplitInventory;
    private final InventoryUI partyFFAInventory;
    private final InventoryUI partyEventInventory;
    private final InventoryUI partyInventory;
    private final Map<String, InventoryUI> duelMapInventories;
    private final Map<String, InventoryUI> partySplitMapInventories;
    private final Map<String, InventoryUI> partyFFAMapInventories;
    private final Map<UUID, InventoryUI> editorInventories;
    private final Map<UUID, InventorySnapshot> snapshots;

    public InventoryManager() {
        this.plugin = Practice.getInstance();
        this.unrankedInventory = new InventoryUI("Unranked Queue", true, 2);
        this.rankedInventory = new InventoryUI("Ranked Queue", true, 2);
        this.editorInventory = new InventoryUI("Kit Editor", true, 2);
        this.duelInventory = new InventoryUI("Send Request", true, 2);
        this.partySplitInventory = new InventoryUI("Split Fights", true, 2);
        this.partyFFAInventory = new InventoryUI("Party FFA", true, 2);
        this.partyEventInventory = new InventoryUI("Party Events", true, 1);
        this.partyInventory = new InventoryUI("Fight Other Parties", true, 6);
        this.duelMapInventories = new HashMap<>();
        this.partySplitMapInventories = new HashMap<>();
        this.partyFFAMapInventories = new HashMap<>();
        this.editorInventories = new HashMap<UUID, InventoryUI>();
        this.snapshots = new HashMap<UUID, InventorySnapshot>();
        this.setupInventories();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateInventories, 20L, 20L);
    }

    private void setupInventories() {
        final Collection<Kit> kits = this.plugin.getKitManager().getKits();
        for (final Kit kit : kits) {
            if (kit.isEnabled()) {
                this.unrankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player) event.getWhoClicked();
                        InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.UNRANKED);
                    }
                });
                if (kit.isRanked()) {
                    this.rankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            final Player player = (Player) event.getWhoClicked();
                            InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.RANKED);
                        }
                    });
                }
                this.editorInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        final Player player = (Player) event.getWhoClicked();
                        if (kit.getKitEditContents()[0] == null) {
                            player.sendMessage(ChatColor.RED + "This kit is not editable.");
                            player.closeInventory();
                            return;
                        }
                        InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
                        InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).setPlayerState(PlayerState.EDITING);
                    }
                });
                this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelClick((Player) event.getWhoClicked(), kit);
                    }
                });
                this.partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitClick((Player) event.getWhoClicked(), kit);
                    }
                });
                this.partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleFFAClick((Player) event.getWhoClicked(), kit);
                    }
                });
            }
        }
        this.partyEventInventory.setItem(3, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.FIREWORK_CHARGE, ChatColor.RED + "Split Fights")) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                final Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
            }
        });
        this.partyEventInventory.setItem(5, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.SLIME_BALL, ChatColor.AQUA + "Party FFA")) {
            @Override
            public void onClick(final InventoryClickEvent event) {
                final Player player = (Player) event.getWhoClicked();
                player.closeInventory();
                player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
            }
        });
        for (final Kit kit : this.plugin.getKitManager().getKits()) {
            final InventoryUI duelInventory = new InventoryUI("Select Arena", true, 6);
            final InventoryUI partySplitInventory = new InventoryUI("Select Arena", true, 6);
            final InventoryUI partyFFAInventory = new InventoryUI("Select Arena", true, 6);
            for (final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
                if (!arena.isEnabled()) {
                    continue;
                }
                if (kit.getExcludedArenas().contains(arena.getName())) {
                    continue;
                }
                if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
                    continue;
                }
                final ItemStack book = ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + arena.getName());
                duelInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDuelMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
                partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartySplitMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
                partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handlePartyFFAMapClick((Player) event.getWhoClicked(), arena, kit);
                    }
                });
            }
            this.duelMapInventories.put(kit.getName(), duelInventory);
            this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
            this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
        }
    }

    private void updateInventories() {
        for (int i = 0; i < 18; ++i) {
            final InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
            if (unrankedItem != null) {
                unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
                this.unrankedInventory.setItem(i, unrankedItem);
            }
            final InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
            if (rankedItem != null) {
                rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
                this.rankedInventory.setItem(i, rankedItem);
            }
        }
    }

    private ItemStack updateQueueLore(final ItemStack itemStack, final QueueType type) {
        if (itemStack == null) {
            return null;
        }
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            final String ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
            final int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
            final int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);
            return ItemUtil.reloreItem(itemStack, ChatColor.YELLOW + "In Queue: " + ChatColor.GREEN + queueSize, ChatColor.YELLOW + "In Game: " + ChatColor.GREEN + inGameSize);
        }
        return null;
    }

    private void addToQueue(final Player player, final PlayerData playerData, final Kit kit, final Party party, final QueueType queueType) {
        if (kit != null) {
            if (party == null) {
                this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
            } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType);
            }
        }
    }

    public void addSnapshot(final InventorySnapshot snapshot) {
        this.snapshots.put(snapshot.getSnapshotId(), snapshot);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.removeSnapshot(snapshot.getSnapshotId()), 600L);
    }

    public void removeSnapshot(final UUID snapshotId) {
        final InventorySnapshot snapshot = this.snapshots.get(snapshotId);
        if (snapshot != null) {
            this.snapshots.remove(snapshotId);
        }
    }

    public InventorySnapshot getSnapshot(final UUID snapshotId) {
        return this.snapshots.get(snapshotId);
    }

    public void addParty(final Player player) {
        final ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.GOLD + player.getName() + " (" + ChatColor.GREEN + "1" + ChatColor.GOLD + ")");
        this.partyInventory.addItem(new InventoryUI.AbstractClickableItem(skull) {
            @Override
            public void onClick(final InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                if (inventoryClickEvent.getWhoClicked() instanceof Player) {
                    final Player sender = (Player) inventoryClickEvent.getWhoClicked();
                    sender.performCommand("duel " + player.getName());
                }
            }
        });
    }

    public void updateParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < this.partyInventory.getSize(); ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    final List<String> lores = new ArrayList<String>();
                    party.members().forEach(member -> lores.add(ChatColor.RED + member.getName()));
                    ItemUtil.reloreItem(stack, (String[]) lores.toArray(new String[0]));
                    ItemUtil.renameItem(stack, ChatColor.GOLD + player.getName() + " (" + ChatColor.GREEN + party.getMembers().size() + ChatColor.GOLD + ")");
                    item.setItemStack(stack);
                    break;
                }
            }
        }
    }

    public void removeParty(final Party party) {
        final Player player = this.plugin.getServer().getPlayer(party.getLeader());
        for (int i = 0; i < this.partyInventory.getSize(); ++i) {
            final InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
            if (item != null) {
                final ItemStack stack = item.getItemStack();
                if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
                    this.partyInventory.removeItem(i);
                    break;
                }
            }
        }
    }

    public void addEditingKitInventory(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());
        final InventoryUI inventory = new InventoryUI("Managing Kit Layout", true, 4);
        for (int i = 1; i <= 7; ++i) {
            final ItemStack save = ItemUtil.createItem(Material.CHEST, ChatColor.YELLOW + "Save Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack load = ItemUtil.createItem(Material.BOOK, ChatColor.YELLOW + "Load Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack rename = ItemUtil.createItem(Material.NAME_TAG, ChatColor.YELLOW + "Rename Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            final ItemStack delete = ItemUtil.createItem(Material.FLINT, ChatColor.YELLOW + "Delete Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
            inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
                @Override
                public void onClick(final InventoryClickEvent event) {
                    final int kitIndex = event.getSlot();
                    InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
                    inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                        }
                    });
                    inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                        @Override
                        public void onClick(final InventoryClickEvent event) {
                            InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                        }
                    });
                }
            });
            final int kitIndex = i;
            if (kitMap != null && kitMap.containsKey(kitIndex)) {
                inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                    }
                });
                inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                    @Override
                    public void onClick(final InventoryClickEvent event) {
                        InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                    }
                });
            }
        }
        this.editorInventories.put(player.getUniqueId(), inventory);
    }

    public void removeEditingKitInventory(final UUID uuid) {
        final InventoryUI inventoryUI = this.editorInventories.get(uuid);
        if (inventoryUI != null) {
            this.editorInventories.remove(uuid);
        }
    }

    public InventoryUI getEditingKitInventory(final UUID uuid) {
        return this.editorInventories.get(uuid);
    }

    private void handleSavingKit(final Player player, final PlayerData playerData, final Kit kit, final Map<Integer, PlayerKit> kitMap, final int kitIndex) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
            player.sendMessage(ChatColor.GREEN + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.RED + ".");
            return;
        }
        final PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(), kit.getName() + " Kit " + kitIndex);
        playerData.addPlayerKit(kitIndex, playerKit);
        player.sendMessage(ChatColor.GREEN + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.RED + ".");
    }

    private void handleLoadKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            final ItemStack[] contents2;
            final ItemStack[] contents = contents2 = kitMap.get(kitIndex).getContents();
            for (final ItemStack itemStack : contents2) {
                if (itemStack != null && itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
            player.getInventory().setContents(contents);
            player.updateInventory();
        }
    }

    private void handleRenamingKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Enter the name you want this kit to be (You can enter chat colors).");
        }
    }

    private void handleDeleteKit(final Player player, final int kitIndex, final Map<Integer, PlayerKit> kitMap, final InventoryUI inventory) {
        if (kitMap != null && kitMap.containsKey(kitIndex)) {
            this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());
            kitMap.remove(kitIndex);
            player.sendMessage(ChatColor.GREEN + "Successfully removed kit " + ChatColor.RED + kitIndex + ChatColor.RED + ".");
            inventory.setItem(kitIndex + 1, 2, null);
            inventory.setItem(kitIndex + 1, 3, null);
            inventory.setItem(kitIndex + 1, 4, null);
        }
    }

    private void handleDuelClick(final Player player, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "That player is currently busy.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(ChatColor.RED + "That player is not in a party.");
            return;
        }
        player.closeInventory();
        player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
    }

    private void handlePartySplitClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        } else {
            player.closeInventory();
            player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
        }
    }

    private void handleFFAClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        } else {
            player.closeInventory();
            player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
        }
    }

    private void handleRedroverClick(final Player player, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 4) {
            player.sendMessage(ChatColor.RED + "There must be at least 4 players in your party to do this.");
        } else {
            final Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
                player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
                return;
            }
            this.createRedroverMatch(party, arena, kit);
        }
    }

    private void handleDuelMapClick(final Player player, final Arena arena, final Kit kit) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
        if (selected == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
            return;
        }
        final PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
        if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "That player is currently busy.");
            return;
        }
        final Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final boolean partyDuel = party != null;
        if (partyDuel && targetParty == null) {
            player.sendMessage(ChatColor.RED + "That player is not in a party.");
            return;
        }
        if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "You have already sent a duel request to this player, please wait.");
            return;
        }
        this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
    }

    private void handleRedroverMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 4) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        } else {
            this.createRedroverMatch(party, arena, kit);
        }
    }

    private void handlePartyFFAMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        } else {
            this.createFFAMatch(party, arena, kit);
        }
    }

    private void handlePartySplitMapClick(final Player player, final Arena arena, final Kit kit) {
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            return;
        }
        player.closeInventory();
        if (party.getMembers().size() < 2) {
            player.sendMessage(InventoryManager.MORE_PLAYERS);
        } else {
            this.createPartySplitMatch(party, arena, kit);
        }
    }

    private void sendDuel(final Player player, final Player selected, final Kit kit, final boolean partyDuel, final Party party, final Party targetParty, final Arena arena) {
        this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);
        player.closeInventory();
        final String requestGetString = player.getName() + (partyDuel ? (ChatColor.YELLOW + "'s party " + ChatColor.GREEN + "(" + party.getMembers().size() + ")") : "") + ChatColor.YELLOW + " has requested a duel with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ". " + ChatColor.GRAY + "[Click to Accept]";
        final String requestSendString = ChatColor.YELLOW + "Sent a duel request to " + ChatColor.GREEN + selected.getName() + (partyDuel ? (ChatColor.YELLOW + "'s party " + ChatColor.GREEN + "(" + party.getMembers().size() + ")") : "") + ChatColor.YELLOW + " with the kit " + ChatColor.DARK_GREEN + kit.getName() + ChatColor.YELLOW + ".";
        final Clickable requestMessage = new Clickable(requestGetString, ChatColor.GRAY + "Click to accept duel", "/accept " + player.getName() + " " + kit.getName());
        if (partyDuel) {
            targetParty.members().forEach(requestMessage::sendToPlayer);
            party.broadcast(requestSendString);
        } else {
            requestMessage.sendToPlayer(selected);
            player.sendMessage(requestSendString);
        }
    }

    private void createPartySplitMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "Starting a Split Party match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    private void createFFAMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList((Iterable) party.getMembers()), 0);
        final Match match = new Match(arena, kit, QueueType.UNRANKED, team);
        match.broadcast(ChatColor.YELLOW + "Starting a Party FFA match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    private void createRedroverMatch(final Party party, final Arena arena, final Kit kit) {
        final MatchTeam[] teams = party.split();
        final Match match = new Match(arena, kit, QueueType.UNRANKED, true, teams);
        final Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
        final Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
        match.broadcast(ChatColor.YELLOW + "Starting a Redrover match with kit " + ChatColor.GREEN + kit.getName() + ".");
        this.plugin.getMatchManager().createMatch(match);
    }

    public InventoryUI getUnrankedInventory() {
        return this.unrankedInventory;
    }

    public InventoryUI getRankedInventory() {
        return this.rankedInventory;
    }

    public InventoryUI getEditorInventory() {
        return this.editorInventory;
    }

    public InventoryUI getDuelInventory() {
        return this.duelInventory;
    }

    public InventoryUI getPartySplitInventory() {
        return this.partySplitInventory;
    }

    public InventoryUI getPartyFFAInventory() {
        return this.partyFFAInventory;
    }

    public InventoryUI getPartyEventInventory() {
        return this.partyEventInventory;
    }

    public InventoryUI getPartyInventory() {
        return this.partyInventory;
    }
}
