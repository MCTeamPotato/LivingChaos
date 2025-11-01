package me.kall.livingchaos.api;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.livingchaos.api.duck.IEntityType;
import me.kall.livingchaos.api.event.EntityChunkChangeEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EntityTracker {
    private static final Object2ObjectMap<ResourceLocation, Long2ObjectMap<IntSet>> ENTITIES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<ResourceLocation, Long2ObjectMap<Object2ObjectMap<ResourceLocation, IntSet>>> ENTITIES_BY_TYPE = new Object2ObjectOpenHashMap<>();
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

    public static @NotNull @UnmodifiableView IntSet getEntities(@NotNull ServerLevel level, long chunkPos) {
        if (!level.getServer().isSameThread()) throw new UnsupportedOperationException("EntityTracker is only available on the server thread!");
        Long2ObjectMap<IntSet> chunks = ENTITIES.get(level.dimension().location());
        if (chunks == null || chunks.isEmpty()) return IntSets.emptySet();
        IntSet entities = chunks.get(chunkPos);
        if (entities == null || entities.isEmpty()) return IntSets.emptySet();
        return IntSets.unmodifiable(entities);
    }

    public static @NotNull @UnmodifiableView IntSet getEntities(@NotNull ServerLevel level, long chunkPos, EntityType<?> type) {
        if (!level.getServer().isSameThread()) throw new UnsupportedOperationException("EntityTracker is only available on the server thread!");
        Long2ObjectMap<Object2ObjectMap<ResourceLocation, IntSet>> chunks = ENTITIES_BY_TYPE.get(level.dimension().location());
        if (chunks == null || chunks.isEmpty()) return IntSets.emptySet();
        Object2ObjectMap<ResourceLocation, IntSet> entitiesByType = chunks.get(chunkPos);
        if (entitiesByType == null || entitiesByType.isEmpty()) return IntSets.emptySet();
        IntSet entities = entitiesByType.get(((IEntityType)type).chaos$registryName());
        if (entities == null || entities.isEmpty()) return IntSets.emptySet();
        return IntSets.unmodifiable(entities);
    }

    private static void update(@NotNull Entity entity, @NotNull ServerLevel level, boolean add) {
        final long chunkPos = entity.chunkPosition().toLong();
        final ResourceLocation dim = level.dimension().location();
        final int id = entity.getId();
        final ResourceLocation entityType = ((IEntityType) entity.getType()).chaos$registryName();
        if (entityType.equals(IEntityType.NONE)) return;

        TASKS.add(() -> {
            Long2ObjectMap<IntSet> chunks = ENTITIES.computeIfAbsent(dim, key -> new Long2ObjectOpenHashMap<>());
            if (add) {
                chunks.computeIfAbsent(chunkPos, key -> new IntOpenHashSet()).add(id);
            } else {
                IntSet entities = chunks.get(chunkPos);
                if (entities != null) {
                    entities.remove(id);
                    if (entities.isEmpty()) chunks.remove(chunkPos);
                    if (chunks.isEmpty()) ENTITIES.remove(dim);
                }
            }

            Long2ObjectMap<Object2ObjectMap<ResourceLocation, IntSet>> chunksByType = ENTITIES_BY_TYPE.computeIfAbsent(dim, key -> new Long2ObjectOpenHashMap<>());
            if (add) {
                chunksByType.computeIfAbsent(chunkPos, key -> new Object2ObjectOpenHashMap<>()).computeIfAbsent(entityType, key -> new IntOpenHashSet()).add(id);
            } else {
                Object2ObjectMap<ResourceLocation, IntSet> entitiesByType = chunksByType.get(chunkPos);
                if (entitiesByType != null) {
                    IntSet entities = entitiesByType.get(entityType);
                    if (entities != null) {
                        entities.remove(id);
                        if (entities.isEmpty()) entitiesByType.remove(entityType);
                        if (entitiesByType.isEmpty()) chunksByType.remove(chunkPos);
                        if (chunksByType.isEmpty()) ENTITIES_BY_TYPE.remove(dim);
                    }
                }
            }
        });
    }

    public static void register(@NotNull IEventBus bus) {
        bus.addListener(EntityTracker::onJoin);
        bus.addListener(EntityTracker::onLeave);
        bus.addListener(EntityTracker::onUpdatePost);
        bus.addListener(EntityTracker::onUpdatePre);
        bus.addListener(EntityTracker::onTick);
    }

    public static void onJoin(@NotNull EntityJoinLevelEvent event) {
        if (event.isCanceled()) return;
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ServerLevel level) {
            update(entity, level, true);
        }
    }

    public static void onLeave(@NotNull EntityLeaveLevelEvent event) {
        if (event.isCanceled()) return;
        Entity entity = event.getEntity();
        if (event.getLevel() instanceof ServerLevel level) {
            update(entity, level, false);
        }
    }

    public static void onUpdatePre(EntityChunkChangeEvent.@NotNull Before event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            update(entity, level, false);
        }
    }

    public static void onUpdatePost(EntityChunkChangeEvent.@NotNull After event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level) {
            update(entity, level, true);
        }
    }

    public static void onTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            event.getServer().execute(() -> {
                Runnable task;
                while ((task = EntityTracker.TASKS.poll()) != null) task.run();
            });
        }
    }
}