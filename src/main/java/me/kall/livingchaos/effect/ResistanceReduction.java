package me.kall.livingchaos.effect;

import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = LivingChaos.MOD_ID)
public class ResistanceReduction extends MobEffect {
    public ResistanceReduction() {
        super(MobEffectCategory.HARMFUL, 91919191);
    }

    @Override public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {}
    @Override public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity livingEntity, int amplifier, double health) {}

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity().hasEffect(ModEffects.RESISTANCE_REDUCTION.get())) {
            event.setAmount(event.getAmount() * 1.2F);
        }
    }
}
