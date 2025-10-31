package me.kall.livingchaos.init;

import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.effect.ResistanceReduction;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LivingChaos.MOD_ID);

    public static final RegistryObject<MobEffect> RESISTANCE_REDUCTION = REGISTER.register("resistance_reduction", ResistanceReduction::new);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
