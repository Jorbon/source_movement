package edu.jorbonism.source_movement;

import java.util.HashMap;
import java.util.Map;

public class ConfigState {
	
	
	public enum BooleanSetting {
		ScrollJumping,
		JumpBoostDirectionally,
		Abh,
	}
	
	public static final Map<String, BooleanSetting> BOOLEAN_LOOKUP = Map.ofEntries(
		Map.entry("scroll_jumping", BooleanSetting.ScrollJumping),
		Map.entry("jump_boost_directionally", BooleanSetting.JumpBoostDirectionally),
		Map.entry("abh", BooleanSetting.Abh)
	);
	
	public static final Map<BooleanSetting, Boolean> BOOLEAN_DEFAULTS = Map.ofEntries(
		Map.entry(BooleanSetting.ScrollJumping, false),
		Map.entry(BooleanSetting.JumpBoostDirectionally, false),
		Map.entry(BooleanSetting.Abh, false)
	);
	
	
	
	public enum DoubleSetting {
		Gravity,
		JumpPower,
		BoostSpeed,
		
		GroundSpeedWalk,
		AirSpeedWalk,
		GroundSpeedSprint,
		AirSpeedSprint,
		GroundSpeedSneak,
		AirSpeedSneak,
		Traction,
		Friction,
		
		HorizontalDrag,
		VerticalDrag,
		FlySpeedWalk,
		FlySpeedSprint,
		
		DiagonalBonusWalk,
		DiagonalBonusSprint,
		DiagonalBonusSneak,
		
		JumpSpeedBoostWalk,
		JumpSpeedBoostSprint,
		JumpSpeedBoostSneak,
		
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
		ElytraVerticalDrag,
		
		HopFriction,
		AbhLevelWalk,
		AbhLevelSprint,
		AbhLevelSneak
	}
	
