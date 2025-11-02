package me.kall.livingchaos.mixin.api;

import me.kall.livingchaos.api.event.EntityChunkChangeEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setPosRaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ChunkPos;<init>(Lnet/minecraft/core/BlockPos;)V"))
    private void beforeChunkPosUpdate(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EntityChunkChangeEvent.Before((Entity) (Object) this));
    }

    @Inject(method = "setPosRaw", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;chunkPosition:Lnet/minecraft/world/level/ChunkPos;", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
    private void afterChunkPosUpdate(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EntityChunkChangeEvent.After((Entity) (Object) this));
    }
}