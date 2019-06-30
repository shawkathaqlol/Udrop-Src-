package org.spigotmc;

import gg.ragemc.spigot.RageSpigot;
import gg.ragemc.spigot.util.DateUtil;
import java.lang.management.ManagementFactory;

import net.minecraft.server.DedicatedServer;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TicksPerSecondCommand extends Command {

	public TicksPerSecondCommand(String name) {
		super(name);
		this.description = "Gets the current ticks per second for the server";
		this.usageMessage = "/tps";
		this.setPermission("bukkit.command.tps");
	}

	private static String format(double tps) // PaperSpigot - made static
	{
		return ((tps / 2 > 18.0) ? ChatColor.GREEN : (tps / 2 > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString()
		       + ((tps / 2 > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, DedicatedServer.TPS);
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		// PaperSpigot start - Further improve tick handling
		double[] tps = org.bukkit.Bukkit.spigot().getTPS();

		String[] tpsAvg = new String[tps.length];

		for (int i = 0; i < tps.length; i++) {
			tpsAvg[i] = format(tps[i]);
		}

		int totalEntities = 0;

		for (World world : Bukkit.getServer().getWorlds()) {
			totalEntities += world.getEntities().size();
		}

		sender.sendMessage(
				RageSpigot.PRIMARY_COLOR + "TPS (1m, 5m, 15m): " +
				org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
		sender.sendMessage(
				RageSpigot.PRIMARY_COLOR + "Online: " + RageSpigot.VALUE_COLOR + Bukkit.getOnlinePlayers().size() +
				"/" + Bukkit.getMaxPlayers());
		sender.sendMessage(RageSpigot.PRIMARY_COLOR + "Memory: " + RageSpigot.VALUE_COLOR +
		                   ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2) / 1048576L +
		                   "/" + Runtime.getRuntime().totalMemory() / 1048576L + " MB");
		sender.sendMessage(RageSpigot.PRIMARY_COLOR + "Uptime: " + RageSpigot.VALUE_COLOR +
		                   DateUtil.formatDateDiff(ManagementFactory.getRuntimeMXBean().getStartTime()));
		sender.sendMessage(RageSpigot.PRIMARY_COLOR + "Entities: " + RageSpigot.VALUE_COLOR + totalEntities);
		sender.sendMessage(RageSpigot.PRIMARY_COLOR + "Last Tick Time: " + RageSpigot.VALUE_COLOR +
		                   (System.currentTimeMillis() - MinecraftServer.LAST_TICK_TIME) + "ms");

		return true;
	}
}
