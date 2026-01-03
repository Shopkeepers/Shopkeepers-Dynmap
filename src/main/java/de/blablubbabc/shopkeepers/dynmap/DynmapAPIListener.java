package de.blablubbabc.shopkeepers.dynmap;

import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;

class DynmapAPIListener extends DynmapCommonAPIListener {

	private final ShopkeepersDynmap shopkeepersDynmap;

	public DynmapAPIListener(ShopkeepersDynmap shopkeepersDynmap) {
		this.shopkeepersDynmap = shopkeepersDynmap;
	}

	// Note: These callbacks are expected to be called on the main thread.

	@Override
	public void apiEnabled(DynmapCommonAPI dynmapApi) {
		shopkeepersDynmap.onDynmapEnabled(dynmapApi);
	}

	@Override
	public void apiDisabled(DynmapCommonAPI dynmapApi) {
		shopkeepersDynmap.onDynmapDisabled();
	}
}
