/*
    Appearance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Appearance
    Licence: GNU LGPLv3
*/

package com.mclegoman.appearance.client.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mclegoman.appearance.common.data.Data;
import com.mclegoman.releasetypeutils.common.version.Helper;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinsDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> implements IdentifiableResourceReloadListener {
	public static final List<SkinData> registry = new ArrayList<>();
	private final Gson gson;
	private final String dataType;
	public SkinsDataLoader() {
		this.gson = new Gson();
		this.dataType = "skins";
	}
	private void add(String uuid, String model, boolean replaceSkin, String skinTexture, boolean replaceCape, String capeTexture) {
		try {
			SkinData data = new SkinData(uuid, model.toLowerCase(), replaceSkin, skinTexture.toLowerCase(), replaceCape, capeTexture);
			if (!registry.contains(data)) registry.add(data);
		} catch (Exception error) {
			Data.version.getLogger().warn("{} Failed to add skins to registry: {}", Data.version.getID(), error);
		}
	}
	public void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		try {
			registry.clear();
			prepared.forEach(this::layout$appearance);
		} catch (Exception error) {
			Data.version.getLogger().warn("{} Failed to apply skins dataloader: {}", Data.version.getID(), error);
		}
	}
	@Override
	public Identifier getFabricId() {
		return Identifier.of(Data.version.getID(), this.dataType);
	}
	private void layout$appearance(Identifier identifier, JsonElement jsonElement) {
		JsonObject reader = jsonElement.getAsJsonObject();
		String uuid = JsonHelper.getString(reader, "uuid");
		String model = JsonHelper.getString(reader, "model");
		String skin = JsonHelper.getString(reader, "texture", "");
		boolean replaceSkin = JsonHelper.getBoolean(reader, "replaceSkin", !skin.isEmpty());
		String cape = JsonHelper.getString(reader, "cape", "");
		boolean replaceCape = JsonHelper.getBoolean(reader, "replaceCape", !cape.isEmpty());
		add(uuid, model, replaceSkin, skin, replaceCape, cape);
	}
	protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, JsonElement> map = new HashMap<>();
		load(resourceManager, this.dataType, this.gson, map);
		return map;
	}
	public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results) {
		ResourceFinder resourceFinder = ResourceFinder.json(dataType);
		for (Map.Entry<Identifier, Resource> identifierResourceEntry : resourceFinder.findResources(manager).entrySet()) {
			Identifier identifier = identifierResourceEntry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);
			try {
				Reader reader = identifierResourceEntry.getValue().getReader();
				try {
					JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
					JsonElement jsonElement2 = results.put(identifier2, jsonElement);
					if (jsonElement2 != null) {
						throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
					}
				} catch (Throwable throwable) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var12) {
							throwable.addSuppressed(var12);
						}
					}
					throw throwable;
				}
				reader.close();
			} catch (Exception error) {
				Data.version.sendToLog(Helper.LogType.ERROR, "Couldn't parse data file " + identifier2 + " from " + identifier + ": " + error);
			}
		}
	}
}
