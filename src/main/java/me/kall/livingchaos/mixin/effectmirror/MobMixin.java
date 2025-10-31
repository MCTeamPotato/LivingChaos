package me.kall.livingchaos.mixin.effectmirror;

import me.kall.livingchaos.tag.LivingTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Shadow @Nullable private LivingEntity target;

    @Inject(method = "setTarget", at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/event/entity/living/LivingChangeTargetEvent;getNewTarget()Lnet/minecraft/world/entity/LivingEntity;", shift = At.Shift.AFTER))
    private void onTargetChange(LivingEntity target, CallbackInfo ci) {
        if (this.target == null) return;
        Mob entity = (Mob) (Object) this;
        if (entity.getType().is(LivingTags.EFFECT_MIRROR)) this.target.getActiveEffects().forEach(entity::addEffect);
    }
}