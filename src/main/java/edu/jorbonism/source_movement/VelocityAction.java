package edu.jorbonism.source_movement;

import java.util.ArrayList;

import net.minecraft.util.math.Vec3d;

public class VelocityAction {

	public ArrayList<VelocityActionStep> actions;
	
	public VelocityAction() {
		actions = new ArrayList<VelocityActionStep>();
	}

	public VelocityAction(ArrayList<VelocityActionStep> steps) {
		actions = steps;
	}

	public void add(VelocityActionStep step) {
		this.actions.add(step);
	}

	public void add(String typeString, double value) {
		this.add(new VelocityActionStep(typeString, value));
	}

	public int size() {
		return this.actions.size();
	}

	public void queue() {
		for (int i = 0; i < this.size(); i++) {
			Srcmov.actionQueue.add(this.actions.get(i));
		}
	}

	public Vec3d execute(Vec3d v, Vec3d movementInput, double yawRadians, double pitchRadians) {
		for (int i = 0; i < this.size(); i++) {
			v = this.actions.get(i).execute(v, movementInput, yawRadians, pitchRadians);
		}
		return v;
	}
}
