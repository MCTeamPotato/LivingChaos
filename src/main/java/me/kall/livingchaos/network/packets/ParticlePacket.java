package me.kall.livingchaos.network.packets;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ParticlePacket {
    private final int entity;
    private final byte particle;

    public ParticlePacket(int entity, byte particle) {
        this.entity = entity;
        this.particle = particle;
    }

    public ParticlePacket(@NotNull FriendlyByteBuf buf) {
        this.entity = buf.readInt();
        this.particle = buf.readByte();
    }

    public void toBytes(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(this.entity);
        buf.writeByte(this.particle);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) return;
            Entity target = level.getEntity(this.entity);
            if (target == null) return;
            ParticleOptions particle = ParticlesConstant.PARTICLES.get(this.particle);
            if (particle == null) return;
            mc.particleEngine.createTrackingEmitter(target, particle);
        });
        ctx.get().setPacketHandled(true);
    }

    public static class ParticlesConstant {
        public static final Byte2ObjectMap<SimpleParticleType> PARTICLES = new Byte2ObjectOpenHashMap<>();

        public static final byte DEATH_LOCK = 0;
        public static final byte MULTI_SHOOT = 1;
        public static final byte PARENT = 2;

        static {
            PARTICLES.put(DEATH_LOCK, ParticleTypes.HAPPY_VILLAGER);
            PARTICLES.put(MULTI_SHOOT, ParticleTypes.ENTITY_EFFECT);
            PARTICLES.put(PARENT, ParticleTypes.ANGRY_VILLAGER);
        }
    }
}
