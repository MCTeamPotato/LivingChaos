package me.kall.livingchaos.network.packets;

import me.kall.livingchaos.api.ext.Undamageable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class UndamageableUpdatePacket {
    private final boolean undamageable;
    private final int entityId;

    public UndamageableUpdatePacket(@NotNull FriendlyByteBuf buf) {
        this.undamageable = buf.readBoolean();
        this.entityId = buf.readInt();
    }

    public UndamageableUpdatePacket(boolean undamageable, int entityId) {
        this.undamageable = undamageable;
        this.entityId = entityId;
    }

    public void toBytes(@NotNull FriendlyByteBuf buf) {
        buf.writeBoolean(this.undamageable);
        buf.writeInt(this.entityId);
    }

    public void handle(@NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Optional.ofNullable(Minecraft.getInstance().level)
                .flatMap(level -> Optional.ofNullable((level.getEntity(this.entityId))))
                .ifPresent(entity -> ((Undamageable)entity).chaos$setUndamageable(this.undamageable)));
        ctx.get().setPacketHandled(true);
    }
}
