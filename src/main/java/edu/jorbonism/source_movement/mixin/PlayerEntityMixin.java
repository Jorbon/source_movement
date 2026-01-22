package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import edu.jorbonism.source_movement.ConfigState;
import edu.jorbonism.source_movement.ConfigState.DoubleSetting;
import edu.jorbonism.source_movement.ClientMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	
	// private boolean onGroundPrevious = true;
	
	protected PlayerEntityMixin(EntityType<? extends PlayerEntity> entityType, World world) { super(entityType, world); }
	
	
	@Override
	public void jump() {
		if (!ClientMain.enabled) {
			super.jump();
			return;
		}
		
		float jump_velocity = this.getJumpVelocity();
		if (!(jump_velocity <= 1.0E-5F)) {
			
			// Choose boost amount
			float boost = 0.0f;
			if (this.isSprinting()) {
				boost = (float) ClientMain.config_state.get_double(ConfigState.DoubleSetting.JumpSpeedBoostSprint);
			} else if (Math.abs(this.forwardSpeed) > 1e-7 || Math.abs(this.sidewaysSpeed) > 1e-7) {
				if (this.isSneaking()) {
					boost = (float) ClientMain.config_state.get_double(ConfigState.DoubleSetting.JumpSpeedBoostSneak);
				} else boost = (float) ClientMain.config_state.get_double(ConfigState.DoubleSetting.JumpSpeedBoostWalk);
			}
			
			// Choose boost direction
			float yaw_radians = this.getYaw() * (float) (Math.PI / 180.0);
			if (ClientMain.config_state.get_boolean(ConfigState.BooleanSetting.JumpBoostDirectionally)) {
					 if ( this.sidewaysSpeed > this.forwardSpeed &&  this.sidewaysSpeed > -this.forwardSpeed) yaw_radians -= Math.PI * 0.5;
				else if (-this.sidewaysSpeed > this.forwardSpeed && -this.sidewaysSpeed > -this.forwardSpeed) yaw_radians += Math.PI * 0.5;
				else if (this.forwardSpeed < 0.0) yaw_radians += Math.PI;
			}
			
			Vec3d velocity = this.getVelocity();
			this.setVelocity(velocity.x - MathHelper.sin(yaw_radians) * boost, Math.max(jump_velocity, velocity.y), velocity.z + MathHelper.cos(yaw_radians) * boost);
			
			this.velocityDirty = true;
		}
	}
	
	@Override
	protected float getJumpVelocity(float strength) {
		if (!ClientMain.enabled) return super.getJumpVelocity(strength);
		return (float) ClientMain.config_state.get_double(ConfigState.DoubleSetting.JumpPower) * strength * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
	}
	
	
	@Override
	protected double getGravity() {
		return ClientMain.config_state.get_double(ConfigState.DoubleSetting.Gravity);
	}
	
	
	@Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
	public void getMovementSpeedMixin(CallbackInfoReturnable<Float> cir) {
		if (!ClientMain.enabled) return;
		
		double speed;
		if (this.isSneaking()) speed = ClientMain.config_state.get_double(DoubleSetting.GroundSpeedSneak);
		else if (this.isSprinting()) speed = ClientMain.config_state.get_double(DoubleSetting.GroundSpeedSprint);
		else speed = ClientMain.config_state.get_double(DoubleSetting.GroundSpeedWalk);
		
		double diagonal_bonus;
		if (this.isSneaking()) diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSneak);
		else if (this.isSprinting()) diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSprint);
		else diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusWalk);
		
		speed *= 1.0 + (Math.sqrt(2.0) - 1.0) * diagonal_bonus;
		
		cir.setReturnValue((float) speed);
		cir.cancel();
	}
	
	@Inject(method = "getOffGroundSpeed", at = @At("HEAD"), cancellable = true)
	protected void getOffGroundSpeedMixin(CallbackInfoReturnable<Float> cir) {
		if (!ClientMain.enabled) return;
		
		double speed;
		if (this.abilities.flying && !this.hasVehicle()) {
			if (this.isSprinting()) speed = this.abilities.getFlySpeed() * 20.0 * ClientMain.config_state.get_double(DoubleSetting.FlySpeedSprint);
			else speed = this.abilities.getFlySpeed() * 20.0 * ClientMain.config_state.get_double(DoubleSetting.FlySpeedWalk);
		} else if (this.isSneaking()) speed = ClientMain.config_state.get_double(DoubleSetting.AirSpeedSneak);
		else if (this.isSprinting()) speed = ClientMain.config_state.get_double(DoubleSetting.AirSpeedSprint);
		else speed = ClientMain.config_state.get_double(DoubleSetting.AirSpeedWalk);
		
		double diagonal_bonus;
		if (this.isSneaking()) diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSneak);
		else if (this.isSprinting()) diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSprint);
		else diagonal_bonus = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusWalk);
		
		speed *= 1.0 + ((Math.sqrt(2.0) - 1.0) * diagonal_bonus);
		
		cir.setReturnValue((float) speed);
		cir.cancel();
	}
	
	
	
	@Shadow private final PlayerAbilities abilities = new PlayerAbilities();
	@Shadow protected abstract boolean clipAtLedge();
	@Shadow private boolean method_30263(float f) { return false; }
	
	
	
	
}






	// private void applyFriction(double blockSlipperiness) {
	// 	double frictionFactor;
	// 	if (this.isOnGround()) {
	// 		frictionFactor = srcmov.doubles.get("ground friction") * (1 - blockSlipperiness * 0.91);
	// 		if (!this.onGroundPrevious)
	// 			frictionFactor *= srcmov.doubles.get("jump friction multiplier");
	// 	} else if (this.abilities.flying)
	// 		frictionFactor = srcmov.doubles.get("horizontal fly friction");
	// 	else
	// 		frictionFactor = srcmov.doubles.get("horizontal air friction");
		
	// 	Vec3d velocity = this.getVelocity();
	// 	double x = velocity.x, y = velocity.y, z = velocity.z;
		
	// 	// the signature ABH anti-bhop
	// 	if (srcmov.booleans.get("enable abh") && this.isOnGround() && !this.onGroundPrevious) {
	// 		double horizontalSpeed2 = x * x + z * z;
	// 		if (horizontalSpeed2 < 1E-8)
	// 			return;
			
	// 		double horizontalSpeed = Math.sqrt(horizontalSpeed2);
			
			
	// 		// adjust for analog input; this does't account for the apparent diagonal speed boost present in vanilla in all movement modes, but that doesn't really matter
	// 		double inputSpeed = this.forwardSpeed * this.forwardSpeed + this.sidewaysSpeed * this.sidewaysSpeed;
	// 		if (inputSpeed >= 1)
	// 			inputSpeed = 1;
	// 		else
	// 			inputSpeed = Math.sqrt(inputSpeed);
			
			
	// 		double maxSpeed = speedTarget() * srcmov.doubles.get("abh threshold");
			
	// 		if (horizontalSpeed > maxSpeed) {
	// 			double speedAddition = maxSpeed - horizontalSpeed;
				
	// 			// deduce the forward direction (erroneously)
	// 			float yawRadians = this.getYaw() * 0.017453292F;
	// 			Vec3d forward = new Vec3d(-MathHelper.sin(yawRadians), 0, MathHelper.cos(yawRadians));
	// 			if (this.forwardSpeed < 0) // if s key, reverse forward direction
	// 				forward = forward.multiply(-1);
				
	// 			Vec3d change = forward.multiply(speedAddition);
	// 			x += change.x;
	// 			z += change.z;
	// 		} else {
	// 			// normal friction
	// 			x *= 1 - frictionFactor;
	// 			z *= 1 - frictionFactor;
	// 		}
	// 	} else {
	// 		// normal friction
	// 		x *= 1 - frictionFactor;
	// 		z *= 1 - frictionFactor;
	// 	}
		
	// 	// apply vertical friction
	// 	if (this.abilities.flying && !this.hasVehicle())
	// 		y *= 1 - srcmov.doubles.get("vertical fly friction");
	// 	else
	// 		y *= 1 - srcmov.doubles.get("vertical air friction");
		
	// 	this.setVelocity(x, y, z);
	// }
	
	
	
	
	// private void applyInputAcceleration(double blockSlipperiness, Vec3d movementInput) {
	// 	double speed = this.getMovementSpeedMain(blockSlipperiness);
		
	// 	double d = movementInput.lengthSquared();
	// 	if (d < 1E-8)
	//	 	return;
			
	//	 Vec3d inputAcc = (d > 1 ? movementInput.normalize() : movementInput).multiply(speed);
	//	 double f = MathHelper.sin(this.getYaw() * 0.017453292F);
	//	 double g = MathHelper.cos(this.getYaw() * 0.017453292F);
	//	 Vec3d a = new Vec3d(inputAcc.x * g - inputAcc.z * f, inputAcc.y, inputAcc.z * g + inputAcc.x * f);
		
		
	// 	// implement the soft control cap at 300su by default
	// 	if (srcmov.booleans.get("use source fling detection") && !this.abilities.flying) {
	// 		Vec3d v = this.getVelocity();
	// 		double vlen2 = v.x * v.x + v.z * v.z;
	// 		double flingSpeed = srcmov.doubles.get("min fling speed");
	// 		double ax = a.x, az = a.z;
	// 		if (vlen2 >= flingSpeed * flingSpeed) {
	// 			// if a velocity on an axis exceeds 150ups, don't allow deceleration in that axis
	// 			if ((v.x > flingSpeed * 0.5 && ax < 0) || (v.x < -flingSpeed * 0.5 && ax > 0)) ax = 0;
	// 			if ((v.z > flingSpeed * 0.5 && az < 0) || (v.z < -flingSpeed * 0.5 && az > 0)) az = 0;
				
	// 			a = new Vec3d(ax, a.y, az);
	// 		}
	// 	}
		
	// 	// main air strafe code
	// 	if (srcmov.booleans.get("use source control cap") && !this.isOnGround() && !this.abilities.flying) {
	// 		// if velocity projected onto acceleration is over the speed cap, don't allow the acceleration to occur
	// 		double multiplier = 1;
	// 		double inputSpeed = d > 1 ? 1 : Math.sqrt(d);
	// 		if (inputSpeed >= 1.0E-7D) {
	// 			// the proposed change in velocity and the magnitude of the acceleration vector
	// 			double am = Math.sqrt(a.x * a.x + a.z * a.z);
	// 			// ax and az are normalized components of horizontal acceleration
	// 			double ax = a.x / am;
	// 			double az = a.z / am;
				
	// 			if (inputSpeed > 1)
	// 				inputSpeed = 1;
				
	// 			Vec3d velocity = this.getVelocity();
	// 			double vx = velocity.x;
	// 			double vz = velocity.z;
	// 			// future v components
	// 			double fvx = vx + ax * am;
	// 			double fvz = vz + az * am;
				
	// 			// project current and future velocity onto input acceleration and limit movement to the limiter capper thing
	// 			double proj = vx * ax + vz * az;
	// 			double fproj = fvx * ax + fvz * az;
	// 			double limit = srcmov.doubles.get("source control cap");
				
	// 			if (proj > limit) {
	// 				// cap already exceeded, prevent further acceleration
	// 				multiplier = 0;
	// 			} else if (fproj > limit) {
	// 				// cap met, cut off acceleration at the cap point
	// 				multiplier = (limit - proj) / (fproj - proj);
	// 			}
				
	// 			// if limit not reached, allow to proceed as normal
	// 		}
			
	// 		a = a.multiply(multiplier);
	// 	}
		
	// 	this.setVelocity(this.getVelocity().add(a));
	// }
	