	public static final Map<String, DoubleSetting> DOUBLE_LOOKUP = Map.ofEntries(
		Map.entry("gravity", DoubleSetting.Gravity),
		Map.entry("jump_power", DoubleSetting.JumpPower),
		Map.entry("boost_speed", DoubleSetting.BoostSpeed),
		
		Map.entry("ground_speed_walk", DoubleSetting.GroundSpeedWalk),
		Map.entry("air_speed_walk", DoubleSetting.AirSpeedWalk),
		Map.entry("ground_speed_sprint", DoubleSetting.GroundSpeedSprint),
		Map.entry("air_speed_sprint", DoubleSetting.AirSpeedSprint),
		Map.entry("ground_speed_sneak", DoubleSetting.GroundSpeedSneak),
		Map.entry("air_speed_sneak", DoubleSetting.AirSpeedSneak),
		
		Map.entry("traction", DoubleSetting.Traction),
		Map.entry("friction", DoubleSetting.Friction),
		Map.entry("horizontal_drag", DoubleSetting.HorizontalDrag),
		Map.entry("vertical_drag", DoubleSetting.VerticalDrag),
		
		Map.entry("fly_speed_walk", DoubleSetting.FlySpeedWalk),
		Map.entry("fly_speed_sprint", DoubleSetting.FlySpeedSprint),
		
		Map.entry("diagonal_bonus_walk", DoubleSetting.DiagonalBonusWalk),
		Map.entry("diagonal_bonus_sprint", DoubleSetting.DiagonalBonusSprint),
		Map.entry("diagonal_bonus_sneak", DoubleSetting.DiagonalBonusSneak),
		
		Map.entry("jump_speed_boost_walk", DoubleSetting.JumpSpeedBoostWalk),
		Map.entry("jump_speed_boost_sprint", DoubleSetting.JumpSpeedBoostSprint),
		Map.entry("jump_speed_boost_sneak", DoubleSetting.JumpSpeedBoostSneak),
		
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
		Map.entry("elytra_vertical_drag", DoubleSetting.ElytraVerticalDrag),
		
		Map.entry("hop_friction", DoubleSetting.HopFriction),
		Map.entry("abh_level_walk", DoubleSetting.AbhLevelWalk),
		Map.entry("abh_level_sprint", DoubleSetting.AbhLevelSprint),
		Map.entry("abh_level_sneak", DoubleSetting.AbhLevelSneak)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_DEFAULTS = Map.ofEntries(
		Map.entry(DoubleSetting.Gravity, 0.08),
		Map.entry(DoubleSetting.JumpPower, 0.42),
		Map.entry(DoubleSetting.BoostSpeed, 0.0),
		
		Map.entry(DoubleSetting.GroundSpeedWalk, 0.098),
		Map.entry(DoubleSetting.AirSpeedWalk, 0.0196),
		Map.entry(DoubleSetting.GroundSpeedSprint, 0.1274),
		Map.entry(DoubleSetting.AirSpeedSprint, 0.02548),
		Map.entry(DoubleSetting.GroundSpeedSneak, 0.0294),
		Map.entry(DoubleSetting.AirSpeedSneak, 0.00588),
		
		Map.entry(DoubleSetting.Traction, 0.216),
		Map.entry(DoubleSetting.Friction, 1.0),
		Map.entry(DoubleSetting.HorizontalDrag, 0.09),
		Map.entry(DoubleSetting.VerticalDrag, 0.02),
		Map.entry(DoubleSetting.FlySpeedWalk, 0.049),
		Map.entry(DoubleSetting.FlySpeedSprint, 0.098),
		
		Map.entry(DoubleSetting.DiagonalBonusWalk, 0.0492696645382),
		Map.entry(DoubleSetting.DiagonalBonusSprint, 0.0492696645382),
		Map.entry(DoubleSetting.DiagonalBonusSneak, 1.0),
		
		Map.entry(DoubleSetting.JumpSpeedBoostWalk, 0.0),
		Map.entry(DoubleSetting.JumpSpeedBoostSprint, 0.2),
		Map.entry(DoubleSetting.JumpSpeedBoostSneak, 0.0),
		
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
		Map.entry(DoubleSetting.ElytraVerticalDrag, 0.02),
		
		Map.entry(DoubleSetting.HopFriction, 1.0),
		Map.entry(DoubleSetting.AbhLevelWalk, 0.1),
		Map.entry(DoubleSetting.AbhLevelSprint, 0.13),
		Map.entry(DoubleSetting.AbhLevelSneak, 0.05)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MINS = Map.ofEntries(
		Map.entry(DoubleSetting.JumpPower, 0.0),
		
		Map.entry(DoubleSetting.Friction, 0.0),
		Map.entry(DoubleSetting.HorizontalDrag, 0.0),
		Map.entry(DoubleSetting.VerticalDrag, 0.0),
		
		Map.entry(DoubleSetting.DiagonalBonusWalk, 0.0),
		Map.entry(DoubleSetting.DiagonalBonusSprint, 0.0),
		Map.entry(DoubleSetting.DiagonalBonusSneak, 0.0),
		
		Map.entry(DoubleSetting.ClimbSpeed, 0.0),
		Map.entry(DoubleSetting.ClimbMaxFallingSpeed, 0.0),
		Map.entry(DoubleSetting.ClimbMaxHorizontalSpeed, 0.0),
		
		Map.entry(DoubleSetting.ElytraRedirection, 0.0),
		Map.entry(DoubleSetting.ElytraHorizontalDrag, 0.0),
		Map.entry(DoubleSetting.ElytraVerticalDrag, 0.0),
		
		Map.entry(DoubleSetting.HopFriction, 0.0)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_MAXES = Map.ofEntries(
		Map.entry(DoubleSetting.HorizontalDrag, 1.0),
		Map.entry(DoubleSetting.VerticalDrag, 1.0),
		
		Map.entry(DoubleSetting.DiagonalBonusWalk, 1.0),
		Map.entry(DoubleSetting.DiagonalBonusSprint, 1.0),
		Map.entry(DoubleSetting.DiagonalBonusSneak, 1.0),
		
		Map.entry(DoubleSetting.ElytraRedirection, 1.0),
		Map.entry(DoubleSetting.ElytraHorizontalDrag, 1.0),
		Map.entry(DoubleSetting.ElytraVerticalDrag, 1.0)
	);
	
	public static final Map<DoubleSetting, Double> DOUBLE_BASE_MULTIPLE = Map.ofEntries(
		Map.entry(DoubleSetting.BoostSpeed, 1.0),
		Map.entry(DoubleSetting.JumpSpeedBoostWalk, 0.2),
		Map.entry(DoubleSetting.JumpSpeedBoostSneak, 0.2)
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

