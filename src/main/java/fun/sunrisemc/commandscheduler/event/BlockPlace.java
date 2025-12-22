package fun.sunrisemc.commandscheduler.event;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;
import fun.sunrisemc.commandscheduler.scheduledcommand.EventType;
import fun.sunrisemc.commandscheduler.utils.PlayerUtils;

public class BlockPlace implements Listener {
 
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        // Get player
        Player player = event.getPlayer();

        // Get block placed
        Block placedBlock = event.getBlock();

        // Get location of placed block
        Location blockLocation = placedBlock.getLocation();

        // Get item in player's hand
        ItemStack itemInHand = PlayerUtils.getItemInHand(player).orElse(null);

        // Trigger commands
        for (CommandConfiguration commandConfig : CommandConfigurationManager.getAll()) {
            commandConfig.onEvent(EventType.PLACE_BLOCK, blockLocation, player, itemInHand, placedBlock, 1);
        }
    }
}