package me.devkevin.practice.handler;

import gg.ragemc.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import me.devkevin.practice.CustomLocation;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.events.oitc.OITCEvent;
import me.devkevin.practice.events.oitc.OITCPlayer;
import me.devkevin.practice.events.redrover.RedroverEvent;
import me.devkevin.practice.events.redrover.RedroverPlayer;
import me.devkevin.practice.events.sumo.SumoEvent;
import me.devkevin.practice.events.sumo.SumoPlayer;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchState;
import me.devkevin.practice.player.PlayerData;
import me.devkevin.practice.player.PlayerState;
import me.devkevin.practice.util.BlockUtil;

import java.util.HashMap;
import java.util.UUID;

public class CustomMovementHandler implements MovementHandler {
    private static HashMap<Match, HashMap<UUID, CustomLocation>> parkourCheckpoints;

    static {
        CustomMovementHandler.parkourCheckpoints = new HashMap<>();
    }

    private final Practice plugin;

    public CustomMovementHandler() {
        this.plugin = Practice.getInstance();
    }

    public static HashMap<Match, HashMap<UUID, CustomLocation>> getParkourCheckpoints() {
        return CustomMovementHandler.parkourCheckpoints;
    }

    public void handleUpdateLocation(final Player player, final Location to, final Location from, final PacketPlayInFlying packetPlayInFlying) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s player data is null");
            return;
        }
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match == null) {
                return;
            }
            if (match.getKit().isSpleef() || match.getKit().isSumo()) {
                if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                    this.plugin.getMatchManager().removeFighter(player, playerData, true);
                }
                if ((to.getX() != from.getX() || to.getZ() != from.getZ()) && match.getMatchState() == MatchState.STARTING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
            }
            if (match.getKit().isParkour()) {
                if (BlockUtil.isStandingOn(player, Material.GOLD_PLATE)) {
                    for (final UUID uuid : this.plugin.getMatchManager().getOpponents(match, player)) {
                        final Player opponent = Bukkit.getPlayer(uuid);
                        if (opponent != null) {
                            this.plugin.getMatchManager().removeFighter(opponent, this.plugin.getPlayerManager().getPlayerData(opponent.getUniqueId()), true);
                        }
                    }
                    CustomMovementHandler.parkourCheckpoints.remove(match);
                } else if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
                    this.teleportToSpawnOrCheckpoint(match, player);
                } else if (BlockUtil.isStandingOn(player, Material.STONE_PLATE) || BlockUtil.isStandingOn(player, Material.IRON_PLATE) || BlockUtil.isStandingOn(player, Material.WOOD_PLATE)) {
                    boolean checkpoint = false;
                    if (!CustomMovementHandler.parkourCheckpoints.containsKey(match)) {
                        checkpoint = true;
                        CustomMovementHandler.parkourCheckpoints.put(match, new HashMap<UUID, CustomLocation>());
                    }
                    if (!CustomMovementHandler.parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
                        checkpoint = true;
                        CustomMovementHandler.parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
                    } else if (CustomMovementHandler.parkourCheckpoints.get(match).containsKey(player.getUniqueId()) && !BlockUtil.isSameLocation(player.getLocation(), CustomMovementHandler.parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation())) {
                        checkpoint = true;
                        CustomMovementHandler.parkourCheckpoints.get(match).put(player.getUniqueId(), CustomLocation.fromBukkitLocation(player.getLocation()));
                    }
                    if (checkpoint) {
                        player.sendMessage(ChatColor.GRAY + "Checkpoint has been saved.");
                    }
                }
                if ((to.getX() != from.getX() || to.getZ() != from.getZ()) && match.getMatchState() == MatchState.STARTING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
            }
        }
        final PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
        if (event != null) {
            if (event instanceof SumoEvent) {
                final SumoEvent sumoEvent = (SumoEvent) event;
                if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
            } else if (event instanceof OITCEvent) {
                final OITCEvent oitcEvent = (OITCEvent) event;
                if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.RESPAWNING) {
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
                if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.FIGHTING && player.getLocation().getBlockY() >= 90) {
                    oitcEvent.teleportNextLocation(player);
                    player.sendMessage(ChatColor.RED + "You have been teleported back to the arena.");
                }
            } else if (event instanceof RedroverEvent) {
                final RedroverEvent redroverEvent = (RedroverEvent) event;
                if (redroverEvent.getPlayer(player).getFightTask() != null && redroverEvent.getPlayer(player).getState() == RedroverPlayer.RedroverState.PREPARING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
            }
        }
    }

    public void handleUpdateRotation(final Player player, final Location location, final Location location1, final PacketPlayInFlying packetPlayInFlying) {
    }

    private void teleportToSpawnOrCheckpoint(final Match match, final Player player) {
        if (!CustomMovementHandler.parkourCheckpoints.containsKey(match)) {
            player.sendMessage(ChatColor.GRAY + "Teleporting back to the beginning.");
            player.teleport(match.getArena().getA().toBukkitLocation());
            return;
        }
        if (!CustomMovementHandler.parkourCheckpoints.get(match).containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.GRAY + "Teleporting back to the beginning.");
            player.teleport(match.getArena().getA().toBukkitLocation());
            return;
        }
        player.teleport(CustomMovementHandler.parkourCheckpoints.get(match).get(player.getUniqueId()).toBukkitLocation());
        player.sendMessage(ChatColor.GRAY + "Teleporting back to last checkpoint.");
    }
}
