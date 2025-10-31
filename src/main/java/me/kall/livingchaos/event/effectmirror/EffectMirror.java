package me.kall.livingchaos.event.effectmirror;

import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.tag.LivingTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = LivingChaos.MOD_ID)
public class EffectMirror {
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity sourceLiving) {
            if (source.getType().is(LivingTags.EFFECT_MIRROR)) entity.getActiveEffects().forEach(sourceLiving::addEffect);
            if (entity.getType().is(LivingTags.EFFECT_MIRROR)) sourceLiving.getActiveEffects().forEach(entity::addEffect);
        }
    }
}
