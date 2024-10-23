/*
    Appearance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Appearance
    Licence: GNU LGPLv3
*/

package com.mclegoman.appearance.mixin;

import com.mclegoman.appearance.client.util.SkinData;
import com.mclegoman.appearance.client.util.SkinsDataLoader;
import com.mclegoman.appearance.common.data.Data;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayerEntity.class, priority = 0)
public abstract class AbstractClientPlayerEntityMixin {
	@Shadow @Nullable private PlayerListEntry playerListEntry;
	@Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
	private void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
		if (this.playerListEntry != null) {
			SkinTextures currentSkinTextures = cir.getReturnValue();
			Identifier skinTexture = currentSkinTextures.texture();
			Identifier capeTexture = currentSkinTextures.capeTexture();
			Identifier elytraTexture = currentSkinTextures.elytraTexture();
			SkinTextures.Model model = currentSkinTextures.model();
			for (SkinData data : SkinsDataLoader.registry) {
				if (data.uuid().equals(String.valueOf(this.playerListEntry.getProfile().getId()))) {
					if (data.replaceSkin()) {
						model = data.model().equalsIgnoreCase("slim") ? SkinTextures.Model.SLIM : SkinTextures.Model.WIDE;
						skinTexture = Identifier.of(Data.version.getID(), "textures/entity/player/" + data.model() + "/" + data.skinTexture() + ".png");
					}
					if (data.replaceCape()) {
						capeTexture = Identifier.of(Data.version.getID(), "textures/entity/player/cape/" + data.capeTexture() + ".png");
						elytraTexture = Identifier.of(Data.version.getID(), "textures/entity/player/cape/" + data.capeTexture() + ".png");
					}
				}
			}
			cir.setReturnValue(new SkinTextures(skinTexture, currentSkinTextures.textureUrl(), capeTexture, elytraTexture, model, currentSkinTextures.secure()));
		}
	}
}
