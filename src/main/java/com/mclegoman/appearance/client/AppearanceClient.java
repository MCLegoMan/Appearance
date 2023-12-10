/*
    Appearance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Appearance
    Licence: GNU LGPLv3
*/

package com.mclegoman.appearance.client;

import com.mclegoman.appearance.client.util.SkinsDataLoader;
import com.mclegoman.appearance.common.data.Data;
import com.mclegoman.releasetypeutils.common.version.Helper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class AppearanceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Data.VERSION.sendToLog(Helper.LogType.INFO, "Initializing Client...");
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SkinsDataLoader());
	}
}
