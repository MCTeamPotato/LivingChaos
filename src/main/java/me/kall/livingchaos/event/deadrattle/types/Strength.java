package me.kall.livingchaos.event.deadrattle.types;

import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.api.EntityTracker;
import me.kall.livingchaos.event.deadrattle.DeadRattle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.ChunkPos;

public record Strength(ServerLevel level, LivingEntity dead) implements Runnable {
    @Override
    public void run() {
        final ChunkPos deadChunk = this.dead.chunkPosition();
        final int chunkX = deadChunk.x;
        final int chunkZ = deadChunk.z;
        final int radius = ChaosConfig.STRENGTH_RADIUS.get();
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

        LivingChaos.LOGGER.info("[LivingChaos] Enemies around [{}, {}] (radius: {}) are successfully enhanced with Speed and Strength", chunkX, chunkZ, radius);
    }
}
