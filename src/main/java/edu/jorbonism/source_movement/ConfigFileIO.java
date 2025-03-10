package edu.jorbonism.source_movement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.Scanner;

public class ConfigFileIO {
	
	static String config_path = FileSystems.getDefault().getPath(System.getProperty("user.dir"), "config", "source_movement.txt").toString();
	public static File config_file = new File(config_path);
	
	public static String get_config_file_string() {
		try {
			Scanner reader = new Scanner(config_file);
			reader.useDelimiter("\0"); // separate by null so it reads the entire file at once
			String data = reader.next();
			reader.close();
			return data;
		} catch (FileNotFoundException e) {
			System.err.println(e);
			return "";
		}
	}
	
	public static void generate_config_file() {
		if (config_file.exists()) return;
		
		try {
			config_file.createNewFile();
			
			FileWriter writer = new FileWriter(config_file);
			
			writer.write("This is the Source Movement config file, where you can change all the settings and do crazy stuff\nEach movement config begins with an at symbol and includes a list \"keyword = value\" pairs, like this:\n\n@	Put the name here after the at\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nuse source fling detection = true\ngravity = 0.05\nhorizontal air friction = 0\nvertical air friction = 0.01\nground friction = 0.7x\nair control = 2.5x\nground control = 0.7x\nsprint jump boost = 0\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\njump friction multiplier = 0.5\n\n#\nA # symbol ends the config so you can leave comments after it, like this.\nIf you don't want to leave comments, there's no need for the #.\nThat config will be registered as Config 1 since it's the first in the file.\nIt also happens to be my recommended movement scheme for Portal 2 movement.\n\nIf that doesn't make sense:\n\ngravity = 0.16\n^ This sets gravity to 0.16, or twice the default.\n\ngravity = 2x\n^ This does exactly the same thing, but is a lot easier to understand an use\n\nenable abh = true\n^ This enables ABH\n\nenable abh: Y\n^ same thing\n\nAnything beginning with f or n is false, and t or y is true. Not case sensitive.\n\n\nHere are all of the modifiable settings and their default values:\n");
			
			for (Map.Entry<String, ConfigState.BooleanSetting> config_bool : ConfigState.BOOLEAN_LOOKUP.entrySet()) {
				writer.write("\n" + config_bool.getKey() + " = " + ConfigState.BOOLEAN_DEFAULTS.get(config_bool.getValue()));
			}
			writer.write("\n");
			for (Map.Entry<String, ConfigState.DoubleSetting> config_double : ConfigState.DOUBLE_LOOKUP.entrySet()) {
				writer.write("\n" + config_double.getKey() + " = " + ConfigState.DOUBLE_DEFAULTS.get(config_double.getValue()));
			}
			
			writer.write("\n\n\nNone of this above here will get loaded into a config since I never used an at symbol.\nBut this will:\n\n@	Portal 1 movement (with ABH)\nhorizontal air friction = 0\nvertical air friction = 0.01\nair control = 3x\norange box jump boosting = true\nwalk jump boost = 0.3x\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\nenable abh = true\ngravity = 1.2x\nground friction = 0.75x\nground control = 0.75x\n\n\n@	Example movement config\nsprint jump boost = 3x\ndirectional jump boosting = true\nhorizontal air friction = 0.01\nfall damage = 0\nsprint jump exhaustion = 0.5x\n#\n\nBy default (in this file) there are four configs.\nYou can add any number, but the number of keybind slots won't change until you re-open Minecraft.\nThe text after the at in the same line is supposed to be the name for it, but right now it's not used in the code anywhere so it doesn't actually matter.\n\nHave fun with it, and try not to break your game.\nHere is a variation of my recommended bunnyhopping config that's more for fun than accuracy:\n@	Source config 2\nhorizontal air friction = 0\nvertical air friction = 0.01\nair control = 4x\ngravity = 0.05\njump height = 1.5x\nsprint jump boost = 0\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nsource control cap = 2x\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\nclimb speed = 5x\nground friction = 0.7x\nground control = 0.7x\njump friction multiplier = 0.5x\n#\n\nAnother thing:\nThere's always a keybind to remove any custom movement effects and go back to vanilla.\nAlso, all keybinds in the mod are unbound by default so you can always hit reset to unbind them again if you want. I don't like it when you can't unbind things in modded minecraft.\n\n\n\nFor those of you still reading, velocity actions are basically a keypress that does something to your momentum.\nNot a part of source engine or anything, just for fun.\nIt works like a script, with a set of commands followed by a value to use:\n\nv* 2		multiply vertical velocity by 2\nv+ 1		add 1 blocks/tick to vertical velocity\nh* 2		multiply horizontal velocity by 2\nh+ 1		add 1 blocks/tick to horizontal velocity in the same direction\nh+f 1		add 1 blocks/tick to horizontal velocity facing forwards\nh+m 1		add 1 blocks/tick to horizontal velocity in movement direction\nt* 2		multiply total velocity by 2\nt+ 1		add 1 blocks/tick to total velocity in the same direction\nt+f 1		add 1 blocks/tick to total velocity facing forwards\n\nAnd you can chain them to do more complex maneuvers\nVelocity actions begin with a tilde:\n\n~	Example velocity action\nv* 0\nv+ 0.75\nh* 0.5\nh+f 0.5\n\n#\nThis will make you jump in whatever condition you are in (air, water, elytra, etc.) and will change your momentum to be more in the forwards direction.\nHere are 2 more useful ones:\n\n~	Stop vertical velocity\nv* 0\n\n~	Boost forwards\nt+f 1\n\n#\nIt would be a good idea to not hold down any velocity action keys unless they do something calm and simple, but I guess it's your game, not mine.\n\nIf you edit these velocity actions while Minecraft is open, you have to press a keybind to trigger a reload on all velocity keys.\nUnlike the movement configs, I don't want to make your computer read the file every time you activate one of those so that spamming can be fun and not laggy.\n\n\n\nThat's all I have to say.\nMy portal movement configs are far from a perfect recreation, so I encourage you to modify it an mess with some of the values.\nLet me know if you find a more accurate config, or configs for other source games.\n\nAlso I suggest you take everything you don't want out of this file and copy these instructions somewhere else so you can use them if you need to.\nIf you lose them you can always delete this file, then the game will load this back in.\n\nOh and PS: If you get banned from Hypixel for doing incredibly cheaty movement things, it's not my fault.\n\n\n");
			
			writer.close();
			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	
	public static int get_num_configs() {
		return get_config_file_string().split("@").length - 1;
	}
	
	public static void load_config(int index) {
		Srcmov.config_state.reset();
		
		String[] data = get_config_file_string().split("@");
		if (index >= data.length - 1) return;
		
		String[] config = data[index + 1].split("#", 2)[0].split("\n");
		
		for (int i = 1; i < config.length; i++) {
			String line = config[i].trim();
			if (line.isEmpty()) continue;
			
			String[] parts = line.split("=");
			if (parts.length < 2) parts = parts[0].split(":");
			if (parts.length != 2) System.out.println("Source Movement config error: \"" + line + "\" doesn't make any sense to me!");
			else {
				String name = parts[0].trim();
				String value = parts[1].trim();
				
				switch (value.charAt(0)) {
					case 't':
					case 'T':
					case 'y':
					case 'Y':
						set_boolean(name, true);
						continue;
					case 'f':
					case 'F':
					case 'n':
					case 'N':
						set_boolean(name, false);
						continue;
					case 'x':
						set_double_multiplier(name, Double.parseDouble(value.substring(1)));
						continue;
					default:
						if (value.charAt(value.length() - 1) == 'x') {
							set_double_multiplier(name, Double.parseDouble(value.substring(0, value.length() - 1)));
						} else {
							set_double(name, Double.parseDouble(value));
						}
				}
			}
		}
	}
	
	public static void set_boolean(String name, boolean value) {
		ConfigState.BooleanSetting key = ConfigState.BOOLEAN_LOOKUP.get(name);
		if (key == null) {
			System.out.println("Source Movement config error: the value \"" + name + "\" doesn't exist or is not a boolean");
		} else {
			Srcmov.config_state.set_boolean(key, value);
		}
	}
	
	public static void set_double(String name, double value) {
		ConfigState.DoubleSetting key = ConfigState.DOUBLE_LOOKUP.get(name);
		if (key == null) {
			System.out.println("Source Movement config error: the value \"" + name + "\" doesn't exist or is not a number");
		} else {
			Srcmov.config_state.set_double(key, value);
		}
	}
	
	public static void set_double_multiplier(String name, double multiplier) {
		ConfigState.DoubleSetting key = ConfigState.DOUBLE_LOOKUP.get(name);
		if (key == null) {
			System.out.println("Source Movement config error: the value \"" + name + "\" doesn't exist or is not a number");
		} else {
			Srcmov.config_state.set_double(key, multiplier * ConfigState.DOUBLE_DEFAULTS.get(key));
		}
	}
	
}
