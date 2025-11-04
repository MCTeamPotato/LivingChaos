package me.kall.livingchaos.config;

import me.kall.jsonate.api.JsonConfig;
import me.kall.livingchaos.LivingChaos;
import net.minecraftforge.common.ForgeConfigSpec;

public class ChaosConfig {
    private static final JsonConfig CONFIG = JsonConfig.create(LivingChaos.MOD_ID, "1")
            .put("DesperateDeathLock", false)
            .initialize();

    public static final boolean DESPERATE = CONFIG.getBoolean("DesperateDeathLock");

    public static final ForgeConfigSpec INSTANCE;
    public static final ForgeConfigSpec.IntValue DEATH_LOCK_SECONDS, MULTI_SHOOT_INTERVAL, MULTI_SHOOT_MIN_COUNT, MULTI_SHOOT_MAX_COUNT, MULTI_SHOOT_COOL_DOWN;
    public static final ForgeConfigSpec.IntValue EXPLOSIVE_DELIVERY_LIMIT, STRENGTH_RADIUS;
    public static final ForgeConfigSpec.IntValue GREEDY_RADIUS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push(LivingChaos.MOD_NAME);
        DEATH_LOCK_SECONDS = builder.comment("During this period, death-locked entities will not die on attacking.").defineInRange("DeathLockDurationSeconds", 6, 0, Integer.MAX_VALUE);
        MULTI_SHOOT_INTERVAL = builder.comment("The arrows during multi-shoot will be released one by one following this interval.").defineInRange("MultiShootIntervalTicks", 10, 0, Integer.MAX_VALUE);
        MULTI_SHOOT_MIN_COUNT = builder.comment("The released arrows' count will not go below this during multi-shoot", "The first arrow released by the shooter is excluded").defineInRange("MultiShootMinArrowsCount", 3, 0, Integer.MAX_VALUE);
        MULTI_SHOOT_MAX_COUNT = builder.comment("The released arrows' count will not go beyond this during multi-shoot", "The first arrow released by the shooter is excluded").defineInRange("MultiShootMaxArrowsCount", 5, 0, Integer.MAX_VALUE);
        MULTI_SHOOT_COOL_DOWN = builder.comment("After multi-shoot is triggered, the shooter will no longer trigger it again during this cooldown period.").defineInRange("MultiShootCoolDownSeconds", 5, 0, Integer.MAX_VALUE);
        builder.push("Dead Rattle");
        EXPLOSIVE_DELIVERY_LIMIT = builder.comment("The count of exploder property delivery towards other entities will not go beyond this limit.").defineInRange("ExploderDeliveryMaxCount", 10, 0, Integer.MAX_VALUE);
        STRENGTH_RADIUS = builder.comment("The distribution of speed and strength potion effect in nearby chunks will not expand beyond this radius.", "Do note that this radius means the extended chunks count from the center chunk, so 1 means 3*3=9 chunks, and if you only want the entity's current chunk to be affected, you need to write 0 here.").defineInRange("StrengthDistributionAroundChunkRadius", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("Greedy");
        GREEDY_RADIUS = builder.comment("The items absorption radius of greedy entities will not expand beyond this radius.", "Do note that this radius means the extended chunks count from the center chunk, so 1 means 3*3=9 chunks, and if you only want the entity's current chunk to be affected, you need to write 0 here.").defineInRange("GreedyAroundChunkRadius", 0, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.pop();
        INSTANCE = builder.build();
    }
}
