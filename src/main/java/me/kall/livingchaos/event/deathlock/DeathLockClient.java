package me.kall.livingchaos.event.deathlock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.kall.livingchaos.api.duck.Undamageable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.jetbrains.annotations.NotNull;

public class DeathLockClient {
    public static void renderTotem(RenderLivingEvent.@NotNull Post<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Undamageable undamageable && undamageable.chaos$isUndamageable()) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            Minecraft minecraft = Minecraft.getInstance();

            poseStack.pushPose();

            poseStack.translate(0.0F, entity.getBbHeight() + 0.2, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + event.getPartialTick()) * 2));
            poseStack.scale(0.5F, 0.5F, 0.5F);

            minecraft.getItemRenderer().renderStatic(Items.TOTEM_OF_UNDYING.getDefaultInstance(), ItemDisplayContext.GROUND, event.getPackedLight(), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.level(), 0);

            poseStack.popPose();
        }
    }
}