package me.kall.livingchaos.api.ext;

public interface Unpickable {
    boolean chaos$isUnpickable();
    void chaos$setUnpickable(boolean unpickable);

    int chaos$ownerEntity();
    void chaos$setOwnerEntity(int ownerEntity);
}
