/*
    Appearance
    Contributor(s): MCLegoMan
    Github: https://github.com/MCLegoMan/Appearance
    Licence: GNU LGPLv3
*/

package com.mclegoman.appearance.mixin;

import com.mclegoman.appearance.client.util.SkinsDataLoader;
import com.mclegoman.appearance.common.data.Data;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
	@Shadow @Nullable protected abstract PlayerListEntry getPlayerListEntry();
	@Inject(method = "getSkinTexture", at = @At("RETURN"), cancellable = true)
	public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		if (playerListEntry != null) {
			for (List<String> DATA : SkinsDataLoader.REGISTRY) {
				if (playerListEntry.getProfile().getId().toString().equals(DATA.get(0))) {
					cir.setReturnValue(new Identifier(Data.VERSION.getID(), "textures/entity/player/" + DATA.get(1) + "/" + DATA.get(2) + ".png"));
				}
			}
		}
	}
	@Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
	public void getModel(CallbackInfoReturnable<String> cir) {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		if (playerListEntry != null) {
			for (List<String> DATA : SkinsDataLoader.REGISTRY) {
				if (playerListEntry.getProfile().getId().toString().equals(DATA.get(0))) {
					cir.setReturnValue(DATA.get(1));
				}
			}
		}
	}
}
