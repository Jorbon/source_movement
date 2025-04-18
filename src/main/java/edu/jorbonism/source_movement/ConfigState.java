package edu.jorbonism.source_movement;

import java.util.HashMap;
import java.util.Map;

public class ConfigState {
	
	
	public enum BooleanSetting {
		ScrollJumping,
		DirectionalJumpBoosting,
	}
	
	public static final Map<String, BooleanSetting> BOOLEAN_LOOKUP = Map.ofEntries(
		Map.entry("scroll_jumping", BooleanSetting.ScrollJumping),
		Map.entry("directional_jump_boosting", BooleanSetting.DirectionalJumpBoosting)
	);
	
	public static final Map<BooleanSetting, Boolean> BOOLEAN_DEFAULTS = Map.ofEntries(
		Map.entry(BooleanSetting.ScrollJumping, false),
		Map.entry(BooleanSetting.DirectionalJumpBoosting, false)
	);
	
	
	
	public enum DoubleSetting {
		Gravity,
		JumpPower,
		BoostSpeed,
		JumpBoostSprintSpeed,
		JumpBoostWalkSpeed,
		JumpingCooldown,
	}
	
	public static final Map<String, DoubleSetting> DOUBLE_LOOKUP = Map.ofEntries(
		Map.entry("gravity", DoubleSetting.Gravity),
		Map.entry("jump_power", DoubleSetting.JumpPower),
		Map.entry("boost_speed", DoubleSetting.BoostSpeed),
		Map.entry("jump_boost_sprint_speed", DoubleSetting.JumpBoostSprintSpeed),
		Map.entry("jump_boost_walk_speed", DoubleSetting.JumpBoostWalkSpeed),
		Map.entry("jumping_cooldown", DoubleSetting.JumpingCooldown)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_DEFAULTS = Map.ofEntries(
		Map.entry(DoubleSetting.Gravity, 0.08),
		Map.entry(DoubleSetting.JumpPower, 0.42),
		Map.entry(DoubleSetting.BoostSpeed, 0.0),
		Map.entry(DoubleSetting.JumpBoostSprintSpeed, 0.2),
		Map.entry(DoubleSetting.JumpBoostWalkSpeed, 0.0)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MINS = Map.ofEntries(
		Map.entry(DoubleSetting.JumpPower, 0.0)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MAXES = Map.of();
	
	public static final Map<DoubleSetting, Double> DOUBLE_BASE_MULTIPLE = Map.ofEntries(
		Map.entry(DoubleSetting.BoostSpeed, 1.0),
		Map.entry(DoubleSetting.JumpBoostWalkSpeed, 0.2)
	);
	
	
	
	private Map<BooleanSetting, Boolean> boolean_values = new HashMap<BooleanSetting, Boolean>();
	private Map<DoubleSetting, Double> double_values = new HashMap<DoubleSetting, Double>();
	
	public ConfigState() {
		reset();
	}
	
	public void reset() {
		boolean_values.putAll(ConfigState.BOOLEAN_DEFAULTS);
		double_values.putAll(ConfigState.DOUBLE_DEFAULTS);
	}
	
	public boolean get_boolean(BooleanSetting key) {
		return boolean_values.get(key);
	}
	
	public double get_double(DoubleSetting key) {
		return double_values.get(key);
	}
	
	public void set_boolean(BooleanSetting key, boolean value) {
		boolean_values.put(key, value);
	}
	
	public void set_double(DoubleSetting key, double value) {
		Double min = DOUBLE_MINS.get(key);
		Double max = DOUBLE_MAXES.get(key);
		
		if (min != null && value < min) {
			double_values.put(key, min);
		} else if (max != null && value > max) {
			double_values.put(key, max);
		} else {
			double_values.put(key, value);
		}
	}
	
	
}


// configBooleans.put("directional jump boosting", false);
// configBooleans.put("orange box jump boosting", false);
// configBooleans.put("full speed jumps", false);
// configBooleans.put("use source control cap", false);
// configBooleans.put("use source fling detection", false);
// configBooleans.put("enable abh", false);

// configDoubles.put("horizontal air friction", new ConfigDouble(0.09, 0.09, 0, 1));
// configDoubles.put("vertical air friction", new ConfigDouble(0.02, 0.02, 0, 1));

// configDoubles.put("ground friction", new ConfigDouble(1));
// configDoubles.put("air control", new ConfigDouble(0.02));
// configDoubles.put("ground control", new ConfigDouble(0.1));
// configDoubles.put("climb speed", new ConfigDouble(0.2));
// configDoubles.put("climb movement cap", new ConfigDouble(0.15));
// configDoubles.put("horizontal fly friction", new ConfigDouble(0.09, 0.09, 0, 1));
// configDoubles.put("fly speed", new ConfigDouble(0.05));
// configDoubles.put("vertical fly friction", new ConfigDouble(0.4, 0.4, 0, 1));
// configDoubles.put("levitation strength", new ConfigDouble(0.05));
// configDoubles.put("source control cap", new ConfigDouble(60 * SRC_TO_MC));
// configDoubles.put("min fling speed", new ConfigDouble(300 * SRC_TO_MC));
// configDoubles.put("abh threshold", new ConfigDouble(3.75));
// configDoubles.put("jump friction multiplier", new ConfigDouble(1));

// configBooleans.put("velocity based fall damage", false);
// configDoubles.put("fall damage", new ConfigDouble(1, 1, 0));
// configDoubles.put("sprint jump exhaustion", new ConfigDouble(0.2, 0.2, 0));
// configDoubles.put("jump exhaustion", new ConfigDouble(0.05, 0.05, 0));

