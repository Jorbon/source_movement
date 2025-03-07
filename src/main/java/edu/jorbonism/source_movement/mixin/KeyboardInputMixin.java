package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.Srcmov;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	// use the global scroll jump flag to actually cause a jump input
	@Inject(method = "tick", at = @At("RETURN"))
	private void tick_scroll_jump(CallbackInfo ci) {
		if (Srcmov.is_scroll_jump_queued) {
			this.playerInput = new PlayerInput(
				this.playerInput.forward(),
				this.playerInput.backward(),
				this.playerInput.left(),
				this.playerInput.right(),
				true,
				this.playerInput.sneak(),
				this.playerInput.sprint()
			);
			Srcmov.is_scroll_jump_queued = false;
		}
	}

}
