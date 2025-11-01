package me.kall.livingchaos.api.duck;

import me.kall.livingchaos.LivingChaos;
import net.minecraft.resources.ResourceLocation;

public interface IEntityType {
    ResourceLocation chaos$registryName();

    ResourceLocation NONE = ResourceLocation.fromNamespaceAndPath(LivingChaos.MOD_ID, "none");
}
