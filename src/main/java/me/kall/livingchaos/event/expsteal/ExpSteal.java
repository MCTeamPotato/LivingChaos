package me.kall.livingchaos.event.expsteal;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.kall.livingchaos.data.StolenExp;
import me.kall.livingchaos.init.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class ExpSteal {
    public static void onPlayerHurt(@NotNull LivingDamageEvent event) {
        if (event.isCanceled()) return;
        if (event.getEntity() instanceof ServerPlayer player && player.experienceLevel > 0 && event.getSource().getEntity() instanceof LivingEntity source && source.getType().is(ModTags.EXP_STEAL)) {
            int exp = (int) event.getAmount();
            player.giveExperienceLevels(-exp);
            UUID playerId = player.getUUID();
            Object2IntMap<UUID> experienceByPlayer = StolenExp.STOLEN_EXP.computeIfAbsent(source.getUUID(), key -> new Object2IntOpenHashMap<>());
            experienceByPlayer.put(playerId, experienceByPlayer.containsKey(playerId) ? exp + experienceByPlayer.getInt(playerId) : exp);
        }
    }

    public static void onLivingDeath(@NotNull LivingDeathEvent event) {
        if (event.isCanceled()) return;
        LivingEntity entity = event.getEntity();
        if (entity.getType().is(ModTags.EXP_STEAL) && entity.level() instanceof ServerLevel level) {
            StolenExp.STOLEN_EXP.getOrDefault(entity.getUUID(), Object2IntMaps.emptyMap()).forEach((uuid, exp) -> Optional.ofNullable(level.getPlayerByUUID(uuid)/*TODO: If the player is offline*/).ifPresent(player -> player.giveExperienceLevels(exp)));
        }
    }
}
