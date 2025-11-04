package me.kall.livingchaos.data;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.UUID;

public class StolenExp {
    //TODO: Saved data
    public static final Object2ObjectMap<UUID, Object2IntMap<UUID>> STOLEN_EXP = new Object2ObjectOpenHashMap<>();
}
