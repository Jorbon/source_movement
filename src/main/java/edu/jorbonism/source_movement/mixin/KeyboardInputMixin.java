package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.Srcmov;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	// use the global scroll jump flag to actually cause a jump input
	@Inject(method = "tick", at = @At("RETURN"))
	private void doScrollJump(boolean slowDown, CallbackInfo ci) {
		if (Srcmov.playerController.enabled && Srcmov.scrollJumpQueuedTemp)
			this.jumping = true;
		Srcmov.scrollJumpQueuedTemp = false;
	}

}
