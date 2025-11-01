package me.kall.livingchaos.mixin.deathlock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.kall.livingchaos.api.duck.Undamageable;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.network.NetworkManager;
import me.kall.livingchaos.network.packets.UndamageableUpdatePacket;
import me.kall.livingchaos.init.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Undamageable {
    @Unique private int chaos$isUndamageableTicks = -1;
    @Unique private boolean chaos$deathLocked;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void onDie(LivingEntity entity, float health, Operation<Void> original) {
        if (health <= 0.0F && entity.getType().is(ModTags.DEATH_LOCK) && !this.chaos$deathLocked()) {
            original.call(entity, 0.1F);
            this.chaos$setUndamageable(true);
        } else {
            original.call(entity, health);
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (this.chaos$deathLocked()) return;
        if (this.chaos$isUndamageable() && this.level() instanceof ServerLevel) {
            this.chaos$isUndamageableTicks--;
            if (!this.chaos$isUndamageable()) {
                this.chaos$setDeathLocked(true);
                NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UndamageableUpdatePacket(false, this.getId()));
            }
        }
    }

    @Override
    public boolean chaos$isUndamageable() {
        if (this.chaos$deathLocked()) return false;
        return this.chaos$isUndamageableTicks != -1;
    }

    @Override
    public void chaos$setUndamageable(boolean undamageable) {
        if (this.chaos$deathLocked()) return;
        this.chaos$isUndamageableTicks = undamageable ? 20 * ChaosConfig.DEATH_LOCK_SECONDS.get() : -1;
        if (this.chaos$isUndamageable() && this.level() instanceof ServerLevel) {
            NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new UndamageableUpdatePacket(true, this.getId()));
        }
    }

    @Override
    public boolean chaos$deathLocked() {
        return this.chaos$deathLocked;
    }

    @Override
    public void chaos$setDeathLocked(boolean deathLocked) {
        this.chaos$deathLocked = deathLocked;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("Undamageable", this.chaos$isUndamageable());
        compound.putBoolean("DeathLocked", this.chaos$deathLocked());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.chaos$setUndamageable(compound.getBoolean("Undamageable"));
        this.chaos$setDeathLocked(compound.getBoolean("DeathLocked"));
    }
}
