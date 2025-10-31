package me.kall.livingchaos.mixin.deadrattle;

import me.kall.livingchaos.api.Exploder;
import me.kall.livingchaos.tag.LivingTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Exploder {
    @Unique private boolean chaos$isExploder;
    @Unique private int chaos$explodeRadius = 4, chaos$deliverRadius = 0, chaos$deliveryCount = 0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean chaos$isExploder() {
        return this.chaos$isExploder || this.getType().is(LivingTags.DEAD_RATTLE);
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
}
