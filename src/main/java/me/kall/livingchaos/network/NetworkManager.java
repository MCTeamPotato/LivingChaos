package me.kall.livingchaos.network;

import com.google.common.base.Predicates;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.network.packets.ParticlePacket;
import me.kall.livingchaos.network.packets.UndamageableUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkManager {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(LivingChaos.MOD_ID, "main"), () -> "1", Predicates.alwaysTrue(), Predicates.alwaysTrue());
    private static int id = 0;

    public static void register() {
        INSTANCE.registerMessage(id++, UndamageableUpdatePacket.class, UndamageableUpdatePacket::toBytes, UndamageableUpdatePacket::new, UndamageableUpdatePacket::handle);
        INSTANCE.registerMessage(id++, ParticlePacket.class, ParticlePacket::toBytes, ParticlePacket::new, ParticlePacket::handle);
    }
}
