package fun.sunrisemc.commandscheduler.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.player.PlayerProfileManager;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        PlayerProfileManager.unload(event.getPlayer());
    }
}