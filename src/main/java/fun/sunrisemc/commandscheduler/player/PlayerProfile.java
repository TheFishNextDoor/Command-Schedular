package fun.sunrisemc.commandscheduler.player;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class PlayerProfile {

    private final @NotNull UUID UUID;

    protected PlayerProfile(@NotNull Player player) {
        this.UUID = player.getUniqueId();
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(UUID));
    }
}