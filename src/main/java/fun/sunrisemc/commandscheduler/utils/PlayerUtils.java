package fun.sunrisemc.commandscheduler.utils;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.RayTraceResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player Utils Class Version 1.0.0
 */
public class PlayerUtils {

    // Hand Item

    public static Optional<Material> getMaterialInHand(@NotNull Player player) {
        Optional<ItemStack> itemInHand = getItemInHand(player);
        if (itemInHand.isPresent()) {
            return Optional.of(itemInHand.get().getType());
        }
        return Optional.empty();
    }

    public static Optional<ItemStack> getItemInHand(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        ItemStack main = inventory.getItemInMainHand();
        if (!isEmptyItemStack(main)) {
            return Optional.of(main);
        }

        ItemStack off = inventory.getItemInOffHand();
        if (!isEmptyItemStack(off)) {
            return Optional.of(off);
        }

        return Optional.empty();
    }

    // Ray Trace

    public static Optional<Block> getLookedAtBlock(@NotNull Player player) {
        RayTraceResult result = player.rayTraceBlocks(64.0);
        if (result == null) {
            return Optional.empty();
        }

        Block block = result.getHitBlock();
        if (block == null) {
            return Optional.empty();
        }

        return Optional.of(block);
    }

    public static Optional<Inventory> getLookedAtInventory(@NotNull Player player) {
        Optional<Block> lookedAtBlock = getLookedAtBlock(player);
        if (lookedAtBlock.isEmpty()) {
            return Optional.empty();
        }

        BlockState blockState = lookedAtBlock.get().getState();
        if (!(blockState instanceof Container)) {
            return Optional.empty();
        }

        Container container = (Container) blockState;
        return Optional.of(container.getInventory());
    }

    // Utils

    private static boolean isEmptyItemStack(@Nullable ItemStack item) {
        return item == null || item.getAmount() == 0 || item.getType().isAir();
    }
}