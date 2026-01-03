package de.blablubbabc.shopkeepers.dynmap.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPermission;
import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPlugin;
import de.blablubbabc.shopkeepers.dynmap.command.lib.Command;

public class UpdateIconsCommand extends Command {

	private final ShopkeepersDynmapPlugin plugin;

	public UpdateIconsCommand(ShopkeepersDynmapPlugin plugin) {
		super(
				"update-icons",
				Collections.emptyList(),
				ShopkeepersDynmapPermission.UPDATE_ICONS,
				"Updates all Dynmap marker icons."
		);
		this.plugin = plugin;
	}

	@Override
	protected void execute(CommandSender sender, List<? extends String> args) {
		var error = plugin.updateIcons();
		if (error != null) {
			sender.sendMessage(c("&c" + error));
		}

		sender.sendMessage(c("&aIcons updated!"));
	}
}
