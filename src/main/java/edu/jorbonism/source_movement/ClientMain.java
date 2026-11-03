package edu.jorbonism.source_movement;

import org.jspecify.annotations.NonNull;
import org.lwjgl.sdl.SDLKeycode;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;



public class ClientMain implements ClientModInitializer {
	
	public static final double SRC_TO_MC = 0.001233008;
	public static final double MC_TO_SRC = 1.0 / SRC_TO_MC;
	
	// public static final double VANILLA_WALK_SPEED = 0.2157764;
	// public static final double VANILLA_SPRINT_SPEED = VANILLA_WALK_SPEED * 1.3;
	// public static final double VANILLA_SNEAK_SPEED = VANILLA_WALK_SPEED * 0.3;
	
	// typical speeds:
	// walk: 0.1155
	// walk diagonal: 0.120264
	// sprint: 0.1501525
	// sprint diagonal: 0.1532167
	// sneak: 0.0353577
	// sneak diagonal: 0.05
	
	
	public static boolean enabled = true;
	public static ConfigState config_state = new ConfigState();
	
	public static boolean is_scroll_jump_queued = false;
	
	private static final KeyMapping.@NonNull Category MOD_CATEGORY = KeyMapping.Category.register(Identifier.withDefaultNamespace("source_movement"));
	// private static KeyMapping key_toggle_srcmov_off;
	private static KeyMapping key_boost;
	
	
	@Override
	public void onInitializeClient() {
		
		// ConfigFileIO.generate_config_file();
		
		// // register n use-keys in reverse order
		// for (int i = ConfigFileIO.get_num_configs() - 1; i >= 0; i--) {
		// 	KeyMapping key = KeyMappingHelper.registerKeyMapping(new KeyMapping(
		// 		"key.source_movement.useconfig" + (i + 1),
		// 		InputConstants.Type.KEYBOARD,
		// 		SDLKeycode.SDLK_UNKNOWN,
		// 		MOD_CATEGORY
		// 	));
			
		// 	ClientTickEvents.END_CLIENT_TICK.register(client -> {
		// 		if (key.consumeClick()) {
		// 			// key.getBoundKeyTranslationKey()
		// 			ClientMain.enabled = true;
		// 			var name = key.getName();
		// 			// Use the translation key of the binding to figure out which config to load
		// 			ConfigFileIO.load_config(Integer.parseInt(name.substring("key.source_movement.useconfig".length())) - 1);
		// 		}
		// 	});
		// }
		
		
		// // disable key
		// key_toggle_srcmov_off = KeyMappingHelper.registerKeyMapping(new KeyMapping(
		// 	"key.source_movement.disable",
		// 	InputConstants.Type.KEYBOARD,
		// 	SDLKeycode.SDLK_UNKNOWN,
		// 	MOD_CATEGORY
		// ));
		
		// ClientTickEvents.END_CLIENT_TICK.register(client -> {
		// 	if (key_toggle_srcmov_off.consumeClick()) {
		// 		ClientMain.enabled = false;
		// 	}
		// });
		
		
		// boost key
		key_boost = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.source_movement.boost",
			InputConstants.Type.KEYBOARD,
			SDLKeycode.SDLK_UNKNOWN,
			MOD_CATEGORY
		));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!ClientMain.enabled) return;
			var player = client.player;
			if (player == null) return;
			while (key_boost.consumeClick()) {
				double boost_speed = 1.0; //config_state.get_double(ConfigState.DoubleSetting.BoostSpeed);
				
				double yaw = player.getYRot() * 0.017453292;
				double pitch = player.getXRot() * 0.017453292;
				double fx = -Math.sin(yaw) * Math.cos(pitch);
				double fz = Math.cos(yaw) * Math.cos(pitch);
				double fy = -Math.sin(pitch);
				player.addDeltaMovement(new Vec3(fx * boost_speed, fy * boost_speed, fz * boost_speed));
			}
		});
		
		ConfigFileIO.load_config(0);
		
	}
	
	
	
	
	
	
	
}
