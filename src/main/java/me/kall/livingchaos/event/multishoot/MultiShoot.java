package me.kall.livingchaos.event.multishoot;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.kall.livingchaos.LivingChaos;
import me.kall.livingchaos.api.IProjectile;
import me.kall.livingchaos.api.Shooter;
import me.kall.livingchaos.config.ChaosConfig;
import me.kall.livingchaos.network.NetworkManager;
import me.kall.livingchaos.network.packets.ParticlePacket;
import me.kall.livingchaos.tag.LivingTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(modid = LivingChaos.MOD_ID)
public class MultiShoot {
    private static final Int2ObjectMap<List<Runnable>> TASKS = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onProjectileJoin(@NotNull EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getEntity() instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity owner) {
            if (!((IProjectile)projectile).chaos$isSource() || !owner.getType().is(LivingTags.MULTI_SHOOT) || !((Shooter)owner).chaos$canGatling()) return;

            int tickCount = level.getServer().getTickCount();
            EntityType<?> type = projectile.getType();
            Vec3 deltaMovement = projectile.getDeltaMovement();

            Vec3 position = projectile.position();
            Vec3 ownerPos = owner.position();
            double xOffset = position.x - ownerPos.x;
            double yOffset = position.y - ownerPos.y;
            double zOffset = position.z - ownerPos.z;

            double lastXPower;
            double lastYPower;
            double lastZPower;

            if (projectile instanceof AbstractHurtingProjectile hurting) {
                lastXPower = hurting.xPower;
                lastYPower = hurting.yPower;
                lastZPower = hurting.zPower;
            } else {
                lastZPower = 0;
                lastYPower = 0;
                lastXPower = 0;
            }

            level.getServer().execute(() -> {
                for (int i = 0; i < ThreadLocalRandom.current().nextInt(ChaosConfig.MULTI_SHOOT_MIN_COUNT, ChaosConfig.MULTI_SHOOT_MAX_COUNT + 1); i++) {
                    TASKS.computeIfAbsent(tickCount + ChaosConfig.MULTI_SHOOT_INTERVAL * (i + 1), key -> new ArrayList<>()).add(() -> genTask(type, owner, xOffset, yOffset, zOffset, deltaMovement, lastXPower, lastYPower, lastZPower));
                }
            });

            level.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.EVOKER_CAST_SPELL, owner.getSoundSource(), 2.5F, 1.5F);
            NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> owner), new ParticlePacket(owner.getId(), ParticlePacket.ParticlesConstant.MULTI_SHOOT));

            ((Shooter)owner).chaos$setCanGatling(false);
        }
    }

    private static void genTask(@NotNull EntityType<?> type, @NotNull LivingEntity owner, double xOffset, double yOffset, double zOffset, Vec3 deltaMovement, double lastXPower, double lastYPower, double lastZPower) {
        Entity entity = type.create(owner.level());
        if (entity instanceof Projectile toShoot && owner instanceof Mob shooter && shooter.isAlive()) {
            LivingEntity target = shooter.getTarget();
            if (target != null) {
                toShoot.setOwner(owner);
                toShoot.setPos(owner.getX() + xOffset, owner.getY() + yOffset, owner.getZ() + zOffset);
                toShoot.setXRot(shooter.getXRot());
                toShoot.setYRot(shooter.getYRot());

                Vec3 shootPos = new Vec3(target.getX() - toShoot.getX(), target.getEyeY() - toShoot.getY(), target.getZ() - toShoot.getZ()).normalize();
                double speed = deltaMovement.length();
                toShoot.setDeltaMovement(shootPos.scale(speed));

                if (toShoot instanceof AbstractHurtingProjectile hurting) {
                    Vec3 targetDirection = new Vec3(target.getX() - owner.getX(), target.getEyeY() - owner.getY(), target.getZ() - owner.getZ()).normalize();
                    hurting.xPower = lastXPower * (1.0 - speed) + targetDirection.x * speed;
                    hurting.yPower = lastYPower * (1.0 - speed) + targetDirection.y * speed;
                    hurting.zPower = lastZPower * (1.0 - speed) + targetDirection.z * speed;
                }

                ((IProjectile)toShoot).chaos$setSource(false);
                owner.level().addFreshEntity(toShoot);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.@NotNull ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            int tickCount = event.getServer().getTickCount();
            event.getServer().execute(() -> {
                List<Runnable> task = TASKS.get(tickCount);
                if (task == null) return;
                task.forEach(Runnable::run);
                TASKS.remove(tickCount);
            });
        }
    }
}
