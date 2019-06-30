package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

public class VersionCommand extends BukkitCommand {

	private static final String PRIMARY_COLOR = ChatColor.WHITE.toString(), VALUE_COLOR = ChatColor.WHITE
			.toString();

	public VersionCommand(String name) {
		super(name);

		this.description = "Gets the version of this server including any plugins in use";
		this.usageMessage = "/version [plugin name]";
		this.setPermission("bukkit.command.version");
		this.setAliases(Arrays.asList("ver", "about"));
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(VersionCommand.PRIMARY_COLOR + "This server is running " + VersionCommand.VALUE_COLOR +
			                   Bukkit.getName() +
			                   VersionCommand.PRIMARY_COLOR + " version " + VersionCommand.VALUE_COLOR +
			                   Bukkit.getVersion() +
			                   VersionCommand.PRIMARY_COLOR + " (Implementing API version " +
			                   VersionCommand.VALUE_COLOR +
			                   Bukkit.getBukkitVersion() + VersionCommand.PRIMARY_COLOR + ")");
			// sendVersion(sender);
		} else {
			StringBuilder name = new StringBuilder();

			for (String arg : args) {
				if (name.length() > 0) {
					name.append(' ');
				}

				name.append(arg);
			}

			String pluginName = name.toString();
			Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
			if (exactPlugin != null) {
				describeToSender(exactPlugin, sender);
				return true;
			}

			boolean found = false;
			pluginName = pluginName.toLowerCase();
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if (plugin.getName().toLowerCase().contains(pluginName)) {
					describeToSender(plugin, sender);
					found = true;
				}
			}

			if (!found && sender.isOp()) {
				sender.sendMessage(
						VersionCommand.PRIMARY_COLOR + "This server is not running any plugin by that name.");
				sender.sendMessage(VersionCommand.PRIMARY_COLOR + "Use /plugins to get a list of plugins.");
			}
		}
		return true;
	}

	private void describeToSender(Plugin plugin, CommandSender sender) {
		PluginDescriptionFile desc = plugin.getDescription();
		sender.sendMessage(
				VersionCommand.VALUE_COLOR + desc.getName() + VersionCommand.PRIMARY_COLOR + " version " +
				VersionCommand.VALUE_COLOR +
				desc.getVersion());

		if (desc.getDescription() != null) {
			sender.sendMessage(desc.getDescription());
		}

		if (desc.getWebsite() != null) {
			sender.sendMessage(
					VersionCommand.PRIMARY_COLOR + "Website: " + VersionCommand.VALUE_COLOR + desc.getWebsite());
		}

		if (!desc.getAuthors().isEmpty()) {
			if (desc.getAuthors().size() == 1) {
				sender.sendMessage(VersionCommand.PRIMARY_COLOR + "Author: " + getAuthors(desc));
			} else {
				sender.sendMessage(VersionCommand.PRIMARY_COLOR + "Authors: " + getAuthors(desc));
			}
		}
	}

	private String getAuthors(final PluginDescriptionFile desc) {
		StringBuilder result = new StringBuilder();
		List<String> authors = desc.getAuthors();

		for (int i = 0; i < authors.size(); i++) {
			if (result.length() > 0) {
				result.append(VersionCommand.PRIMARY_COLOR);

				if (i < authors.size() - 1) {
					result.append(", ");
				} else {
					result.append(" and ");
				}
			}

			result.append(VersionCommand.VALUE_COLOR);
			result.append(authors.get(i));
		}

		return result.toString();
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		Validate.notNull(sender, "Sender cannot be null");
		Validate.notNull(args, "Arguments cannot be null");
		Validate.notNull(alias, "Alias cannot be null");

		if (args.length == 1) {
			List<String> completions = new ArrayList<String>();
			String toComplete = args[0].toLowerCase();
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
					completions.add(plugin.getName());
				}
			}
			return completions;
		}
		return ImmutableList.of();
	}
}
