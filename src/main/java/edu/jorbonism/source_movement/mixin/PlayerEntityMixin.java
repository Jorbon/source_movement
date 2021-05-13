package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import edu.jorbonism.source_movement.Srcmov;
import edu.jorbonism.source_movement.SrcmovPC;
import edu.jorbonism.source_movement.VelocityAction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	private SrcmovPC srcmov = Srcmov.playerController;

	private boolean onGroundPrevious = true;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	public void modTravel(Vec3d movementInput, CallbackInfo ci) {
		if (!srcmov.enabled)
			return;

		FluidState fluidState = this.world.getFluidState(this.getBlockPos());
		if (this.isFallFlying() || ((this.isTouchingWater() || this.isInLava()) && this.method_29920() && !this.canWalkOnFluid(fluidState.getFluid())))
			return;
		
		double oldx = this.getX(), oldy = this.getY(), oldz = this.getZ();
		this.travelSrcmov(movementInput);
		this.increaseTravelMotionStats(this.getX() - oldx, this.getY() - oldy, this.getZ() - oldz);

		// lastly we will do all the actions from button presses in the last tick
		if (this.world.isClient)
			this.executeActionQueue();

		ci.cancel();
	}

	// this is so that actions still work if movement settings are disabled
	@Inject(method = "travel", at = @At("RETURN"))
	public void modTravelEnd(Vec3d movementInput, CallbackInfo ci) {
		// lastly we will do all the actions from button presses in the last tick
		if (this.world.isClient)
			this.executeActionQueue();
	}

	@Inject(method = "jump", at = @At("HEAD"), cancellable = true)
	public void modJump(CallbackInfo ci) {
		if (!srcmov.enabled)
			return;

		this.jumpSrcmov();
		this.incrementStat(Stats.JUMP);
		if (this.isSprinting())
			this.addExhaustion((float) srcmov.doubles.get("sprint jump exhaustion").doubleValue());
		else
			this.addExhaustion((float) srcmov.doubles.get("jump exhaustion").doubleValue());

		ci.cancel();
	}


	public void travelSrcmov(Vec3d movementInput) {
		if (this.isClimbing())
			this.climb();

		this.applyGravity();

		if (this.abilities.flying && !this.hasVehicle()) {
			this.fallDistance = 0;
			this.setFlag(7, false);
		}

		double blockSlipperiness = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness();
		
		this.applyFriction(blockSlipperiness);

		// apply movement last so the final value stored is the actual velocity its moving at
		this.applyInputAcceleration(blockSlipperiness, movementInput);

		this.onGroundPrevious = this.onGround;
		// here is where onGround is updated
		this.move(MovementType.SELF, this.getVelocity());


		{ // popping method_29242
			this.lastLimbDistance = this.limbDistance;
			double d = this.getX() - this.prevX;
			double e = this instanceof Flutterer ? this.getY() - this.prevY : 0;
			double f = this.getZ() - this.prevZ;
			float g = MathHelper.sqrt(d * d + e * e + f * f) * 4.0F;
			if (g > 1.0F) {
				g = 1.0F;
			}

			this.limbDistance += (g - this.limbDistance) * 0.4F;
			this.limbAngle += this.limbDistance;
		}
	}

	private void applyFriction(double blockSlipperiness) {
		double frictionFactor;
		if (this.onGround) {
			frictionFactor = srcmov.doubles.get("ground friction") * (1 - blockSlipperiness * 0.91);
			if (!this.onGroundPrevious)
				frictionFactor *= srcmov.doubles.get("jump friction multiplier");
		} else if (this.abilities.flying)
			frictionFactor = srcmov.doubles.get("horizontal fly friction");
		else
			frictionFactor = srcmov.doubles.get("horizontal air friction");

		Vec3d velocity = this.getVelocity();
		double x = velocity.x, y = velocity.y, z = velocity.z;

		// the signature ABH anti-bhop
		if (srcmov.booleans.get("enable abh") && this.onGround && !this.onGroundPrevious) {
			double horizontalSpeed2 = x * x + z * z;
			if (horizontalSpeed2 < 1E-8)
				return;
			
			double horizontalSpeed = MathHelper.sqrt(horizontalSpeed2);
			

			// adjust for analog input; this does't account for the apparent diagonal speed boost present in vanilla in all movement modes, but that doesn't really matter
			double inputSpeed = this.forwardSpeed * this.forwardSpeed + this.sidewaysSpeed * this.sidewaysSpeed;
			if (inputSpeed >= 1)
				inputSpeed = 1;
			else
				inputSpeed = MathHelper.sqrt(inputSpeed);

			
			double maxSpeed = speedTarget() * srcmov.doubles.get("abh threshold");

			if (horizontalSpeed > maxSpeed) {
				double speedAddition = maxSpeed - horizontalSpeed;

				// deduce the forward direction (erroneously)
				float yawRadians = this.yaw * 0.017453292F;
				Vec3d forward = new Vec3d(-MathHelper.sin(yawRadians), 0, MathHelper.cos(yawRadians));
				if (this.forwardSpeed < 0) // if s key, reverse forward direction
					forward = forward.multiply(-1);

				Vec3d change = forward.multiply(speedAddition);
				x += change.x;
				z += change.z;
			} else {
				// normal friction
				x *= 1 - frictionFactor;
				z *= 1 - frictionFactor;
			}
		} else {
			// normal friction
			x *= 1 - frictionFactor;
			z *= 1 - frictionFactor;
		}

		// apply vertical friction
		if (this.abilities.flying && !this.hasVehicle())
			y *= 1 - srcmov.doubles.get("vertical fly friction");
		else
			y *= 1 - srcmov.doubles.get("vertical air friction");
		
		this.setVelocity(x, y, z);
	}

	public void jumpSrcmov() {
		if (!srcmov.booleans.get("full speed jumps"))
			this.applyFriction(this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness());

		Vec3d velocity = this.getVelocity();
		double x = velocity.x, y = velocity.y, z = velocity.z;

		double jumpVelocity = this.getJumpVelocity();
		if (this.hasStatusEffect(StatusEffects.JUMP_BOOST))
			jumpVelocity += 0.1 * (this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1);

		double jumpPower = MathHelper.sqrt(5 * 2 * srcmov.doubles.get("gravity") * srcmov.doubles.get("jump height"));
		// I've included the y component and added another term to cancel out some constant downward momentum while grounded
		y += jumpVelocity * jumpPower + srcmov.doubles.get("gravity") * (1 - srcmov.doubles.get("vertical air friction"));


		boolean doJumpBoost = false;
		if (srcmov.booleans.get("directional jump boosting"))
			doJumpBoost = Math.abs(this.forwardSpeed) > 1E-4 || Math.abs(this.sidewaysSpeed) > 1E-4 && !this.isSneaking();
		else if (srcmov.booleans.get("orange box jump boosting"))
			doJumpBoost = Math.abs(this.forwardSpeed) > 1E-4 && !this.isSneaking();
		else
			doJumpBoost = this.forwardSpeed > 1E-4 && !this.isSneaking();

		if (doJumpBoost) {
			float yawRadians = this.yaw * 0.017453292F;
			if (srcmov.booleans.get("directional jump boosting"))
				yawRadians -= MathHelper.atan2(this.sidewaysSpeed, this.forwardSpeed);

			// get boost amount, negate if moving backwards w/ setting
			double boost = this.isSprinting() ? srcmov.doubles.get("sprint jump boost") : srcmov.doubles.get("walk jump boost") * (
				srcmov.booleans.get("orange box jump boosting") && this.forwardSpeed < 0 ? -1 : 1
			);
			x += -MathHelper.sin(yawRadians) * boost;
			z += MathHelper.cos(yawRadians) * boost;
		}
		
		this.setVelocity(x, y, z);

		this.velocityDirty = true;
	}

	private double getMovementSpeedSrcmov(double blockSlipperiness) {
		// this has to correct for frictional offsets since I moved things around. I tried moving them back to fix this but it didn't work so idk where this actually comes from. Also apparently ground movement is fine how it is.
		if (this.onGround)
			return srcmov.doubles.get("ground control") * (this.isSprinting() ? 1.3 : 1) * 0.216 / (blockSlipperiness * blockSlipperiness * blockSlipperiness);
		if (this.abilities.flying)
			return (1 - srcmov.doubles.get("horizontal fly friction")) * srcmov.doubles.get("fly speed") * (this.isSprinting() ? 2 : 1);
		return (1 - srcmov.doubles.get("horizontal fly friction")) * srcmov.doubles.get("air control") * (this.isSprinting() ? 1.3 : 1);
	}

	private void applyInputAcceleration(double blockSlipperiness, Vec3d movementInput) {
		double speed = this.getMovementSpeedSrcmov(blockSlipperiness);

		double d = movementInput.lengthSquared();
    	if (d < 1E-8)
        	return;
    	
        Vec3d inputAcc = (d > 1 ? movementInput.normalize() : movementInput).multiply(speed);
        double f = MathHelper.sin(this.yaw * 0.017453292F);
        double g = MathHelper.cos(this.yaw * 0.017453292F);
        Vec3d a = new Vec3d(inputAcc.x * g - inputAcc.z * f, inputAcc.y, inputAcc.z * g + inputAcc.x * f);


		// implement the soft control cap at 300su by default
		if (srcmov.booleans.get("use source fling detection") && !this.abilities.flying) {
			Vec3d v = this.getVelocity();
			double vlen2 = v.x * v.x + v.z * v.z;
			double flingSpeed = srcmov.doubles.get("min fling speed");
			double ax = a.x, az = a.z;
			if (vlen2 >= flingSpeed * flingSpeed) {
				// if a velocity on an axis exceeds 150ups, don't allow deceleration in that axis
				if ((v.x > flingSpeed * 0.5 && ax < 0) || (v.x < -flingSpeed * 0.5 && ax > 0)) ax = 0;
				if ((v.z > flingSpeed * 0.5 && az < 0) || (v.z < -flingSpeed * 0.5 && az > 0)) az = 0;

				a = new Vec3d(ax, a.y, az);
			}
		}
		
		// main air strafe code
		if (srcmov.booleans.get("use source control cap") && !this.onGround && !this.abilities.flying) {
			// if velocity projected onto acceleration is over the speed cap, don't allow the acceleration to occur
			double multiplier = 1;
			double inputSpeed = d > 1 ? 1 : MathHelper.sqrt(d);
			if (inputSpeed >= 1.0E-7D) {
				// the proposed change in velocity and the magnitude of the acceleration vector
				double am = MathHelper.sqrt(a.x * a.x + a.z * a.z);
				// ax and az are normalized components of horizontal acceleration
				double ax = a.x / am;
				double az = a.z / am;

				if (inputSpeed > 1)
					inputSpeed = 1;

				Vec3d velocity = this.getVelocity();
				double vx = velocity.x;
				double vz = velocity.z;
				// future v components
				double fvx = vx + ax * am;
				double fvz = vz + az * am;

				// project current and future velocity onto input acceleration and limit movement to the limiter capper thing
				double proj = vx * ax + vz * az;
				double fproj = fvx * ax + fvz * az;
				double limit = srcmov.doubles.get("source control cap");

				if (proj > limit) {
					// cap already exceeded, prevent further acceleration
					multiplier = 0;
				} else if (fproj > limit) {
					// cap met, cut off acceleration at the cap point
					multiplier = (limit - proj) / (fproj - proj);
				}

				// if limit not reached, allow to proceed as normal
			}

			a = a.multiply(multiplier);
		}

		this.setVelocity(this.getVelocity().add(a));
	}

	private void applyGravity() {
		if (this.abilities.flying)
			return;

		double gravity = srcmov.doubles.get("gravity");
		if (this.getVelocity().y <= 0 && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
			gravity = 0.01;
			this.fallDistance = 0;
		}
		
		Vec3d velocity = this.getVelocity();
		double y = velocity.y;
		if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
			y += (srcmov.doubles.get("levitation strength") * (double) (this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - y) * 0.2;
			this.fallDistance = 0;
		} else if (this.world.isClient && !this.world.isChunkLoaded(this.getVelocityAffectingPos())) {
			if (this.getY() > 0)
				y = -0.1;
			else
				y = 0;
		} else if (!this.hasNoGravity())
			y -= gravity;
		
		this.setVelocity(velocity.x, y, velocity.z);
	}

	private void climb() {
		Vec3d velocity = this.getVelocity();

		this.fallDistance = 0.0F;
		double cap = srcmov.doubles.get("climb movement cap");
		double x = MathHelper.clamp(velocity.x, -cap, cap);
		double z = MathHelper.clamp(velocity.z, -cap, cap);
		double y = Math.max(velocity.y, -cap);
		if (y < 0 && !this.getBlockState().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder())
			y = 0;

		if (this.horizontalCollision || this.jumping)
			y = srcmov.doubles.get("climb speed");
		
		this.setVelocity(x, y, z);
	}

	private void executeActionQueue() {
		VelocityAction actionSet = new VelocityAction(Srcmov.actionQueue);
		this.setVelocity(actionSet.execute(this.getVelocity(), new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed), this.yaw * 0.017453292F, this.pitch * 0.017453292F));
		Srcmov.actionQueue.clear();
	}

	private double speedTarget() {
		if (this.isSprinting())
			return Srcmov.sprintSpeedTarget;
		else if (this.isSneaking())
			return Srcmov.sneakSpeedTarget;
		return Srcmov.walkSpeedTarget;
	}

	@Shadow private PlayerAbilities abilities;
	@Shadow private void incrementStat(Identifier jump) {}
	@Shadow private void addExhaustion(float f) {}
	@Shadow private void increaseTravelMotionStats(double d, double e, double f) {}

}


