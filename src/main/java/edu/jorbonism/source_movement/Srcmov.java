package edu.jorbonism.source_movement;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;



public class Srcmov implements ClientModInitializer {
	
	// public static final String MOD_ID = "srcmov";
	
	
	public static final double SRC_TO_MC = 0.001233008;
	public static final double MC_TO_SRC = 1.0 / SRC_TO_MC;
	
	// public static final double VANILLA_WALK_SPEED = 0.2157764;
	// public static final double VANILLA_SPRINT_SPEED = VANILLA_WALK_SPEED * 1.3;
	// public static final double VANILLA_SNEAK_SPEED = VANILLA_WALK_SPEED * 0.3;
	
	
	public static boolean enabled = true;
	public static ConfigState config_state = new ConfigState();
	
	public static boolean is_scroll_jump_queued = false;
	
	private static KeyBinding key_toggle_srcmov_off;
	private static KeyBinding key_boost;
	
	
	@Override
	public void onInitializeClient() {
		
		ConfigFileIO.generate_config_file();
		
		// register n use-keys in reverse order
		for (int i = ConfigFileIO.get_num_configs() - 1; i >= 0; i--) {
			KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.srcmov.useconfig" + (i+1),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.srcmov.all"
			));
			
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (key.wasPressed()) {
					enabled = true;
					String tr = key.getTranslationKey();
					// Use the translation key of the binding to figure out which config to load
					ConfigFileIO.load_config(Integer.parseInt(tr.substring("key.srcmov.useconfig".length())) - 1);
				}
			});
		}
		
		
		// disable key
		key_toggle_srcmov_off = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.srcmov.disable",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"category.srcmov.all"
		));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (key_toggle_srcmov_off.wasPressed()) enabled = false;
		});
		
		
		// boost key
		key_boost = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.srcmov.boost",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"category.srcmov.all"
		));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!enabled) return;
			while (key_boost.wasPressed()) {
				double boost_speed = config_state.get_double(ConfigState.DoubleSetting.BoostSpeed);
				
				Vec3d velocity = client.player.getVelocity();
				double yaw = client.player.getYaw() * 0.017453292;
				double pitch = client.player.getPitch() * 0.017453292;
				double fx = -Math.sin(yaw) * Math.cos(pitch);
				double fz = Math.cos(yaw) * Math.cos(pitch);
				double fy = -Math.sin(pitch);
				client.player.setVelocity(velocity.x + fx * boost_speed, velocity.y + fy * boost_speed, velocity.z + fz * boost_speed);
			}
		});
		
	}
	
	
	
	
	
	
	
}
