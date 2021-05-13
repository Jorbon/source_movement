package edu.jorbonism.source_movement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/* to do list for 0.2.0
	more inclusive damage control
	redo fall damage
	make stuff work on a dedicated server
	implement elytra & liquids movement
*/

public class Srcmov implements ClientModInitializer, DedicatedServerModInitializer {

	public static final String MOD_ID = "srcmov";
	
	static String configPath = System.getProperty("user.dir") + "\\config\\source_movement.srcmov";
	static File configFile = new File(configPath);

	public static final HashMap<String,ConfigDouble> configDoubles = new HashMap<String,ConfigDouble>();
	public static final HashMap<String,Boolean> configBooleans = new HashMap<String,Boolean>();

	public static SrcmovPC playerController;
	
	public static final double sourceConversionFactor = 0.001233008;
	public static final double walkSpeedTarget = 0.2157764;
	public static final double sprintSpeedTarget = walkSpeedTarget * 1.3;
	public static final double sneakSpeedTarget = walkSpeedTarget * 0.3;


	public static boolean scrollJumpQueuedTemp = false;

	private static KeyBinding keySetVanilla;
	private static KeyBinding keyReloadActions;
	public static ArrayList<VelocityAction> actions = new ArrayList<VelocityAction>();
	public static ArrayList<VelocityActionStep> actionQueue = new ArrayList<VelocityActionStep>();


	@Override
	public void onInitializeClient() {

		initValues();

		playerController = new SrcmovPC();
		
		ConfigLoader.initConfig();

		// register the velocity action keys
		for (int i = actions.size() - 1; i >= 0; i--) {
			KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.srcmov.action" + (i+1),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.srcmov.all"
			));
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				while (key.wasPressed()) {
					String tr = key.getTranslationKey();
					// use translation key to determine index
					// 17 is "key.srcmov.action".length()
					int index = Integer.parseInt(tr.substring(17)) - 1;
					if (index < 0 || index >= actions.size()) return;
					actions.get(index).queue();
				}
			});
		}

		// register n use-keys in reverse order
		for (int i = ConfigLoader.getConfigCount() - 1; i >= 0; i--) {
			KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.srcmov.useconfig" + (i+1),
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.srcmov.all"
			));
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				if (key.wasPressed()) {
					Srcmov.playerController.enabled = true;
					String tr = key.getTranslationKey();
					// we will get the translation key of the bind to figure out which config to load
					// 20 is "key.srcmov.useconfig".length()
					ConfigLoader.loadConfig(Integer.parseInt(tr.substring(20)) - 1);
				}
			});
		}

		// key to reload velocity actions
		keyReloadActions = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.srcmov.reloadactions",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"category.srcmov.all"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keyReloadActions.wasPressed())
				ConfigLoader.cacheActions();
		});

		// now register the big disable key
		keySetVanilla = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.srcmov.setvanilla", // The translation key of the keybinding's name
			InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
			GLFW.GLFW_KEY_UNKNOWN, // The keycode of the key
			"category.srcmov.all" // The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keySetVanilla.wasPressed())
				Srcmov.playerController.enabled = false;
		});
		
	}

	@Override
	public void onInitializeServer() {
		initValues();

		playerController = new SrcmovPC();
	}

	public static void initValues() {
		configBooleans.put("directional jump boosting", false);
		configBooleans.put("orange box jump boosting", false);
		configBooleans.put("full speed jumps", false);
		configBooleans.put("scroll jump", false);
		configBooleans.put("use source control cap", false);
		configBooleans.put("use source fling detection", false);
		configBooleans.put("enable abh", false);
		
		configDoubles.put("gravity", new ConfigDouble(0.08));
		configDoubles.put("jump height", new ConfigDouble(1.25, 1.25, 0));
		configDoubles.put("horizontal air friction", new ConfigDouble(0.09, 0.09, 0, 1));
		configDoubles.put("vertical air friction", new ConfigDouble(0.02, 0.02, 0, 1));
		configDoubles.put("sprint jump boost", new ConfigDouble(0.2));
		configDoubles.put("walk jump boost", new ConfigDouble(0, 0.2));
		configDoubles.put("ground friction", new ConfigDouble(1));
		configDoubles.put("air control", new ConfigDouble(0.02));
		configDoubles.put("ground control", new ConfigDouble(0.1));
		configDoubles.put("climb speed", new ConfigDouble(0.2));
		configDoubles.put("climb movement cap", new ConfigDouble(0.15));
		configDoubles.put("horizontal fly friction", new ConfigDouble(0.09, 0.09, 0, 1));
		configDoubles.put("fly speed", new ConfigDouble(0.05));
		configDoubles.put("vertical fly friction", new ConfigDouble(0.4, 0.4, 0, 1));
		configDoubles.put("levitation strength", new ConfigDouble(0.05));
		configDoubles.put("source control cap", new ConfigDouble(inMCUnits(60)));
		configDoubles.put("min fling speed", new ConfigDouble(inMCUnits(300)));
		configDoubles.put("abh threshold", new ConfigDouble(3.75));
		configDoubles.put("jump friction multiplier", new ConfigDouble(1));

		configBooleans.put("velocity based fall damage", false);
		configDoubles.put("fall damage", new ConfigDouble(1, 1, 0));
		configDoubles.put("sprint jump exhaustion", new ConfigDouble(0.2, 0.2, 0));
		configDoubles.put("jump exhaustion", new ConfigDouble(0.05, 0.05, 0));
	}

	public static double inSourceUnits(double mcUnits) {
		return mcUnits / sourceConversionFactor;
	}

	public static double inMCUnits(double srcUnits) {
		return srcUnits * sourceConversionFactor;
	}



	
	


}
