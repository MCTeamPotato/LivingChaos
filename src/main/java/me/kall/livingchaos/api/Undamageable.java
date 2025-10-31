package me.kall.livingchaos.api;

public interface Undamageable {
    boolean chaos$isUndamageable();
    void chaos$setUndamageable(boolean undamageable);

    boolean chaos$deathLocked();
    void chaos$setDeathLocked(boolean deathLocked);
}
