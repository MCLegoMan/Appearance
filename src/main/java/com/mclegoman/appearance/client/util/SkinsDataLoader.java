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
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkinsDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
	public static final List<List<String>> REGISTRY = new ArrayList<>();
	public static final String ID = "skins";
	public SkinsDataLoader() {
		super(new Gson(), ID);
	}
	private void add(String UUID, String MODEL, String TEXTURE) {
		try {
			List<String> DATA = new ArrayList<>();
			DATA.add(UUID);
			DATA.add(MODEL.toLowerCase());
			DATA.add(TEXTURE.toLowerCase());
			if (!REGISTRY.contains(DATA)) REGISTRY.add(DATA);
		} catch (Exception error) {
			Data.VERSION.getLogger().warn("{} Failed to add skins to registry: {}", Data.VERSION.getID(), error);
		}
	}
	private void reset() {
		try {
			REGISTRY.clear();
		} catch (Exception error) {
			Data.VERSION.getLogger().warn("{} Failed to reset skins registry: {}", Data.VERSION.getID(), error);
		}
	}
	@Override
	public void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
		try {
			reset();
			prepared.forEach(this::layout$appearance);
		} catch (Exception error) {
			Data.VERSION.getLogger().warn("{} Failed to apply skins dataloader: {}", Data.VERSION.getID(), error);
		}
	}
	@Override
	public Identifier getFabricId() {
		return new Identifier(Data.VERSION.getID(), ID);
	}
	private void layout$appearance(Identifier identifier, JsonElement jsonElement) {
		JsonObject READER = jsonElement.getAsJsonObject();
		String UUID = JsonHelper.getString(READER, "uuid");
		String MODEL = JsonHelper.getString(READER, "model");
		String TEXTURE = JsonHelper.getString(READER, "texture");
		add(UUID, MODEL, TEXTURE);
	}
}