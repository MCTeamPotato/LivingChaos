package me.kall.livingchaos.mixin.multishoot;

import me.kall.livingchaos.api.ext.IProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.Projectile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class ProjectileMixin implements IProjectile {
    @Unique
    private boolean chaos$isSource = true;

    @Override
    public boolean chaos$isSource() {
        return this.chaos$isSource;
    }

    @Override
    public void chaos$setSource(boolean isSource) {
        this.chaos$isSource = isSource;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.chaos$isSource = compound.getBoolean("IsSourceShoot");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("IsSourceShoot", this.chaos$isSource());
    }
}
