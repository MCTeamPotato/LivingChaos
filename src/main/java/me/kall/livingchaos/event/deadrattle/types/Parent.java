package me.kall.livingchaos.event.deadrattle.types;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.kall.livingchaos.api.ext.ParentBear;
import me.kall.livingchaos.network.NetworkManager;
import me.kall.livingchaos.network.packets.ParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Parent(ServerLevel level, LivingEntity deadBaby) implements Runnable {
    private static final Int2ObjectMap<List<Runnable>> ANGER_TASKS = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());

    @Override
    public void run() {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(this.deadBaby.getX(), this.deadBaby.getY(), this.deadBaby.getZ());
        for (int height = 0; height <= 15; height++) {
            mutablePos.setY(this.deadBaby.getBlockY() + height);
            if (!this.level.isEmptyBlock(mutablePos)) {
                this.level.setBlockAndUpdate(mutablePos, Blocks.AIR.defaultBlockState());
            }
        }

        for (int parentCount = 0; parentCount < ThreadLocalRandom.current().nextInt(2, 5); parentCount++) {
            Entity parent = this.deadBaby.getType().create(this.level);
            if (parent instanceof Mob mob) {
                if (mob.isBaby()) mob.setBaby(false);
                ((ParentBear)mob).chaos$setParent(true);
                mob.setPos(this.deadBaby.getX(), this.deadBaby.getY() + 10, this.deadBaby.getZ());
                mob.setTarget(this.deadBaby.getLastAttacker());
                ANGER_TASKS.computeIfAbsent(this.level.getServer().getTickCount() + 40, key -> new ArrayList<>()).add(() -> NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> mob), new ParticlePacket(mob.getId(), ParticlePacket.ParticlesConstant.PARENT)));
                this.level.addFreshEntity(mob);
            }
        }
    }

    public static void onTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START) && !ANGER_TASKS.isEmpty()) {
            int tickCount = event.getServer().getTickCount();
            if (ANGER_TASKS.containsKey(tickCount)) {
                ANGER_TASKS.remove(tickCount).forEach(Runnable::run);
            }
        }
    }

    public static void onLivingAttack(@NotNull LivingAttackEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_FALL) && event.getEntity() instanceof ParentBear parentBear && parentBear.chaos$isParent()) {
            event.setCanceled(true);
        }
    }
}
