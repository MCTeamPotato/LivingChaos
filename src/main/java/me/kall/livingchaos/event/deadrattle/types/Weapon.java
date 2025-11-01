package me.kall.livingchaos.event.deadrattle.types;

import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.api.EntityTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public record Weapon(ServerLevel level, LivingEntity dead) implements Runnable {
    @Override
    public void run() {
        final ChunkPos deadChunk = this.dead.chunkPosition();
        final int chunkX = deadChunk.x;
        final int chunkZ = deadChunk.z;
        final int radius = 1;
        final ItemStack mainHand = this.dead.getMainHandItem();
        final ItemStack offHand = this.dead.getOffhandItem();
        final EntityType<?> type = this.dead.getType();
        AtomicBoolean delivered = new AtomicBoolean(false);
        Runnable task = () -> {
            for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                    for (int id : EntityTracker.getEntities(this.level, ChunkPos.asLong(x, z), type)) {
                        Entity entity = this.level.getEntity(id);
                        if (!(entity instanceof LivingEntity living) || living.isDeadOrDying() || !living.getMainHandItem().isEmpty()) continue;

                        boolean mainHandEmpty = living.getMainHandItem().isEmpty();
                        boolean offHandEmpty = living.getOffhandItem().isEmpty();
                        if (mainHandEmpty && offHandEmpty) {
                            living.setItemSlot(EquipmentSlot.MAINHAND, mainHand.copy());
                            living.setItemSlot(EquipmentSlot.OFFHAND, offHand.copy());
                        } else if (mainHandEmpty) {
                            living.setItemSlot(EquipmentSlot.MAINHAND, mainHand.copy());
                        } else if (offHandEmpty) {
                            living.setItemSlot(EquipmentSlot.OFFHAND, offHand.copy());
                        } else {
                            continue;
                        }

                        LivingChaos.LOGGER.info("[LivingChaos] Weapon delivery successfully processed. Target: {}", living);
                        delivered.set(true);
                        return;
                    }
                }
            }
        };

        this.level.getServer().execute(task);
        LivingChaos.LOGGER.info("[LivingChaos] Weapon delivery: {}", delivered.get());
    }

    public static boolean hasWeapon(@NotNull LivingEntity living) {
        return living.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE) || living.getOffhandItem().getAttributeModifiers(EquipmentSlot.OFFHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }
}
