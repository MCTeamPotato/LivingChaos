package me.kall.livingchaos;

import me.kall.livingchaos.event.deathlock.DeathLock;
import me.kall.livingchaos.init.ModEffects;
import me.kall.livingchaos.network.NetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(LivingChaos.MOD_ID)
public final class LivingChaos {
    public static final String MOD_ID = "livingchaos";
    public static final String MOD_NAME = "LivingChaos";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public LivingChaos(@NotNull FMLJavaModLoadingContext context) {
        NetworkManager.register();
        DeathLock.register(MinecraftForge.EVENT_BUS);
        ModEffects.register(context.getModEventBus());
    }
}
