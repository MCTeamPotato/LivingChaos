package me.kall.livingchaos.mixin.deadrattle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.kall.livingchaos.api.duck.Exploder;
import me.kall.livingchaos.api.duck.IExplosion;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.event.deadrattle.DeadRattle;
import me.kall.livingchaos.event.deadrattle.types.Explosive;
import me.kall.livingchaos.init.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Explosion.class)
public class ExplosionMixin implements IExplosion {
    @Shadow @Final @Nullable private Entity source;
    @Unique private boolean chaos$delivered;

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean onHurtEntity(Entity entity, DamageSource damageSource, float amount, Operation<Boolean> original) {
        if (this.source instanceof Exploder exploder && exploder.chaos$isExploder() && entity instanceof LivingEntity living) {
            DeadRattle.updateEffect(living, ModEffects.RESISTANCE_REDUCTION.get());
            if (!this.chaos$delivered() && exploder.chaos$deliveryCount() <= ChaosConfig.EXPLOSIVE_DELIVERY_LIMIT.get() && exploder instanceof LivingEntity dead && dead.level() instanceof ServerLevel level) {
                Explosive.delivery(level, dead, exploder);
                this.chaos$setDelivered(true);
            }
        }
        return original.call(entity, damageSource, amount);
    }

    @Override
    public boolean chaos$delivered() {
        return this.chaos$delivered;
    }

    @Override
    public void chaos$setDelivered(boolean delivered) {
        this.chaos$delivered = delivered;
    }
}
