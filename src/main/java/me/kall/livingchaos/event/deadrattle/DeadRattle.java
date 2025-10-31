package me.kall.livingchaos.event.deadrattle;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntIterator;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.api.Exploder;
import me.kall.livingchaos.api.IExplosion;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.data.EntityTracker;
import me.kall.livingchaos.tag.LivingTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = LivingChaos.MOD_ID)
public class DeadRattle {
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> EXPLOSION = () -> (level, dead) -> ((Exploder) dead).chaos$isExploder() ? new Explosion(level, dead) : null;
    private static final Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>> STRENGTH = () -> Strength::new;

    private static final List<Supplier<BiFunction<ServerLevel, LivingEntity, Runnable>>> AVAILABLE_TASKS = Lists.newArrayList(EXPLOSION, STRENGTH);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (!entity.getType().is(LivingTags.DEAD_RATTLE) || !(entity.level() instanceof ServerLevel level)) return;

        Runnable chosen = null;
        while (chosen == null) {
            chosen = AVAILABLE_TASKS.get(ThreadLocalRandom.current().nextInt(AVAILABLE_TASKS.size())).get().apply(level, entity);
        }

        chosen.run();
    }

    public record Explosion(ServerLevel level, LivingEntity dead) implements Runnable {
        @Override
        public void run() {
            this.level.explode(this.dead, this.dead.getX(), this.dead.getY(), this.dead.getZ(), ((Exploder) this.dead).chaos$explodeRadius(), false, Level.ExplosionInteraction.MOB);
        }

        public static void delivery(@NotNull ServerLevel level, @NotNull LivingEntity dead, @NotNull Exploder exploder, IExplosion explosion) {
            final ChunkPos deadChunk = dead.chunkPosition();
            final int chunkX = deadChunk.x;
            final int chunkZ = deadChunk.z;
            final int radius = exploder.chaos$deliverRadius();
            Runnable task = () -> {
                for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                    for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                        IntIterator entities = EntityTracker.getEntities(level, ChunkPos.asLong(x, z)).iterator();
                        if (!entities.hasNext()) continue;

                        Entity candidate = null;

                        while (candidate == null && entities.hasNext()) {
                            candidate = level.getEntity(entities.nextInt());
                            if ((candidate instanceof Exploder already && already.chaos$isExploder()) || !(candidate instanceof Enemy)) {
                                candidate = null;
                            }
                        }

                        if (candidate instanceof Exploder toDeliver) {
                            toDeliver.chaos$setExploder(true);
                            toDeliver.chaos$setExplodeRadius(exploder.chaos$explodeRadius() + 4);
                            toDeliver.chaos$setDeliverRadius(exploder.chaos$deliverRadius() + 1);
                            toDeliver.chaos$setDeliveryCount(exploder.chaos$deliveryCount() + 1);
                            LivingChaos.LOGGER.info("[LivingChaos] Exploder delivery successfully processed. Target: {}", candidate);
                            explosion.chaos$setDelivered(true);
                            return;
                        }
                    }
                }
            };

            level.getServer().execute(task);
        }
    }

    private record Strength(ServerLevel level, LivingEntity dead) implements Runnable {
        @Override
        public void run() {
            final ChunkPos deadChunk = this.dead.chunkPosition();
            final int chunkX = deadChunk.x;
            final int chunkZ = deadChunk.z;
            final int radius = ChaosConfig.DEAD_RATTLE_STRENGTH_RADIUS;
            final MobEffect speed = MobEffects.MOVEMENT_SPEED;
            final MobEffect strength = MobEffects.DAMAGE_BOOST;
            Runnable task = () -> {
                for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                    for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                        for (int id : EntityTracker.getEntities(this.level, ChunkPos.asLong(x, z))) {
                            Entity entity = this.level.getEntity(id);
                            if (entity instanceof LivingEntity living && entity instanceof Enemy) {
                                DeadRattle.updateEffect(living, speed, strength);
                            }
                        }
                    }
                }
            };

            this.level.getServer().execute(task);
        }
    }

    public static void updateEffect(@NotNull LivingEntity living, MobEffect @NotNull ... effects) {
        for (MobEffect effect : effects) {
            MobEffectInstance effectInstance = living.getEffect(effect);
            living.addEffect(new MobEffectInstance(effect, 400, effectInstance == null ? 0 : effectInstance.getAmplifier() + 1));
        }
    }
}
