package me.kall.livingchaos.api.duck;

public interface Exploder {
    boolean chaos$isExploder();
    void chaos$setExploder(boolean exploder);

    int chaos$explodeRadius();
    void chaos$setExplodeRadius(int explodeRadius);

    int chaos$deliverRadius();
    void chaos$setDeliverRadius(int deliverRadius);

    int chaos$deliveryCount();
    void chaos$setDeliveryCount(int deliveryCount);
}
