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
import me.kall.livingchaos.event.expsteal.ExpSteal;
import me.kall.livingchaos.event.greedy.Greedy;
import me.kall.livingchaos.event.multishoot.MultiShoot;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLLoader;

public class ModEvents {
    public static void register(IEventBus forgeBus) {
        EntityTracker.register();
        DeathLock.register(forgeBus);

        forgeBus.addListener(EventPriority.LOWEST, ResistanceReduction::onLivingDamage);
        forgeBus.addListener(EventPriority.LOWEST, DeadRattle::onLivingDeath);
        forgeBus.addListener(Explosive::onExploderDeath);
        forgeBus.addListener(Parent::onLivingAttack);
        forgeBus.addListener(Parent::onTick);
        forgeBus.addListener(EffectMirror::onLivingDamage);
        forgeBus.addListener(EventPriority.LOWEST, MultiShoot::onProjectileJoin);
        forgeBus.addListener(MultiShoot::onTick);
        forgeBus.addListener(Greedy::onItemPick);
        forgeBus.addListener(EventPriority.LOWEST, Greedy::onLivingTick);
        forgeBus.addListener(Rubbish::setup);
        forgeBus.addListener(EventPriority.LOWEST, ExpSteal::onLivingDeath);
        forgeBus.addListener(EventPriority.LOWEST, ExpSteal::onPlayerHurt);

        if (FMLLoader.getDist().isClient()) {
            forgeBus.addListener(DeathLockClient::renderTotem);
        }
    }
}
