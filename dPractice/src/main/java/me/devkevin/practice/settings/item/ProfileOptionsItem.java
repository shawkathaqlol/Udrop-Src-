package me.devkevin.practice.settings.item;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.inventory.UtilItem;

import java.util.ArrayList;
import java.util.List;

public enum ProfileOptionsItem {
    DUEL_REQUESTS(UtilItem.createItem(Material.LEASH, 1, (short) 1, ChatColor.WHITE.toString() + ChatColor.BOLD + "Duel Requests"), "Do you want to accept duel requests?"),
    PARTY_INVITES(UtilItem.createItem(Material.PAPER, 1, (short) 1, ChatColor.WHITE.toString() + ChatColor.BOLD + "Party Invites"), "Do you want to accept party invitations?"),
    TOGGLE_SCOREBOARD(UtilItem.createItem(Material.EMPTY_MAP, 1, (short) 8, ChatColor.WHITE.toString() + ChatColor.BOLD + "Toggle Scoreboard"), "Toggle your scoreboard"),
    ALLOW_SPECTATORS(UtilItem.createItem(Material.COMPASS, 1, (short) 1, ChatColor.WHITE.toString() + ChatColor.BOLD + "Allow Spectators"), "Allow players to spectate your matches?"),
    TOGGLE_TIME(UtilItem.createItem(Material.SLIME_BALL, 1, (short) 1, ChatColor.WHITE.toString() + ChatColor.BOLD + "Toggle Time"), "Toggle between day, sunset & night");

    private ItemStack item;
    private List<String> description;

    ProfileOptionsItem(final ItemStack item, final String description) {
        this.item = item;
        (this.description = new ArrayList<String>()).add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
        String parts = "";
        for (int i = 0; i < description.split(" ").length; ++i) {
            final String part = description.split(" ")[i];
            parts = parts + part + " ";
            if (i == 4 || i + 1 == description.split(" ").length) {
                this.description.add(ChatColor.GRAY + parts.trim());
                parts = "";
            }
        }
        this.description.add(" ");
    }

    public static ProfileOptionsItem fromItem(final ItemStack itemStack) {
        for (final ProfileOptionsItem item : values()) {
            for (final ProfileOptionsItemState state : ProfileOptionsItemState.values()) {
                if (item.getItem(state).isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }

    public ItemStack getItem(final ProfileOptionsItemState state) {
        if (this == ProfileOptionsItem.DUEL_REQUESTS || this == ProfileOptionsItem.PARTY_INVITES || this == ProfileOptionsItem.ALLOW_SPECTATORS) {
            final List<String> lore = new ArrayList<String>(this.description);
            lore.add("  " + ((state == ProfileOptionsItemState.ENABLED) ? (ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + ((state == ProfileOptionsItemState.DISABLED) ? (ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.DISABLED));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        }
        if (this == ProfileOptionsItem.TOGGLE_TIME) {
            final List<String> lore = new ArrayList<String>(this.description);
            lore.add("  " + ((state == ProfileOptionsItemState.DAY) ? (ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.DAY));
            lore.add("  " + ((state == ProfileOptionsItemState.SUNSET) ? (ChatColor.GOLD + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.SUNSET));
            lore.add("  " + ((state == ProfileOptionsItemState.NIGHT) ? (ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.NIGHT));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        }
        if (this == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
            final List<String> lore = new ArrayList<String>(this.description);
            lore.add("  " + ((state == ProfileOptionsItemState.ENABLED) ? (ChatColor.GREEN + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.ENABLED));
            lore.add("  " + ((state == ProfileOptionsItemState.SHOW_PING) ? (ChatColor.YELLOW + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.SHOW_PING));
            lore.add("  " + ((state == ProfileOptionsItemState.DISABLED) ? (ChatColor.RED + StringEscapeUtils.unescapeHtml4("&#9658;") + " ") : "  ") + ChatColor.GRAY + this.getOptionDescription(ProfileOptionsItemState.DISABLED));
            lore.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------");
            return new ItemBuilder(this.item).lore(lore).build();
        }
        return this.getItem(ProfileOptionsItemState.DISABLED);
    }

    public String getOptionDescription(final ProfileOptionsItemState state) {
        if (this == ProfileOptionsItem.DUEL_REQUESTS || this == ProfileOptionsItem.PARTY_INVITES || this == ProfileOptionsItem.ALLOW_SPECTATORS) {
            if (state == ProfileOptionsItemState.ENABLED) {
                return "Enable";
            }
            if (state == ProfileOptionsItemState.DISABLED) {
                return "Disable";
            }
        } else if (this == ProfileOptionsItem.TOGGLE_TIME) {
            if (state == ProfileOptionsItemState.DAY) {
                return "Day";
            }
            if (state == ProfileOptionsItemState.SUNSET) {
                return "Sunset";
            }
            if (state == ProfileOptionsItemState.NIGHT) {
                return "Night";
            }
        } else if (this == ProfileOptionsItem.TOGGLE_SCOREBOARD) {
            if (state == ProfileOptionsItemState.ENABLED) {
                return "Enable";
            }
            if (state == ProfileOptionsItemState.SHOW_PING) {
                return "Show Ping";
            }
            if (state == ProfileOptionsItemState.DISABLED) {
                return "Disable";
            }
        }
        return this.getOptionDescription(ProfileOptionsItemState.DISABLED);
    }
}
