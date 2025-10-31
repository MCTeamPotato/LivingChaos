package me.kall.livingchaos.config;

import me.kall.jsonate.api.JsonConfig;
import me.kall.livingchaos.LivingChaos;

public class ChaosConfig {
    private static final JsonConfig CONFIG = JsonConfig.create(LivingChaos.MOD_ID, "1919810")
            .put("DesperateDeathLock", false)
            .put("DeathLockDuration(Seconds)", 6)
            .put("MultiShootIntervalTicks", 10)
            .put("MultiShootMinCount", 3)
            .put("MultiShootMaxCount", 5)
            .put("MultiShootCoolDownSeconds", 5)
            .put("DeadRattleExplosionDeliveryMaxCount", 10)
            .put("DeadRattleStrengthAroundChunkRadius", 1)
            .initialize();

    public static final boolean DESPERATE = CONFIG.getBoolean("DesperateDeathLock");
    public static final int DEATH_LOCK_DURATION = CONFIG.getInt("DeathLockDuration(Seconds)");
    public static final int MULTI_SHOOT_INTERVAL = CONFIG.getInt("MultiShootIntervalTicks");
    public static final int MULTI_SHOOT_MIN_COUNT = CONFIG.getInt("MultiShootMinCount");
    public static final int MULTI_SHOOT_MAX_COUNT = CONFIG.getInt("MultiShootMaxCount");
    public static final int MULTI_SHOOT_COOL_DOWN = CONFIG.getInt("MultiShootCoolDownSeconds");
    public static final int DEAD_RATTLE_DELIVERY_LIMIT =  CONFIG.getInt("DeadRattleExplosionDeliveryMaxCount");
    public static final int DEAD_RATTLE_STRENGTH_RADIUS = CONFIG.getInt("DeadRattleStrengthAroundChunkRadius");

    public static void init() {
        //Classload trigger
    }
}
