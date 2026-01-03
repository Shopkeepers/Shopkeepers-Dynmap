package de.blablubbabc.shopkeepers.dynmap.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPermission;
import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPlugin;
import de.blablubbabc.shopkeepers.dynmap.command.lib.Command;

public class ReloadCommand extends Command {

	private final ShopkeepersDynmapPlugin plugin;

	public ReloadCommand(ShopkeepersDynmapPlugin plugin) {
		super(
				"reload",
				Collections.emptyList(),
				ShopkeepersDynmapPermission.RELOAD,
				"Reloads this plugin."
		);
		this.plugin = plugin;
	}

	@Override
	protected void execute(CommandSender sender, List<? extends String> args) {
		plugin.reload();
		sender.sendMessage(c("&aPlugin reloaded!"));
	}
}
