package me.devkevin.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.kit.PlayerKit;
import me.devkevin.practice.util.PlayerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorManager {
    private final Practice plugin;
    private final Map<UUID, String> editing;
    private final Map<UUID, PlayerKit> renaming;

    public EditorManager() {
        this.plugin = Practice.getInstance();
        this.editing = new HashMap<>();
        this.renaming = new HashMap<>();
    }

    public void addEditor(final Player player, final Kit kit) {
        this.editing.put(player.getUniqueId(), kit.getName());
        this.plugin.getInventoryManager().addEditingKitInventory(player, kit);
        PlayerUtil.clearPlayer(player);
        player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
        player.getInventory().setContents(kit.getContents());
        player.sendMessage(ChatColor.GREEN + "You are editing kit " + ChatColor.YELLOW + kit.getName() + ChatColor.GREEN + ".");
    }

    public void removeEditor(final UUID editor) {
        this.renaming.remove(editor);
        this.editing.remove(editor);
        this.plugin.getInventoryManager().removeEditingKitInventory(editor);
    }

    public String getEditingKit(final UUID editor) {
        return this.editing.get(editor);
    }

    public void addRenamingKit(final UUID uuid, final PlayerKit playerKit) {
        this.renaming.put(uuid, playerKit);
    }

    public void removeRenamingKit(final UUID uuid) {
        this.renaming.remove(uuid);
    }

    public PlayerKit getRenamingKit(final UUID uuid) {
        return this.renaming.get(uuid);
    }
}
