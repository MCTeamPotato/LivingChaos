package me.kall.livingchaos.mixin.deadrattle;

import me.kall.livingchaos.api.ext.Exploder;
import me.kall.livingchaos.api.ext.ParentBear;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Exploder, ParentBear {
    @Unique private boolean chaos$isExploder, chaos$isParent;
    @Unique private int chaos$explodeRadius = 4, chaos$deliverRadius = 0, chaos$deliveryCount = 0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.chaos$setExploder(compound.getBoolean("IsExploder"));
        this.chaos$setExplodeRadius(compound.getInt("ExplodeRadius"));
        this.chaos$setDeliverRadius(compound.getInt("ExploderDeliveryRadius"));
        this.chaos$setDeliveryCount(compound.getInt("ExploderDeliveryCount"));
        this.chaos$setParent(compound.getBoolean("IsParent"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("IsExploder", this.chaos$isExploder());
        compound.putInt("ExplodeRadius", this.chaos$explodeRadius());
        compound.putInt("ExploderDeliveryRadius", this.chaos$deliverRadius());
        compound.putInt("ExploderDeliveryCount", this.chaos$deliveryCount());
        compound.putBoolean("IsParent", this.chaos$isParent());
    }

    @Override
    public boolean chaos$isExploder() {
        return this.chaos$isExploder;
    }

    @Override
    public void chaos$setExploder(boolean exploder) {
        this.chaos$isExploder = exploder;
    }

    @Override
    public int chaos$explodeRadius() {
        return this.chaos$explodeRadius;
    }

    @Override
    public void chaos$setExplodeRadius(int explodeRadius) {
        this.chaos$explodeRadius = explodeRadius;
    }

    @Override
    public int chaos$deliverRadius() {
        return this.chaos$deliverRadius;
    }

    @Override
    public void chaos$setDeliverRadius(int deliverRadius) {
        this.chaos$deliverRadius = deliverRadius;
    }

    @Override
    public int chaos$deliveryCount() {
        return this.chaos$deliveryCount;
    }

    @Override
    public void chaos$setDeliveryCount(int deliveryCount) {
        this.chaos$deliveryCount = deliveryCount;
    }

    @Override
    public boolean chaos$isParent() {
        return this.chaos$isParent;
    }

    @Override
    public void chaos$setParent(boolean isParent) {
        this.chaos$isParent = isParent;
    }
}
