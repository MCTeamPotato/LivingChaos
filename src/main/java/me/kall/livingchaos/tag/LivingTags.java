package me.kall.livingchaos.tag;

import me.kall.livingchaos.LivingChaos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class LivingTags {
    public static final TagKey<EntityType<?>> DEATH_LOCK = init("death_lock");
    public static final TagKey<EntityType<?>> EFFECT_MIRROR = init("effect_mirror");
    public static final TagKey<EntityType<?>> MULTI_SHOOT = init("multi_shoot");
    public static final TagKey<EntityType<?>> DEAD_RATTLE = init("dead_rattle");

    private static @NotNull TagKey<EntityType<?>> init(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LivingChaos.MOD_ID, name));
    }
}
