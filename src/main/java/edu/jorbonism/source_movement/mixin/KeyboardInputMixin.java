package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.ClientMain;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {

	// use the global scroll jump flag to actually cause a jump input
	@Inject(method = "tick", at = @At("RETURN"))
	private void tick_scroll_jump(CallbackInfo ci) {
		if (ClientMain.is_scroll_jump_queued) {
			this.keyPresses = new Input(
				this.keyPresses.forward(),
				this.keyPresses.backward(),
				this.keyPresses.left(),
				this.keyPresses.right(),
				true,
				this.keyPresses.shift(),
				this.keyPresses.sprint()
			);
			ClientMain.is_scroll_jump_queued = false;
		}
	}

}
