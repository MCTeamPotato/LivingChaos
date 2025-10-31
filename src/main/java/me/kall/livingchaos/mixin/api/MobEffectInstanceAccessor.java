package me.kall.livingchaos.mixin.api;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {
    @Accessor("amplifier")
    void setAmplifier(int amplifier);

    @Accessor("duration")
    void setDuration(int duration);
}
