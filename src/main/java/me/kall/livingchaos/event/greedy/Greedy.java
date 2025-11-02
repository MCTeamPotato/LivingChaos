package me.kall.livingchaos.event.greedy;

import me.kall.livingchaos.api.EntityTracker;
import me.kall.livingchaos.api.ext.Unpickable;
import me.kall.livingchaos.init.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.jetbrains.annotations.NotNull;

public class Greedy {
    public static void onLivingTick(LivingEvent.@NotNull LivingTickEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level && entity.getType().is(ModTags.GREEDY) && level.getServer().getTickCount() % 20 == 0) {
            ChunkPos chunk = entity.chunkPosition();
            int entityId = entity.getId();
            int centerX = chunk.x;
            int centerZ = chunk.z;
            int radius = 3;
            level.getServer().execute(() -> {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        for (int id : EntityTracker.getEntities(level, ChunkPos.asLong(x, z), EntityType.ITEM)) {
                            Entity candidate = level.getEntity(id);
                            if (candidate instanceof ItemEntity item && item instanceof Unpickable unpickable) {
                                if (unpickable.chaos$isUnpickable()) {
                                    Entity owner = level.getEntity(unpickable.chaos$ownerEntity());
                                    if (owner != null) item.moveTo(owner.getX(), owner.getY(), owner.getZ());
                                } else {
                                    unpickable.chaos$setUnpickable(true);
                                    unpickable.chaos$setOwnerEntity(entityId);
                                    item.moveTo(entity.getX(), entity.getY(), entity.getZ());
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public static void validateOwner(ItemEntity item, @NotNull Unpickable unpickable) {
        if (unpickable.chaos$isUnpickable() && item.level() instanceof ServerLevel level && level.getServer().getTickCount() % 41 == 0) {
            if (item.level().getEntity(unpickable.chaos$ownerEntity()) == null) {
                unpickable.chaos$setUnpickable(false);
                unpickable.chaos$setOwnerEntity(-1);
            }
        }
    }

    public static void onItemPick(@NotNull EntityItemPickupEvent event) {
        if (((Unpickable)event.getItem()).chaos$isUnpickable()) {
            event.setCanceled(true);
        }
    }
}
