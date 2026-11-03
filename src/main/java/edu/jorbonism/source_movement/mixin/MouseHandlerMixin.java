package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.ConfigState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import edu.jorbonism.source_movement.ClientMain;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	
	@Shadow private Minecraft client;
	
	// catch the scroll event and cause a jump anytime it would normally scroll the hotbar
	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void catchScrollJump(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (!ClientMain.enabled) return;
		if (!ClientMain.config_state.get_boolean(ConfigState.BooleanSetting.ScrollJumping)) return;
		ClientMain.is_scroll_jump_queued = true;
		ci.cancel();
	}
}
