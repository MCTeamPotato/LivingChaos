package me.kall.livingchaos.event.deadrattle.types;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Rubbish(ServerLevel level, LivingEntity dead) implements Runnable {
    private static final List<Item> DROPPABLE = new ObjectArrayList<>();

    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            this.level.addFreshEntity(new ItemEntity(this.level, this.dead.getX(), this.dead.getY(), this.dead.getZ(), random().setHoverName(Component.literal(String.valueOf(System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(114514L, 1919810L))))));
        }
    }

    private static @NotNull ItemStack random() {
        return DROPPABLE.get(ThreadLocalRandom.current().nextInt(DROPPABLE.size())).getDefaultInstance();
    }

    public static void setup(@NotNull ServerStartedEvent event) {
        ForgeRegistries.ITEMS.getValues().stream().filter(Rubbish::isRubbish).forEach(DROPPABLE::add);
    }

    @SuppressWarnings("deprecation")
    private static boolean isRubbish(@NotNull Item item) {
        Holder.Reference<Item> reference = item.builtInRegistryHolder();
        return item.equals(Items.POISONOUS_POTATO) || item.equals(Items.ROTTEN_FLESH) || item.equals(Items.COBBLESTONE) || item.equals(Items.DEAD_BUSH)
                || reference.is(ItemTags.BUTTONS)
                || reference.is(ItemTags.FLOWERS)
                || reference.is(ItemTags.LEAVES)
                || reference.is(ItemTags.TALL_FLOWERS)
                || reference.is(ItemTags.SAPLINGS);
    }
}
