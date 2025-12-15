package fun.sunrisemc.commandscheduler.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class PlayerProfileManager {

    private static @NotNull HashMap<UUID, PlayerProfile> playerProfiles = new HashMap<>();

    @NotNull
    public static PlayerProfile get(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        PlayerProfile playerProfile = playerProfiles.get(uuid);
        if (playerProfile == null) {
            playerProfile = new PlayerProfile(player);
            playerProfiles.put(uuid, playerProfile);
        }
        return playerProfile;
    }

    public static void unload(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        playerProfiles.remove(uuid);
    }
}