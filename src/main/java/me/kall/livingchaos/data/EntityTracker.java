package me.kall.livingchaos.data;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.api.event.EntityChunkChangeEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = LivingChaos.MOD_ID)
public class EntityTracker {
    private static final Object2ObjectMap<ResourceLocation, Long2ObjectMap<IntSet>> ENTITIES = new Object2ObjectOpenHashMap<>();
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

    public static @NotNull @UnmodifiableView IntSet getEntities(@NotNull ServerLevel level, long chunkPos) {
        if (!level.getServer().isSameThread()) throw new UnsupportedOperationException("EntityTracker is only available on the server thread!");
        Long2ObjectMap<IntSet> chunks = ENTITIES.get(level.dimension().location());
        if (chunks == null || chunks.isEmpty()) return IntSets.emptySet();
        IntSet entities = chunks.get(chunkPos);
        if (entities == null || entities.isEmpty()) return IntSets.emptySet();
        return IntSets.unmodifiable(entities);
    }

    private static void removeEntity(@NotNull Entity entity, @NotNull ServerLevel level) {
        final long chunkPos = entity.chunkPosition().toLong();
        final ResourceLocation dim = level.dimension().location();
        final var chunkMap = ENTITIES.get(dim);
        if (chunkMap == null) return;
        final var entities = chunkMap.get(chunkPos);
        if (entities == null) return;
        final int id = entity.getId();
        TASKS.add(() -> {
            entities.remove(id);
            if (!entities.isEmpty()) return;
            chunkMap.remove(chunkPos);
            if (!chunkMap.isEmpty()) return;
            ENTITIES.remove(dim);
        });
    }

    private static void addEntity(@NotNull Entity entity, @NotNull ServerLevel level) {
        final long chunkPos = entity.chunkPosition().toLong();
        final ResourceLocation dim = level.dimension().location();
        final int id = entity.getId();
        TASKS.add(() -> ENTITIES.computeIfAbsent(dim, key -> new Long2ObjectOpenHashMap<>()).computeIfAbsent(chunkPos, key -> new IntOpenHashSet()).add(id));
    }

    @SubscribeEvent
    public static void onJoin(@NotNull EntityJoinLevelEvent event) {
        if (event.isCanceled()) return;
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ServerLevel level) {
            addEntity(entity, level);
        }
    }

    @SubscribeEvent
    public static void onLeave(@NotNull EntityLeaveLevelEvent event) {
        if (event.isCanceled()) return;
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    @SubscribeEvent
    public static void onUpdatePre(EntityChunkChangeEvent.@NotNull Before event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            removeEntity(entity, level);
        }
    }

    @SubscribeEvent
    public static void onUpdatePost(EntityChunkChangeEvent.@NotNull After event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            addEntity(entity, level);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            event.getServer().execute(() -> {
                Runnable task;
                while ((task = EntityTracker.TASKS.poll()) != null) task.run();
            });
        }
    }
}