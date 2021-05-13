package edu.jorbonism.source_movement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class ConfigLoader {

	public static File configFile = Srcmov.configFile;

	public static String getConfigFileString() {
		try {
			Scanner reader = new Scanner(configFile);
			// separate by char #0 (end of file) so it gets the entire file together
			reader.useDelimiter(Character.toString((char) 0));
			String data = reader.next();
			reader.close();
			return data;
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return "";
		}
	}

	public static void initConfig() {
		generateConfigFile();
		cacheActions();
	}

	public static void generateConfigFile() { generateConfigFile(false); }
	public static void generateConfigFile(boolean overwrite) {
		if (configFile.exists() && !overwrite) return;

		try {
			configFile.createNewFile();

			FileWriter writer = new FileWriter(configFile);

			writer.write("This is the Source Movement config file, where you can change all the settings and do crazy stuff\nEach movement config begins with an at symbol and includes a list \"keyword = value\" pairs, like this:\n\n@	Put the name here after the at\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nuse source fling detection = true\ngravity = 0.05\nhorizontal air friction = 0\nvertical air friction = 0.01\nground friction = 0.7x\nair control = 2.5x\nground control = 0.7x\nsprint jump boost = 0\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\njump friction multiplier = 0.5\n\n#\nA # symbol ends the config so you can leave comments after it, like this.\nIf you don't want to leave comments, there's no need for the #.\nThat config will be registered as Config 1 since it's the first in the file.\nIt also happens to be my recommended movement scheme for Portal 2 movement.\n\nIf that doesn't make sense:\n\ngravity = 0.16\n^ This sets gravity to 0.16, or twice the default.\n\ngravity = 2x\n^ This does exactly the same thing, but is a lot easier to understand an use\n\nenable abh = true\n^ This enables ABH\n\nenable abh: Y\n^ same thing\n\nAnything beginning with f or n is false, and t or y is true. Not case sensitive.\n\n\nHere are all of the modifiable settings and their default values:\n");

			for (Map.Entry<String,Boolean> m : Srcmov.configBooleans.entrySet()) {
				writer.write("\n" + m.getKey() + " = " + m.getValue());
			}
			writer.write("\n");
			for (Map.Entry<String,ConfigDouble> m : Srcmov.configDoubles.entrySet()) {
				writer.write("\n" + m.getKey() + " = " + m.getValue().defaultValue);
			}

			writer.write("\n\n\nNone of this above here will get loaded into a config since I never used an at symbol.\nBut this will:\n\n@	Portal 1 movement (with ABH)\nhorizontal air friction = 0\nvertical air friction = 0.01\nair control = 3x\norange box jump boosting = true\nwalk jump boost = 0.3x\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\nenable abh = true\ngravity = 1.2x\nground friction = 0.75x\nground control = 0.75x\n\n\n@	Example movement config\nsprint jump boost = 3x\ndirectional jump boosting = true\nhorizontal air friction = 0.01\nfall damage = 0\nsprint jump exhaustion = 0.5x\n#\n\nBy default (in this file) there are four configs.\nYou can add any number, but the number of keybind slots won't change until you re-open Minecraft.\nThe text after the at in the same line is supposed to be the name for it, but right now it's not used in the code anywhere so it doesn't actually matter.\n\nHave fun with it, and try not to break your game.\nHere is a variation of my recommended bunnyhopping config that's more for fun than accuracy:\n@	Source config 2\nhorizontal air friction = 0\nvertical air friction = 0.01\nair control = 4x\ngravity = 0.05\njump height = 1.5x\nsprint jump boost = 0\nfull speed jumps = true\nscroll jump = true\nuse source control cap = true\nsource control cap = 2x\nfall damage = 0\nsprint jump exhaustion = 0\njump exhaustion = 0\nclimb speed = 5x\nground friction = 0.7x\nground control = 0.7x\njump friction multiplier = 0.5x\n#\n\nAnother thing:\nThere's always a keybind to remove any custom movement effects and go back to vanilla.\nAlso, all keybinds in the mod are unbound by default so you can always hit reset to unbind them again if you want. I don't like it when you can't unbind things in modded minecraft.\n\n\n\nFor those of you still reading, velocity actions are basically a keypress that does something to your momentum.\nNot a part of source engine or anything, just for fun.\nIt works like a script, with a set of commands followed by a value to use:\n\nv* 2		multiply vertical velocity by 2\nv+ 1		add 1 blocks/tick to vertical velocity\nh* 2		multiply horizontal velocity by 2\nh+ 1		add 1 blocks/tick to horizontal velocity in the same direction\nh+f 1		add 1 blocks/tick to horizontal velocity facing forwards\nh+m 1		add 1 blocks/tick to horizontal velocity in movement direction\nt* 2		multiply total velocity by 2\nt+ 1		add 1 blocks/tick to total velocity in the same direction\nt+f 1		add 1 blocks/tick to total velocity facing forwards\n\nAnd you can chain them to do more complex maneuvers\nVelocity actions begin with a tilde:\n\n~	Example velocity action\nv* 0\nv+ 0.75\nh* 0.5\nh+f 0.5\n\n#\nThis will make you jump in whatever condition you are in (air, water, elytra, etc.) and will change your momentum to be more in the forwards direction.\nHere are 2 more useful ones:\n\n~	Stop vertical velocity\nv* 0\n\n~	Boost forwards\nt+f 1\n\n#\nIt would be a good idea to not hold down any velocity action keys unless they do something calm and simple, but I guess it's your game, not mine.\n\nIf you edit these velocity actions while Minecraft is open, you have to press a keybind to trigger a reload on all velocity keys.\nUnlike the movement configs, I don't want to make your computer read the file every time you activate one of those so that spamming can be fun and not laggy.\n\n\n\nThat's all I have to say.\nMy portal movement configs are far from a perfect recreation, so I encourage you to modify it an mess with some of the values.\nLet me know if you find a more accurate config, or configs for other source games.\n\nAlso I suggest you take everything you don't want out of this file and copy these instructions somewhere else so you can use them if you need to.\nIf you lose them you can always delete this file, then the game will load this back in.\n\nOh and PS: If you get banned from Hypixel for doing incredibly cheaty movement things, it's not my fault.\n\n\n");
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void cacheActions() {
		Srcmov.actions.clear();

		String[] actions = getConfigFileString().split("~");

		for (int i = 1; i < actions.length; i++) {
			VelocityAction action = new VelocityAction();

			String a = actions[i].split("@", 2)[0].split("#", 2)[0];
			String[] b = a.split("\n");
			for (int j = 1; j < b.length; j++) {

				String h = b[j].trim();
				if (h.length() == 0) continue;
				String[] h2 = h.split(" ");

				if (h2.length == 1)
					System.out.println("Source Movement config error: bad line in file: \"" + h + "\"");
				else
					action.add(h2[0], Double.parseDouble(h2[1]));
			}

			if (action.size() > 0) Srcmov.actions.add(action);
		}
	}

	public static void loadConfig(int index) {
		Srcmov.playerController.reset();

		String[] data = getConfigFileString().split("@");
		if (index >= data.length - 1) return;

		String[] config = data[index + 1].split("~", 2)[0].split("#", 2)[0].split("\n");

		for (int i = 1; i < config.length; i++) {
			String line = config[i].trim();
			if (line.isEmpty()) continue;
			
			String[] parts = line.split("=");
			if (parts.length < 2) parts = parts[0].split(":");
			if (parts.length != 2) System.out.println("Source Movement config error: \"" + line + "\" doesn't make any sense to me!");
			else {
				String name = parts[0].trim(), value = parts[1].trim();
				setConfigValue(name, value);
			}
		}
	}

	public static int getConfigCount() {
		return getConfigFileString().split("@").length - 1;
	}
	
	public static void setConfigValue(String valueName, String value) {
		char v1 = value.charAt(0);
		int mode;
		String v2;
		if (v1 == 't' || v1 == 'T' || v1 == 'y' || v1 == 'Y') {
			mode = 2;
			v2 = "t";
		} else if (v1 == 'f' || v1 == 'F' || v1 == 'n' || v1 == 'N') {
			mode = 3;
			v2 = "f";
		} else if (v1 == 'x') {
			mode = 1;
			v2 = value.substring(1);
		} else if (value.charAt(value.length() - 1) == 'x') {
			mode = 1;
			v2 = value.substring(0, value.length() - 1);
		} else {
			mode = 0;
			v2 = value;
		}
		
		switch (mode) {
			case 0: Srcmov.playerController.setDouble(valueName, Double.parseDouble(v2)); break;
			case 1: Srcmov.playerController.setDoubleMultiplier(valueName, Double.parseDouble(v2)); break;
			case 2: Srcmov.playerController.setBoolean(valueName, true); break;
			case 3: Srcmov.playerController.setBoolean(valueName, false); break;
		}
	}


	
}
