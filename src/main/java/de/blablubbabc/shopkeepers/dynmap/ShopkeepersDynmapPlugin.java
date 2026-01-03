package de.blablubbabc.shopkeepers.dynmap;

import org.bukkit.plugin.java.JavaPlugin;

import de.blablubbabc.shopkeepers.dynmap.command.Commands;

public class ShopkeepersDynmapPlugin extends JavaPlugin {

	private final Settings settings = new Settings(this);
	private final Commands commands = new Commands(this);
	private final ShopkeepersDynmap shopkeepersDynmap = new ShopkeepersDynmap(this);

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();

		commands.register();

		shopkeepersDynmap.enable();
	}

	@Override
	public void onDisable() {
		shopkeepersDynmap.disable();
	}

	/**
	 * Reloads the plugin.
	 */
	public void reload() {
		this.onDisable();
		this.onEnable();
	}

	public Settings getSettings() {
		return settings;
	}

	public void debug(String message) {
		if (!settings.isDebugging()) {
			return;
		}

		this.getLogger().info(message);
	}

	/**
	 * Updates all Dynmap shopkeeper marker icons.
	 * 
	 * @return an error message, or <code>null</code> on success
	 */
	public String updateIcons() {
		return shopkeepersDynmap.updateIcons();
	}
}
