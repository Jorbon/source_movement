package edu.jorbonism.source_movement;

import java.util.HashMap;
import java.util.Map;

public class SrcmovPC {

	public boolean enabled = false;
	public final HashMap<String,Double> doubles;
	public final HashMap<String,Boolean> booleans;

	public SrcmovPC() {
		doubles = new HashMap<String,Double>();
		booleans = new HashMap<String,Boolean>();

		reset();
	}

	public void setDouble(String key, double value) {
		if (Srcmov.configDoubles.containsKey(key))
			doubles.put(key, Srcmov.configDoubles.get(key).applyLimits(value));
		else
			System.out.println("Source Movement config error: the value \"" + key + "\" doesn't exist or is not a number");
	}

	public void setDoubleMultiplier(String key, double mult) {
		if (Srcmov.configDoubles.containsKey(key))
			doubles.put(key, Srcmov.configDoubles.get(key).fromMultiplier(mult));
		else
			System.out.println("Source Movement config error: the value \"" + key + "\" doesn't exist or is not a number");
	}

	public void setBoolean(String key, boolean value) {
		if (Srcmov.configBooleans.containsKey(key))
			booleans.put(key, value);
		else
			System.out.println("Source Movement config error: the value \"" + key + "\" doesn't exist or is not a boolean");
	}

	public void reset() {
		for (Map.Entry<String,ConfigDouble> m : Srcmov.configDoubles.entrySet())
			doubles.put(m.getKey(), m.getValue().defaultValue);

		booleans.putAll(Srcmov.configBooleans);
	}
}
