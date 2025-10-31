package me.kall.livingchaos.event.deathlock;

import me.kall.livingchaos.api.Undamageable;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.network.NetworkManager;
import me.kall.livingchaos.network.packets.ParticlePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class DeathLock {
    public static void lockDeath(@NotNull LivingEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Undamageable undamageable && undamageable.chaos$isUndamageable()) {
            event.setCanceled(true);
            if (entity.level() instanceof ServerLevel level) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, entity.getSoundSource(), 2.0F, entity.getRandom().nextFloat() * 0.1F + 0.9F);
                NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new ParticlePacket(entity.getId(), ParticlePacket.ParticlesConstant.DEATH_LOCK));
            }
        }
    }

    public static void register(IEventBus bus) {
        if (ChaosConfig.DESPERATE) {
            bus.addListener((LivingAttackEvent event) -> lockDeath(event));
        } else {
            bus.addListener((LivingDamageEvent event) -> lockDeath(event));
        }
    }
}
