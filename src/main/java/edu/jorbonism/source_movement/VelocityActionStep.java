package edu.jorbonism.source_movement;

import net.minecraft.util.math.Vec3d;

public class VelocityActionStep {
	int type;
	double value;

	VelocityActionStep(int type, double value) {
		this.type = type;
		this.value = value;
	}

	VelocityActionStep(String typeString, double value) {
		if (typeString.equals("v*")) this.type = 0;
		else if (typeString.equals("v+")) this.type = 1;
		else if (typeString.equals("h*")) this.type = 2;
		else if (typeString.equals("h+")) this.type = 3;
		else if (typeString.equals("h+f")) this.type = 4;
		else if (typeString.equals("h+m")) this.type = 5;
		else if (typeString.equals("t*")) this.type = 6;
		else if (typeString.equals("t+")) this.type = 7;
		else if (typeString.equals("t+f")) this.type = 8;
		else if (typeString.equals("t+m")) this.type = 9;

		this.value = value;
	}

	Vec3d execute(Vec3d v, Vec3d movementInput, double yawRadians, double pitchRadians) {
		double fx, fy, fz, m, f, g;
		Vec3d vec3d;
		switch(this.type) {
			case 0: return new Vec3d(v.x, v.y * this.value, v.z);
			case 1: return new Vec3d(v.x, v.y + this.value, v.z);
			case 2: return new Vec3d(v.x * this.value, v.y, v.z * this.value);
			case 3:
				if (v.x < 1E-4 && v.z < 1E-4) return v;
				m = Math.sqrt(v.x * v.x + v.z * v.z);
				fx = v.x / m;
				fz = v.z / m;
				return new Vec3d(v.x + fx * this.value, v.y, v.z + fz * this.value);
			case 4:
				fx = -Math.sin(yawRadians);
				fz = Math.cos(yawRadians);
				return new Vec3d(v.x + fx * this.value, v.y, v.z + fz * this.value);
			case 5:
				m = movementInput.x * movementInput.x + movementInput.z * movementInput.z;
				if (m < 1.0E-7) return v;
				vec3d = (m > 1 ? movementInput.normalize() : movementInput).multiply(this.value);
				f = Math.sin(yawRadians);
				g = Math.cos(yawRadians);
				return new Vec3d(v.x + vec3d.x * g - vec3d.z * f, v.y, v.z + vec3d.z * g + vec3d.x * f);
			case 6: return v.multiply(this.value);
			case 7:
				if (v.x < 1E-4 && v.y < 1E-4 && v.z < 1E-4) return v;
				m = v.length();
				fx = v.x / m;
				fy = v.y / m;
				fz = v.z / m;
				return new Vec3d(v.x + fx * this.value, v.y + fy * this.value, v.z + fz * this.value);
			case 8:
				fx = -Math.sin(yawRadians) * Math.cos(pitchRadians);
				fz = Math.cos(yawRadians) * Math.cos(pitchRadians);
				fy = -Math.sin(pitchRadians);
				return new Vec3d(v.x + fx * this.value, v.y + fy * this.value, v.z + fz * this.value);
		}
		return v;
	}
}
