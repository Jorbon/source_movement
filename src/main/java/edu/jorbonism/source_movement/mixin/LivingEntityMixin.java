package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import edu.jorbonism.source_movement.ConfigState.BooleanSetting;
import edu.jorbonism.source_movement.ConfigState.DoubleSetting;
import edu.jorbonism.source_movement.ClientMain;
import net.minecraft.block.Blocks;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
	
	protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }
	
	public boolean was_on_ground = false;
	
	
	@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isGliding()Z", ordinal = 0), require = 1)
	public void before_travel(CallbackInfo ci) {
		if (!ClientMain.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		this.forwardSpeed /= 0.98f;
		this.sidewaysSpeed /= 0.98f;
		
		if (this.isSneaking()) {
			this.forwardSpeed /= 0.3f;
			this.sidewaysSpeed /= 0.3f;
		}
		
		double diagonal_boost = 0.0;
		if (this.isSneaking()) diagonal_boost = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSneak);
		else if (this.isSprinting()) diagonal_boost = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusSprint);
		else diagonal_boost = ClientMain.config_state.get_double(DoubleSetting.DiagonalBonusWalk);
		
		float throttle = (float) (1.0 / (1.0 + (Math.sqrt(2) - 1.0) * diagonal_boost));
		this.forwardSpeed *= throttle;
		this.sidewaysSpeed *= throttle;
	}
	
	
	@SuppressWarnings("deprecation")
	@Inject(method = "travelMidAir", at = @At("HEAD"), cancellable = true)
	private void travel_mid_air_mixin(Vec3d movement_input, CallbackInfo ci) {
		// if (((Entity) this) instanceof PlayerEntity) System.out.println(this.getVelocity().horizontalLength());
		
		if (!ClientMain.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		BlockPos block_pos = this.getVelocityAffectingPos();
		double slipperiness = this.isOnGround() ? this.getEntityWorld().getBlockState(block_pos).getBlock().getSlipperiness() : 1.0;
		
		// Get movement speed
		double movement_speed;
		if (this.isOnGround()) 
			movement_speed = this.getMovementSpeed() / (slipperiness * slipperiness * slipperiness) * ClientMain.config_state.get_double(DoubleSetting.Traction);
		else movement_speed = this.getOffGroundSpeed();
		
		// Apply movement input
		this.updateVelocity((float) movement_speed, movement_input);
		
		if (this.isClimbing()) { // Apply climbing speed
			this.onLanding();
			
			Vec3d velocity = this.getVelocity();
			double max_horizontal_speed = ClientMain.config_state.get_double(DoubleSetting.ClimbMaxHorizontalSpeed);
			double vx = MathHelper.clamp(velocity.x, -max_horizontal_speed, max_horizontal_speed);
			double vz = MathHelper.clamp(velocity.z, -max_horizontal_speed, max_horizontal_speed);
			
			double vy_min = -ClientMain.config_state.get_double(DoubleSetting.ClimbMaxFallingSpeed);
			if (this.isHoldingOntoLadder() && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING)) {
				vy_min = 0.0;
			}
			double vy = Math.max(velocity.y, vy_min);
			
			this.setVelocity(new Vec3d(vx, vy, vz));
		}
		this.move(MovementType.SELF, this.getVelocity());
		
		
		Vec3d velocity = this.getVelocity();
		double vy = velocity.y;
		
		// climbing
		if ((this.horizontalCollision || this.jumping) && (this.isClimbing() || (this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this)))) {
			vy = ClientMain.config_state.get_double(DoubleSetting.ClimbSpeed);
		}
		
		// gravity
		StatusEffectInstance levitation = this.getStatusEffect(StatusEffects.LEVITATION);
		if (levitation != null) {
			vy += (0.05 * (levitation.getAmplifier() + 1) - velocity.y) * 0.2;
		} else if (!this.getEntityWorld().isClient() || this.getEntityWorld().isChunkLoaded(block_pos)) {
			vy -= this.getEffectiveGravity();
		} else if (this.getY() > this.getEntityWorld().getBottomY()) {
			vy = -0.1;
		} else {
			vy = 0.0;
		}
		
		
		
		double vx = velocity.x;
		double vz = velocity.z;
		
		double friction = Math.clamp((1.0 - slipperiness) * ClientMain.config_state.get_double(DoubleSetting.Friction), 0.0, 1.0);
		
		
		if (this.isOnGround() && !this.was_on_ground) {
			friction *= ClientMain.config_state.get_double(DoubleSetting.HopFriction);
			vx *= 1.0 - friction;
			vz *= 1.0 - friction;
			
			if (ClientMain.config_state.get_boolean(BooleanSetting.Abh)) {
				double horizontal_speed = Math.sqrt(vx * vx + vz * vz);
				if (horizontal_speed > 1e-4) {
					// double input_magnitude = Math.min(movement_input.horizontalLength(), 1.0);
					double max_speed = ClientMain.config_state.get_double(DoubleSetting.AbhLevelWalk);
					if (horizontal_speed > max_speed) {
						double impulse = max_speed - horizontal_speed;
						float yaw_radians = this.getYaw() * (float) Math.PI / 180.0f;
						if (movement_input.z < 0.0) yaw_radians += Math.PI;
						vx -= impulse * MathHelper.sin(yaw_radians);
						vz += impulse * MathHelper.cos(yaw_radians);
					}
					
				}
			}
			
		} else {
			vx *= 1.0 - friction;
			vz *= 1.0 - friction;
		}
		
		this.was_on_ground = this.isOnGround();
		
		
		
		vx *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.HorizontalDrag);
		vz *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.HorizontalDrag);
		
		vy *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.VerticalDrag);
		
		this.setVelocity(vx, vy, vz);
		
		ci.cancel();
	}
	
	
	
	
	@Inject(method = "travelInFluid", at = @At("HEAD"), cancellable = true)
	private void travel_in_fluid_mixin(Vec3d movementInput, CallbackInfo ci) {
		if (!ClientMain.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		boolean falling = this.getVelocity().y <= 0.0;
		double y = this.getY();
		double gravity = this.getEffectiveGravity();
		if (this.isTouchingWater()) {
			float f = this.isSprinting() ? 0.9F : this.getBaseWaterMovementSpeedMultiplier();
			float g = 0.02F;
			float h = (float) this.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
			if (!this.isOnGround()) {
				h *= 0.5F;
			}
			
			if (h > 0.0F) {
				f += (0.546F - f) * h;
				g += (this.getMovementSpeed() - g) * h;
			}
			
			if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
				f = 0.96F;
			}
			
			this.updateVelocity(g, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			Vec3d velocity = this.getVelocity();
			if (this.horizontalCollision && this.isClimbing()) {
				velocity = new Vec3d(velocity.x, 0.2, velocity.z);
			}
			
			velocity = velocity.multiply(f, 0.8F, f);
			this.setVelocity(this.applyFluidMovingSpeed(gravity, falling, velocity));
		} else {
			this.updateVelocity(0.02F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
				this.setVelocity(this.getVelocity().multiply(0.5, 0.8F, 0.5));
				Vec3d vec3d2 = this.applyFluidMovingSpeed(gravity, falling, this.getVelocity());
				this.setVelocity(vec3d2);
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5));
			}
			
			if (gravity != 0.0) {
				this.setVelocity(this.getVelocity().add(0.0, -gravity / 4.0, 0.0));
			}
		}
		
		Vec3d velocity = this.getVelocity();
		if (this.horizontalCollision && this.doesNotCollide(velocity.x, velocity.y + 0.6F - this.getY() + y, velocity.z)) {
			this.setVelocity(velocity.x, 0.3F, velocity.z);
		}
		
		ci.cancel();
	}
	
	
	@Inject(method = "calcGlidingVelocity", at = @At("HEAD"), cancellable = true)
	private void calc_gliding_velocity_mixin(Vec3d velocity, CallbackInfoReturnable<Vec3d> cir) {
		if (!ClientMain.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		double vx = velocity.x;
		double vy = velocity.y;
		double vz = velocity.z;
		
		Vec3d facing = this.getRotationVector();
		double facing_horizontal_length_squared = facing.x * facing.x + facing.z * facing.z;
		double facing_horizontal_length = Math.sqrt(facing_horizontal_length_squared);
		double velocity_horizontal_length = Math.sqrt(vx*vx + vz*vz);
		
		// gravity
		double gravity = this.getEffectiveGravity() * ClientMain.config_state.get_double(DoubleSetting.ElytraGravityMultiplier);
		vy -= gravity;
		vy += gravity * facing_horizontal_length_squared * (1.0 - ClientMain.config_state.get_double(DoubleSetting.ElytraGravityModifier));
		
		if (facing_horizontal_length > 0.0) {
			double horizontal_force = 0.0;
			
			// downward velocity -> outward force
			double outward_lift = Math.max(-vy, 0.0) * facing_horizontal_length_squared;
			vy += outward_lift * ClientMain.config_state.get_double(DoubleSetting.ElytraOutwardLift);
			horizontal_force += outward_lift * ClientMain.config_state.get_double(DoubleSetting.ElytraOutwardForce);
			
			// outward velocity -> upward force
			double upward_lift = Math.max(facing.y, 0.0) * velocity_horizontal_length;
			vy += upward_lift * ClientMain.config_state.get_double(DoubleSetting.ElytraUpwardLift);
			horizontal_force -= upward_lift * ClientMain.config_state.get_double(DoubleSetting.ElytraUpwardDrag);;
			
			vx += horizontal_force * facing.x / facing_horizontal_length;
			vz += horizontal_force * facing.z / facing_horizontal_length;
			
			// change horizontal velocity towards facing direction
			double redirection = ClientMain.config_state.get_double(DoubleSetting.ElytraRedirection);
			vx += redirection * (-vx + velocity_horizontal_length * facing.x / facing_horizontal_length);
			vz += redirection * (-vz + velocity_horizontal_length * facing.z / facing_horizontal_length);
		}
		
		vx *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.ElytraHorizontalDrag);
		vz *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.ElytraHorizontalDrag);
		vy *= 1.0 - ClientMain.config_state.get_double(DoubleSetting.ElytraVerticalDrag);
		
		cir.setReturnValue(new Vec3d(vx, vy, vz));
		cir.cancel();
	}
	
	
	@Shadow public float forwardSpeed;
	@Shadow public float sidewaysSpeed;
	@Shadow protected boolean jumping;
	@Shadow public abstract boolean isHoldingOntoLadder();
	@Shadow public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> levitation);
	@Shadow protected abstract float getBaseWaterMovementSpeedMultiplier();
	@Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> waterMovementEfficiency);
	@Shadow public abstract float getMovementSpeed();
	@Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> dolphinsGrace);
	@Shadow public abstract boolean isClimbing();
	@Shadow public abstract Vec3d applyFluidMovingSpeed(double e, boolean bl, Vec3d vec3d);
	@Shadow protected abstract double getEffectiveGravity();
	@Shadow protected abstract float getOffGroundSpeed();
	
	
}
