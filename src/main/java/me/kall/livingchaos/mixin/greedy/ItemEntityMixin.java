package me.kall.livingchaos.mixin.greedy;

import me.kall.livingchaos.api.ext.Unpickable;
import me.kall.livingchaos.event.greedy.Greedy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Unpickable {
    @Unique private boolean chaos$isUnpickable;
    @Unique private int chaos$ownerEntity = -1;

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean chaos$isUnpickable() {
        return this.chaos$isUnpickable;
    }

    @Override
    public void chaos$setUnpickable(boolean unpickable) {
        this.chaos$isUnpickable = unpickable;
    }

    @Override
    public int chaos$ownerEntity() {
        return this.chaos$ownerEntity;
    }

    @Override
    public void chaos$setOwnerEntity(int ownerEntity) {
        this.chaos$ownerEntity = ownerEntity;
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z"))
    private void onTick(CallbackInfo ci) {
        Greedy.validateOwner((ItemEntity) (Object)this, this);
        this.setGlowingTag(true);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void write(@NotNull CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("IsUnpickable", this.chaos$isUnpickable());
        compound.putInt("OwnerEntity", this.chaos$ownerEntity());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(@NotNull CompoundTag compound, CallbackInfo ci) {
        this.chaos$setUnpickable(compound.getBoolean("IsUnpickable"));
        this.chaos$setOwnerEntity(compound.getInt("OwnerEntity"));
    }
}
