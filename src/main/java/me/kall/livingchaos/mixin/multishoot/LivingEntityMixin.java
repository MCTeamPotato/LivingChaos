package me.kall.livingchaos.mixin.multishoot;

import me.kall.livingchaos.api.Shooter;
import me.kall.livingchaos.config.ChaosConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Shooter {
    @Unique private int chaos$canGatlingCooldown = -1;

    @Override
    public boolean chaos$canGatling() {
        return this.chaos$canGatlingCooldown == -1;
    }

    @Override
    public void chaos$setCanGatling(boolean canGatling) {
        this.chaos$canGatlingCooldown = canGatling ? -1 : ChaosConfig.MULTI_SHOOT_COOL_DOWN * 20;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;detectEquipmentUpdates()V", shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (!this.chaos$canGatling()) {
            this.chaos$canGatlingCooldown--;
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.chaos$setCanGatling(compound.getBoolean("CanGatling"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("CanGatling", this.chaos$canGatling());
    }
}
