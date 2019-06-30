package me.devkevin.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventState;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.parkour.ParkourEvent;
import me.devkevin.practice.events.redrover.RedroverEvent;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;

import java.util.Arrays;

public class SpectateEventCommand extends Command {
    private final Practice plugin;

    public SpectateEventCommand() {
        super("eventspectate");
        this.plugin = Practice.getInstance();
        this.setDescription("Spectate an event.");
        this.setUsage(ChatColor.RED + "Usage: /eventspectate <event>");
        this.setAliases(Arrays.asList("eventspec", "specevent"));
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }
        final PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);
        if (event == null) {
            player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
            return true;
        }
        if (event.getState() != EventState.STARTED) {
            player.sendMessage(ChatColor.RED + "That event hasn't started, please wait.");
            return true;
        }
        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are already spectating this event.");
                return true;
            }
            this.plugin.getEventManager().removeSpectator(player);
        }
        player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + event.getName() + " Event" + ChatColor.GREEN + ".");
        if (event instanceof SumoEvent) {
            this.plugin.getEventManager().addSpectatorSumo(player, playerData, (SumoEvent) event);
        } else if (event instanceof OITCEvent) {
            this.plugin.getEventManager().addSpectatorOITC(player, playerData, (OITCEvent) event);
        } else if (event instanceof ParkourEvent) {
            this.plugin.getEventManager().addSpectatorParkour(player, playerData, (ParkourEvent) event);
        } else if (event instanceof RedroverEvent) {
            this.plugin.getEventManager().addSpectatorRedrover(player, playerData, (RedroverEvent) event);
        }
        return true;
    }
}
