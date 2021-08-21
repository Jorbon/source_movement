package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.Srcmov;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public abstract class MouseMixin {
	
	@Shadow private MinecraftClient client;

	// catch the scroll event and cause a jump anytime it would normally scroll the hotbar
	@Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = 
		"Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"
	), cancellable = true)
	private void catchScrollJump(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (Srcmov.playerController.enabled && Srcmov.playerController.booleans.get("scroll jump")) {
			Srcmov.scrollJumpQueuedTemp = true;
			ci.cancel();
		}
	}
}
