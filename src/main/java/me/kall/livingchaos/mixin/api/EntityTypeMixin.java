package me.kall.livingchaos.mixin.api;

import me.kall.livingchaos.api.ext.IEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(EntityType.class)
public class EntityTypeMixin implements IEntityType {
    @Unique private ResourceLocation chaos$registryName;

    @Override
    public ResourceLocation chaos$registryName() {
        if (this.chaos$registryName == null) this.chaos$registryName = Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getKey((EntityType<?>) (Object) this)).orElse(IEntityType.NONE);
        return this.chaos$registryName;
    }
}
