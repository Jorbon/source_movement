package edu.jorbonism.source_movement;

import java.util.HashMap;
import java.util.Map;

public class ConfigState {
	
	
	public enum BooleanSetting {
		ScrollJumping,
		JumpBoostDirectionally,
	}
	
	public static final Map<String, BooleanSetting> BOOLEAN_LOOKUP = Map.ofEntries(
		Map.entry("scroll_jumping", BooleanSetting.ScrollJumping),
		Map.entry("jump_boost_directionally", BooleanSetting.JumpBoostDirectionally)
	);
	
	public static final Map<BooleanSetting, Boolean> BOOLEAN_DEFAULTS = Map.ofEntries(
		Map.entry(BooleanSetting.ScrollJumping, false),
		Map.entry(BooleanSetting.JumpBoostDirectionally, false)
	);
	
	
	
	public enum DoubleSetting {
		Gravity,
		JumpPower,
		BoostSpeed,
		JumpBoostSprintSpeed,
		JumpBoostWalkSpeed,
		
		ClimbSpeed,
		ClimbMaxFallingSpeed,
		ClimbMaxHorizontalSpeed,
		
		ElytraGravityMultiplier,
		ElytraGravityModifier,
		ElytraOutwardLift,
		ElytraOutwardForce,
		ElytraUpwardLift,
		ElytraUpwardDrag,
		ElytraRedirection,
		ElytraHorizontalDrag,
		ElytraVerticalDrag
	}
	
	public static final Map<String, DoubleSetting> DOUBLE_LOOKUP = Map.ofEntries(
		Map.entry("gravity", DoubleSetting.Gravity),
		Map.entry("jump_power", DoubleSetting.JumpPower),
		Map.entry("boost_speed", DoubleSetting.BoostSpeed),
		Map.entry("jump_boost_sprint_speed", DoubleSetting.JumpBoostSprintSpeed),
		Map.entry("jump_boost_walk_speed", DoubleSetting.JumpBoostWalkSpeed),
		
		Map.entry("climb_speed", DoubleSetting.ClimbSpeed),
		Map.entry("climb_max_falling_speed", DoubleSetting.ClimbMaxFallingSpeed),
		Map.entry("climb_max_horizontal_speed", DoubleSetting.ClimbMaxHorizontalSpeed),
		
		Map.entry("elytra_gravity_multiplier", DoubleSetting.ElytraGravityMultiplier),
		Map.entry("elytra_gravity_modifier", DoubleSetting.ElytraGravityModifier),
		Map.entry("elytra_outward_lift", DoubleSetting.ElytraOutwardLift),
		Map.entry("elytra_outward_force", DoubleSetting.ElytraOutwardForce),
		Map.entry("elytra_upward_lift", DoubleSetting.ElytraUpwardLift),
		Map.entry("elytra_upward_drag", DoubleSetting.ElytraUpwardDrag),
		Map.entry("elytra_redirection", DoubleSetting.ElytraRedirection),
		Map.entry("elytra_horizontal_drag", DoubleSetting.ElytraHorizontalDrag),
		Map.entry("elytra_vertical_drag", DoubleSetting.ElytraVerticalDrag)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_DEFAULTS = Map.ofEntries(
		Map.entry(DoubleSetting.Gravity, 0.08),
		Map.entry(DoubleSetting.JumpPower, 0.42),
		Map.entry(DoubleSetting.BoostSpeed, 0.0),
		Map.entry(DoubleSetting.JumpBoostSprintSpeed, 0.2),
		Map.entry(DoubleSetting.JumpBoostWalkSpeed, 0.0),
		
		Map.entry(DoubleSetting.ClimbSpeed, 0.2),
		Map.entry(DoubleSetting.ClimbMaxFallingSpeed, 0.15),
		Map.entry(DoubleSetting.ClimbMaxHorizontalSpeed, 0.15),
		
		Map.entry(DoubleSetting.ElytraGravityMultiplier, 1.0),
		Map.entry(DoubleSetting.ElytraGravityModifier, 0.25),
		Map.entry(DoubleSetting.ElytraOutwardLift, 0.1),
		Map.entry(DoubleSetting.ElytraOutwardForce, 0.1),
		Map.entry(DoubleSetting.ElytraUpwardLift, 0.128),
		Map.entry(DoubleSetting.ElytraUpwardDrag, 0.04),
		Map.entry(DoubleSetting.ElytraRedirection, 0.1),
		Map.entry(DoubleSetting.ElytraHorizontalDrag, 0.01),
		Map.entry(DoubleSetting.ElytraVerticalDrag, 0.02)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MINS = Map.ofEntries(
		Map.entry(DoubleSetting.JumpPower, 0.0),
		Map.entry(DoubleSetting.ClimbSpeed, 0.0),
		Map.entry(DoubleSetting.ClimbMaxFallingSpeed, 0.0),
		Map.entry(DoubleSetting.ClimbMaxHorizontalSpeed, 0.0),
		Map.entry(DoubleSetting.ElytraRedirection, 0.0),
		Map.entry(DoubleSetting.ElytraHorizontalDrag, 0.0),
		Map.entry(DoubleSetting.ElytraVerticalDrag, 0.0)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MAXES = Map.ofEntries(
		Map.entry(DoubleSetting.ElytraRedirection, 1.0),
		Map.entry(DoubleSetting.ElytraHorizontalDrag, 1.0),
		Map.entry(DoubleSetting.ElytraVerticalDrag, 1.0)
	);
	
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

