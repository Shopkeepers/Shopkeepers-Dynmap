package de.blablubbabc.shopkeepers.dynmap.command;

import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPermission;
import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPlugin;
import de.blablubbabc.shopkeepers.dynmap.command.lib.RootCommand;

class ShopkeepersDynmapCommand extends RootCommand {

	private static final String COMMAND_NAME = "shopkeepers-dynmap";

	public static ShopkeepersDynmapCommand register(ShopkeepersDynmapPlugin plugin) {
		var bukkitCommand = plugin.getCommand(COMMAND_NAME);
		return new ShopkeepersDynmapCommand(plugin, bukkitCommand);
	}

	private ShopkeepersDynmapCommand(
			ShopkeepersDynmapPlugin plugin,
			org.bukkit.command.PluginCommand bukkitCommand
	) {
		super(
				plugin,
				bukkitCommand,
				ShopkeepersDynmapPermission.HELP
		);

		this.addSubCommand(new HelpCommand(this));
		this.addSubCommand(new ReloadCommand(plugin));
		this.addSubCommand(new UpdateIconsCommand(plugin));
	}
}
