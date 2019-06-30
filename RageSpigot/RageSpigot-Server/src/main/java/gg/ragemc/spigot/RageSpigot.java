package gg.ragemc.spigot;

import gg.ragemc.spigot.handler.MovementHandler;
import gg.ragemc.spigot.handler.PacketHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

public enum RageSpigot {

    INSTANCE;

	public static final String PRIMARY_COLOR = ChatColor.GOLD.toString();
	public static final String SECONDARY_COLOR = ChatColor.LIGHT_PURPLE.toString();
	public static final String VALUE_COLOR = ChatColor.GRAY.toString();

    private RageSpigotConfig config;
    private Set<PacketHandler> packetHandlers = new HashSet<>();
    private Set<MovementHandler> movementHandlers = new HashSet<>();

    public RageSpigotConfig getConfig() {
        return this.config;
    }

    public Set<PacketHandler> getPacketHandlers() {
        return this.packetHandlers;
    }

    public Set<MovementHandler> getMovementHandlers() {
        return this.movementHandlers;
    }

    public void setConfig(RageSpigotConfig config) {
        this.config = config;
    }

	public void addPacketHandler(PacketHandler handler) {
		this.packetHandlers.add(handler);
	}

	public void addMovementHandler(MovementHandler handler) {
		this.movementHandlers.add(handler);
	}

	public void registerCommands() {
		Map<String, Command> commands = new HashMap<>();

		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
		}
	}

}
