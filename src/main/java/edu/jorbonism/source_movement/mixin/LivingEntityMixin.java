package edu.jorbonism.source_movement.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import edu.jorbonism.source_movement.Srcmov;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
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
	
	
	@Inject(method = "travelMidAir", at = @At("HEAD"), cancellable = true)
	private void travelMidAirMixin(Vec3d movementInput, CallbackInfo ci) {
		if (!Srcmov.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		BlockPos blockPos = this.getVelocityAffectingPos();
		float f = this.isOnGround() ? this.getWorld().getBlockState(blockPos).getBlock().getSlipperiness() : 1.0F;
		float g = f * 0.91F;
		Vec3d vec3d = this.applyMovementInput(movementInput, f);
		double d = vec3d.y;
		StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.LEVITATION);
		if (statusEffectInstance != null) {
			d += (0.05 * (statusEffectInstance.getAmplifier() + 1) - vec3d.y) * 0.2;
		} else if (!this.getWorld().isClient || this.getWorld().isChunkLoaded(blockPos)) {
			d -= this.getEffectiveGravity();
		} else if (this.getY() > this.getWorld().getBottomY()) {
			d = -0.1;
		} else {
			d = 0.0;
		}

		if (this.hasNoDrag()) {
			this.setVelocity(vec3d.x, d, vec3d.z);
		} else {
			float h = this instanceof Flutterer ? g : 0.98F;
			this.setVelocity(vec3d.x * g, d * h, vec3d.z * g);
		}
		
		ci.cancel();
	}
	
	
	@Inject(method = "travelInFluid", at = @At("HEAD"), cancellable = true)
	private void travelInFluidMixin(Vec3d movementInput, CallbackInfo ci) {
		if (!Srcmov.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		boolean bl = this.getVelocity().y <= 0.0;
		double d = this.getY();
		double e = this.getEffectiveGravity();
		if (this.isTouchingWater()) {
			float f = this.isSprinting() ? 0.9F : this.getBaseWaterMovementSpeedMultiplier();
			float g = 0.02F;
			float h = (float)this.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
			if (!this.isOnGround()) {
				h *= 0.5F;
			}

			if (h > 0.0F) {
				f += (0.54600006F - f) * h;
				g += (this.getMovementSpeed() - g) * h;
			}

			if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
				f = 0.96F;
			}

			this.updateVelocity(g, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			Vec3d vec3d = this.getVelocity();
			if (this.horizontalCollision && this.isClimbing()) {
				vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
			}

			vec3d = vec3d.multiply(f, 0.8F, f);
			this.setVelocity(this.applyFluidMovingSpeed(e, bl, vec3d));
		} else {
			this.updateVelocity(0.02F, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
				this.setVelocity(this.getVelocity().multiply(0.5, 0.8F, 0.5));
				Vec3d vec3d2 = this.applyFluidMovingSpeed(e, bl, this.getVelocity());
				this.setVelocity(vec3d2);
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5));
			}

			if (e != 0.0) {
				this.setVelocity(this.getVelocity().add(0.0, -e / 4.0, 0.0));
			}
		}

		Vec3d vec3d2 = this.getVelocity();
		if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - this.getY() + d, vec3d2.z)) {
			this.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
		}
		
		ci.cancel();
	}
	
	
	@Inject(method = "calcGlidingVelocity", at = @At("HEAD"), cancellable = true)
	private void calcGlidingVelocityMixin(Vec3d oldVelocity, CallbackInfoReturnable<Vec3d> cir) {
		if (!Srcmov.enabled) return;
		if (!(((Entity) this) instanceof PlayerEntity)) return;
		
		Vec3d vec3d = this.getRotationVector();
		float f = this.getPitch() * (float) (Math.PI / 180.0);
		double d = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
		double e = oldVelocity.horizontalLength();
		double g = this.getEffectiveGravity();
		double h = MathHelper.square(Math.cos(f));
		oldVelocity = oldVelocity.add(0.0, g * (-1.0 + h * 0.75), 0.0);
		if (oldVelocity.y < 0.0 && d > 0.0) {
			double i = oldVelocity.y * -0.1 * h;
			oldVelocity = oldVelocity.add(vec3d.x * i / d, i, vec3d.z * i / d);
		}

		if (f < 0.0F && d > 0.0) {
			double i = e * -MathHelper.sin(f) * 0.04;
			oldVelocity = oldVelocity.add(-vec3d.x * i / d, i * 3.2, -vec3d.z * i / d);
		}

		if (d > 0.0) {
			oldVelocity = oldVelocity.add((vec3d.x / d * e - oldVelocity.x) * 0.1, 0.0, (vec3d.z / d * e - oldVelocity.z) * 0.1);
		}
		
		cir.setReturnValue(oldVelocity.multiply(0.99F, 0.98F, 0.99F));
		cir.cancel();
	}
	
	
	@Shadow private Vec3d applyMovementInput(Vec3d movementInput, float slipperiness) { return Vec3d.ZERO; }
	@Shadow public StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect) { return null; }
	@Shadow protected double getEffectiveGravity() { return 0.0; }
	@Shadow public boolean hasNoDrag() { return false; }
	@Shadow protected float getBaseWaterMovementSpeedMultiplier() { return 0.8f; }
	@Shadow public double getAttributeValue(RegistryEntry<EntityAttribute> attribute) { return 0.0; }
	@Shadow private float getMovementSpeed() { return 0.0f; }
	@Shadow public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect) { return false; }
	@Shadow public boolean isClimbing() { return false; }
	@Shadow public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) { return Vec3d.ZERO; }
	
}
