package de.blablubbabc.shopkeepers.dynmap;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerSet;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;

/**
 * Shopkeepers Dynmap integration.
 */
public class ShopkeepersDynmap {

	private static final String JAR_ASSETS = "assets";
	private static final String[] ASSETS = new String[] {
			"admin16.png",
			"admin24.png",
			"admin32.png",
			"book16.png",
			"book24.png",
			"book32.png",
			"buy16.png",
			"buy24.png",
			"buy32.png",
			"sell16.png",
			"sell24.png",
			"sell32.png",
			"trade16.png",
			"trade24.png",
			"trade32.png"
	};

	private static final String MARKERSET_ID = "shopkeepers.markerset";
	private static final String MARKER_ID_PREFIX = "shopkeepers-";

	private final ShopkeepersDynmapPlugin plugin;
	private final ShopkeepersListener shopkeeperListener = new ShopkeepersListener(this);
	private final DynmapAPIListener dynmapAPIListener = new DynmapAPIListener(this);

	private DynmapCommonAPI dynmapApi = null;

	private boolean enabled = false;
	private boolean assetsWritten = false;

	public ShopkeepersDynmap(ShopkeepersDynmapPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Enables the integration.
	 * <p>
	 * For example called during plugin enable.
	 */
	public void enable() {
		if (enabled) {
			return;
		}

		if (!plugin.getSettings().isEnabled()) {
			return;
		}

		enabled = true;
		// Try to write the assets again after each reload:
		assetsWritten = false;

		// Called immediately if the Dynmap API is currently enabled:
		DynmapCommonAPIListener.register(dynmapAPIListener);

		Bukkit.getPluginManager().registerEvents(shopkeeperListener, plugin);
	}

	/**
	 * Disables the integration.
	 * <p>
	 * For example called during plugin disable.
	 */
	public void disable() {
		if (!enabled) {
			return;
		}

		HandlerList.unregisterAll(shopkeeperListener);

		DynmapCommonAPIListener.unregister(dynmapAPIListener);

		this.onDynmapDisabled();

		enabled = false;
	}

	// Called on the main thread.
	void onDynmapEnabled(DynmapCommonAPI newDynmapApi) {
		if (!enabled) {
			return;
		}
		// This is called on the main thread and we disable the integration during plugin disable:
		assert plugin.isEnabled();

		if (this.dynmapApi != null) {
			// Already enabled. Unexpected.
			return;
		}

		this.dynmapApi = newDynmapApi;

		if (!assetsWritten) {
			assetsWritten = true;

			this.addMarkerIcons(dynmapApi);
		}

		this.addAllShopkeepers(newDynmapApi);
	}

	// Called on the main thread.
	void onDynmapDisabled() {
		var dynmapApi = this.dynmapApi;
		if (dynmapApi == null) {
			return; // Already disabled
		}
		// dynmapApi is only assigned when the integration is enabled:
		assert enabled;

		this.removeAllShopkeepers(dynmapApi);

		this.dynmapApi = null;
	}

	private void addMarkerIcons(DynmapCommonAPI dynmapApi) {
		plugin.getLogger().info("Adding marker icons...");

		var markerApi = dynmapApi.getMarkerAPI();

		var addedIconsCount = 0;
		for (var asset : ASSETS) {
			var iconId = this.getMarkerIconId(asset);
			// Note: We don't replace existing icons here, since the user might have replaced them
			// with their own assets. The user can update them by using the update-icons command.
			if (markerApi.getMarkerIcon(iconId) != null) {
				continue;
			}

			var iconName = this.getMarkerIconName(asset);

			var sourcePath = JAR_ASSETS + "/" + asset;
			var inputStream = plugin.getResource(sourcePath);

			markerApi.createMarkerIcon(iconId, iconName, inputStream);
			addedIconsCount++;
		}

		plugin.getLogger().info("Added missing marker icons: " + addedIconsCount);
	}

	private void deleteMarkerIcons(DynmapCommonAPI dynmapApi) {
		plugin.getLogger().info("Deleting marker icons with prefix '" + MARKER_ID_PREFIX + "'...");

		var markerApi = dynmapApi.getMarkerAPI();

		var deletedIconsCount = 0;
		for (var icon : new ArrayList<>(markerApi.getMarkerIcons())) {
			if (icon.getMarkerIconID().startsWith(MARKER_ID_PREFIX)) {
				icon.deleteIcon();
				deletedIconsCount++;
			}
		}

		plugin.getLogger().info("Deleted marker icons: " + deletedIconsCount);
	}

	/**
	 * Updates all Dynmap shopkeeper marker icons.
	 * 
	 * @return an error message, or <code>null</code> on success
	 */
	public String updateIcons() {
		if (!enabled) {
			return "The Shopkeepers-Dynmap integration is currently disabled!";
		}

		if (this.dynmapApi == null) {
			return "The Dynmap API is currently not available!";
		}

		plugin.getLogger().info("Updating marker icons...");
		this.deleteMarkerIcons(dynmapApi);
		this.addMarkerIcons(dynmapApi);

		return null; // Success
	}

	private String getMarkerIconId(String asset) {
		// The same as the icon name:
		return this.getMarkerIconName(asset);
	}

	private String getMarkerIconName(String asset) {
		// Strip the trailing file extension:
		// Note: Any leading file path is included.
		var nameEnd = asset.lastIndexOf('.');
		if (nameEnd < 0) nameEnd = asset.length();

		return MARKER_ID_PREFIX + asset.substring(0, nameEnd);
	}

	private MarkerSet getOrCreateMarkerSet(DynmapCommonAPI dynmapApi) {
		var markerApi = dynmapApi.getMarkerAPI();
		var markerSet = markerApi.getMarkerSet(MARKERSET_ID);
		if (markerSet == null) {
			markerSet = markerApi.createMarkerSet(
					MARKERSET_ID,
					plugin.getSettings().getMarkerSetName(),
					null,
					false
			);
		}

		return markerSet;
	}

	private MarkerSet getMarkerSet(DynmapCommonAPI dynmapApi) {
		var markerApi = dynmapApi.getMarkerAPI();
		return markerApi.getMarkerSet(MARKERSET_ID);
	}

	private void addAllShopkeepers(DynmapCommonAPI dynmapApi) {
		// Note: If the Shopkeepers API is later enabled, the shopkeepers will be added one-by-one
		// via the ShopkeeperAddedEvent.
		if (!ShopkeepersAPI.isEnabled()) {
			return;
		}

		var allShopkeepers = ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers();
		allShopkeepers.forEach(shopkeeper -> this.addShopkeeper(dynmapApi, shopkeeper));
		plugin.getLogger().info("Added Dynmap markers for all shopkeepers: "
				+ allShopkeepers.size());
	}

	private void removeAllShopkeepers(DynmapCommonAPI dynmapApi) {
		// Remove the shopkeepers marker set:
		var markerSet = this.getMarkerSet(dynmapApi);
		var markerCount = 0;
		if (markerSet != null) {
			markerCount = markerSet.getMarkers().size();
			markerSet.deleteMarkerSet();
		}

		plugin.getLogger().info("Removed " + markerCount + " Dynmap markers for all shopkeepers.");
	}

	void addShopkeeper(Shopkeeper shopkeeper) {
		if (this.dynmapApi == null) {
			return;
		}

		this.addShopkeeper(this.dynmapApi, shopkeeper);
	}

	private void addShopkeeper(DynmapCommonAPI dynmapApi, Shopkeeper shopkeeper) {
		assert dynmapApi != null;
		assert shopkeeper != null;

		var worldName = shopkeeper.getWorldName();
		if (worldName == null) {
			// E.g. the case for virtual shopkeepers.
			plugin.debug(shopkeeper.getLogPrefix()
					+ "Not adding Dynmap marker for virtual shopkeeper.");
			return;
		}

		var shopTypeId = shopkeeper.getType().getIdentifier();
		var markerIconId = plugin.getSettings().getMarkerIcon(shopTypeId);
		if (markerIconId == null || markerIconId.isBlank()) {
			// Skip if no marker icon is defined:
			plugin.debug(shopkeeper.getLogPrefix() + "Not adding Dynmap marker: No icon defined.");
			return;
		}

		var markerApi = dynmapApi.getMarkerAPI();
		var markerIcon = markerApi.getMarkerIcon(markerIconId);
		if (markerIcon == null) {
			plugin.debug(shopkeeper.getLogPrefix() + "Not adding Dynmap marker: Icon not found: "
					+ markerIconId);
			return;
		}

		var markerSet = this.getOrCreateMarkerSet(dynmapApi);

		var marker = markerSet.createMarker(
				this.getMarkerId(shopkeeper),
				this.getMarkerLabel(shopkeeper),
				false, // Don't process label as HTML since this can be controlled by players
				worldName,
				shopkeeper.getX() + 0.5D,
				shopkeeper.getY() + 0.5D,
				shopkeeper.getZ() + 0.5D,
				markerIcon,
				false // Persistent
		);
		marker.setDescription(this.getShopkeeperDetail(shopkeeper));

		plugin.debug(shopkeeper.getLogPrefix() + "Added Dynmap marker.");
	}

	private String getMarkerId(Shopkeeper shopkeeper) {
		return "shopkeeper_" + shopkeeper.getId();
	}

	private String getMarkerLabel(Shopkeeper shopkeeper) {
		var ownerName = "";
		if (shopkeeper instanceof PlayerShopkeeper playerShopkeeper) {
			ownerName = playerShopkeeper.getOwnerName();
		}

		return plugin.getSettings().getMarkerLabel(shopkeeper.getType().getIdentifier())
				.replace("{shop_id}", Integer.toString(shopkeeper.getId()))
				.replace("{shop_uuid}", shopkeeper.getUniqueId().toString())
				.replace("{shop_name}", ChatColor.stripColor(shopkeeper.getDisplayName()))
				.replace("{shop_owner_name}", ownerName);
	}

	private String getShopkeeperDetail(Shopkeeper shopkeeper) {
		// Note: Don't include unescaped user input (e.g. the shopkeeper name) here.

		var shopObjectTypeName = shopkeeper.getShopObject().getType().getDisplayName();
		var offersCount = shopkeeper.getTradingRecipes(null).size();

		var ownerName = "";
		if (shopkeeper instanceof PlayerShopkeeper playerShopkeeper) {
			ownerName = playerShopkeeper.getOwnerName();
		}

		var detail = plugin.getSettings().getMarkerDetailText()
				.replaceAll("\\r\\n|\\r|\\n", "<br>")
				.replace("{shop_id}", Integer.toString(shopkeeper.getId()))
				.replace("{shop_uuid}", shopkeeper.getUniqueId().toString())
				.replace("{shop_type}", shopkeeper.getType().getDisplayName())
				.replace("{shop_object_type}", shopObjectTypeName)
				.replace("{shop_owner_name}", ownerName)
				.replace("{shop_offers_count}", Integer.toString(offersCount));

		return detail;
	}

	void removeShopkeeper(Shopkeeper shopkeeper) {
		if (this.dynmapApi == null) {
			return;
		}

		this.removeShopkeeper(this.dynmapApi, shopkeeper);
	}

	private void removeShopkeeper(DynmapCommonAPI dynmapApi, Shopkeeper shopkeeper) {
		assert dynmapApi != null;
		assert shopkeeper != null;

		// Not skipping virtual shopkeepers here: Maybe the shopkeeper object type changed in the
		// meantime from previously non-virtual to now virtual.

		var markerSet = this.getMarkerSet(dynmapApi);
		if (markerSet == null) {
			return;
		}

		var markerId = this.getMarkerId(shopkeeper);
		var marker = markerSet.findMarker(markerId);
		if (marker == null) {
			return;
		}

		marker.deleteMarker();

		plugin.debug(shopkeeper.getLogPrefix() + "Removed Dynmap marker.");
	}

	void updateShopkeeper(Shopkeeper shopkeeper) {
		if (this.dynmapApi == null) {
			return;
		}

		this.removeShopkeeper(this.dynmapApi, shopkeeper);
		this.addShopkeeper(this.dynmapApi, shopkeeper);
	}
}
