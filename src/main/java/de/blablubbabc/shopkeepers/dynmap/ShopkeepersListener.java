package de.blablubbabc.shopkeepers.dynmap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.nisovin.shopkeepers.api.events.ShopkeeperAddedEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperEditedEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperRemoveEvent;

class ShopkeepersListener implements Listener {

	private final ShopkeepersDynmap shopkeepersDynmap;

	public ShopkeepersListener(ShopkeepersDynmap shopkeepersDynmap) {
		this.shopkeepersDynmap = shopkeepersDynmap;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onShopkeeperAdded(ShopkeeperAddedEvent event) {
		shopkeepersDynmap.addShopkeeper(event.getShopkeeper());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onShopkeeperRemove(ShopkeeperRemoveEvent event) {
		shopkeepersDynmap.removeShopkeeper(event.getShopkeeper());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void onShopkeeperEdited(ShopkeeperEditedEvent event) {
		shopkeepersDynmap.updateShopkeeper(event.getShopkeeper());
	}
}
