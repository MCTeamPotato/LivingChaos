package me.kall.livingchaos.init;

import me.kall.livingchaos.api.EntityTracker;
import me.kall.livingchaos.effect.ResistanceReduction;
import me.kall.livingchaos.event.deadrattle.DeadRattle;
import me.kall.livingchaos.event.deadrattle.types.Explosive;
import me.kall.livingchaos.event.deadrattle.types.Parent;
import me.kall.livingchaos.event.deadrattle.types.Rubbish;
import me.kall.livingchaos.event.deathlock.DeathLock;
import me.kall.livingchaos.event.deathlock.DeathLockClient;
import me.kall.livingchaos.event.effectmirror.EffectMirror;
import me.kall.livingchaos.event.multishoot.MultiShoot;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

public class ModEvents {
    public static void register(IEventBus forgeBus, @NotNull IEventBus modBus) {
        EntityTracker.register(forgeBus);
        DeathLock.register(forgeBus);

        forgeBus.addListener(EventPriority.LOWEST, ResistanceReduction::onLivingDamage);
        forgeBus.addListener(EventPriority.LOWEST, DeadRattle::onLivingDeath);
        forgeBus.addListener(Explosive::onExploderDeath);
        forgeBus.addListener(Parent::onLivingAttack);
        forgeBus.addListener(Parent::onTick);
        forgeBus.addListener(EffectMirror::onLivingDamage);
        forgeBus.addListener(EventPriority.LOWEST, MultiShoot::onProjectileJoin);
        forgeBus.addListener(MultiShoot::onTick);

        modBus.addListener(Rubbish::setup);

        if (FMLLoader.getDist().isClient()) {
            forgeBus.addListener(DeathLockClient::renderTotem);
        }
    }
}
