package me.kall.livingchaos;

import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.init.ModEffects;
import me.kall.livingchaos.init.ModEvents;
import me.kall.livingchaos.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(LivingChaos.MOD_ID)
public final class LivingChaos {
    public static final String MOD_ID = "livingchaos";
    public static final String MOD_NAME = "Living Chaos";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public LivingChaos(@NotNull FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        NetworkManager.register();
        ModEffects.register(modBus);
        ModEvents.register(MinecraftForge.EVENT_BUS, modBus);
        context.registerConfig(ModConfig.Type.COMMON, ChaosConfig.INSTANCE);
    }
}
