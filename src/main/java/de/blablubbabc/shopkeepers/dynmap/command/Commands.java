package de.blablubbabc.shopkeepers.dynmap.command;

import de.blablubbabc.shopkeepers.dynmap.ShopkeepersDynmapPlugin;

public class Commands {

	private final ShopkeepersDynmapPlugin plugin;

	public Commands(ShopkeepersDynmapPlugin plugin) {
		this.plugin = plugin;
	}

	public void register() {
		ShopkeepersDynmapCommand.register(plugin);
	}
}
