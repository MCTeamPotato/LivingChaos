package me.kall.livingchaos.event.deadrattle.types;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.api.EntityTracker;
import me.kall.livingchaos.api.duck.Exploder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

public record Explosive(ServerLevel level, LivingEntity dead) implements Runnable {
    private static final IntSet EXPLODERS = IntSets.synchronize(new IntOpenHashSet());

    @Override
    public void run() {
        ((Exploder) this.dead).chaos$setExploder(true);
        this.level.explode(this.dead, this.dead.getX(), this.dead.getY(), this.dead.getZ(), ((Exploder) this.dead).chaos$explodeRadius(), false, Level.ExplosionInteraction.MOB);
    }

    public static void delivery(@NotNull ServerLevel level, @NotNull LivingEntity dead, @NotNull Exploder exploder) {
        final ChunkPos deadChunk = dead.chunkPosition();
        final int chunkX = deadChunk.x;
        final int chunkZ = deadChunk.z;
        final int radius = exploder.chaos$deliverRadius();
        Runnable task = () -> {
            for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                    for (int id : EntityTracker.getEntities(level, ChunkPos.asLong(x, z))) {
                        Entity candidate;

                        candidate = level.getEntity(id);
                        if ((candidate instanceof Exploder already && already.chaos$isExploder()) || !(candidate instanceof Enemy)) {
                            candidate = null;
                        }

                        if (candidate instanceof Exploder toDeliver) {
                            toDeliver.chaos$setExploder(true);
                            toDeliver.chaos$setExplodeRadius(exploder.chaos$explodeRadius() + 4);
                            toDeliver.chaos$setDeliverRadius(exploder.chaos$deliverRadius() + 1);
                            toDeliver.chaos$setDeliveryCount(exploder.chaos$deliveryCount() + 1);
                            EXPLODERS.add(id);
                            LivingChaos.LOGGER.info("[LivingChaos] Exploder delivery successfully processed. Target: {}", candidate);
                            return;
                        }
                    }
                }
            }
        };

        level.getServer().execute(task);
    }

    public static void onExploderDeath(@NotNull LivingDeathEvent event) {
        LivingEntity deadExploder = event.getEntity();
        if (EXPLODERS.remove(deadExploder.getId()) && deadExploder.level() instanceof ServerLevel level) {
            level.getServer().execute(new Explosive(level, deadExploder));
        }
    }
}
