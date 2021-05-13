package edu.jorbonism.source_movement;

// This class is meant to hold physics values to be modified while protecting things from game-breaking values, such as friction values being negative, as this would cause exponential growth and crash the game (and if you are wondering, yes, this happened during testing more than once).

public class ConfigDouble {
	public final double defaultValue;
	public final double increment;
	public final double min;
	public final double max;
	public final boolean hasMin;
	public final boolean hasMax;

	public ConfigDouble(double defaultValue, double increment, double min, double max) {
		this.hasMax = true;
		this.hasMin = true;

		if (max < min) {
			this.min = max;
			this.max = min;
		} else {
			this.min = min;
			this.max = max;
		}

		this.defaultValue = applyLimits(defaultValue);

		this.increment = increment;
	}

	public ConfigDouble(double defaultValue, double increment, double min) {
		this.hasMax = false;
		this.hasMin = true;
		this.min = min;
		this.max = 0;

		this.defaultValue = applyLimits(defaultValue);

		this.increment = increment;
	}

	public ConfigDouble(double defaultValue, double increment) {
		this.hasMax = false;
		this.hasMin = false;
		this.min = 0;
		this.max = 0;
		this.defaultValue = defaultValue;
		this.increment = increment;
	}

	public ConfigDouble(double defaultValue) {
		this(defaultValue, defaultValue);
	}

	public double applyLimits(double value) {
		if (this.hasMin && value < this.min)
			return this.min;
		else if (this.hasMax && value > this.max)
			return this.max;
		return value;
	}

	public double fromMultiplier(double mult) {
		return applyLimits(mult * this.increment);
	}
}
