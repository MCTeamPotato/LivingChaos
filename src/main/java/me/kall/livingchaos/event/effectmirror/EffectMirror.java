package me.kall.livingchaos.event.effectmirror;

import me.kall.livingchaos.init.ModTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;

public class EffectMirror {
    public static void onLivingDamage(@NotNull LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity sourceLiving) {
            if (source.getType().is(ModTags.EFFECT_MIRROR)) entity.getActiveEffects().forEach(sourceLiving::addEffect);
            if (entity.getType().is(ModTags.EFFECT_MIRROR)) sourceLiving.getActiveEffects().forEach(entity::addEffect);
        }
    }
}
