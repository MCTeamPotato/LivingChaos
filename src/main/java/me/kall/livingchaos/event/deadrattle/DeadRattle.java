package me.kall.livingchaos.event.deadrattle;

import com.google.common.collect.Lists;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.event.deadrattle.types.*;
import me.kall.livingchaos.init.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DeadRattle {
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> EXPLOSION = () -> Explosive::new;
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> STRENGTH = () -> Strength::new;
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> WEAPON = () -> (level, living) -> Weapon.hasWeapon(living) ? new Weapon(level, living) : null;
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> RUBBISH = () -> Rubbish::new;
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> PARENT = () -> (level, living) -> living.isBaby() ? new Parent(level, living) : null;

    private static final List<Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>>> AVAILABLE_TASKS = Lists.newArrayList(EXPLOSION, STRENGTH, WEAPON, RUBBISH, PARENT);

    public static void onLivingDeath(@NotNull LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (!entity.getType().is(ModTags.DEAD_RATTLE) || !(entity.level() instanceof ServerLevel level)) return;

        Runnable chosen = null;
        while (chosen == null) {
            chosen = AVAILABLE_TASKS.get(ThreadLocalRandom.current().nextInt(AVAILABLE_TASKS.size())).get().apply(level, entity);
        }

        try {
            chosen.run();
        } catch (Exception exception) {
            LivingChaos.LOGGER.error("Error executing DeadRattle task", exception);
        }
    }

    public static void updateEffect(@NotNull LivingEntity living, MobEffect @NotNull ... effects) {
        for (MobEffect effect : effects) {
            MobEffectInstance effectInstance = living.getEffect(effect);
            living.addEffect(new MobEffectInstance(effect, 400, effectInstance == null ? 0 : effectInstance.getAmplifier() + 1));
        }
    }
}
