package fun.sunrisemc.commandscheduler.player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;

public class PlayerProfile {

    private final @NotNull UUID UUID;

    private final @NotNull HashMap<String, Boolean> lastConditionCheckValues = new HashMap<>();

    protected PlayerProfile(@NotNull Player player) {
        this.UUID = player.getUniqueId();
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(UUID));
    }

    public boolean getLastConditionCheckValue(CommandConfiguration commandConfiguration) {
        return lastConditionCheckValues.getOrDefault(commandConfiguration.getId(), false);
    }

    public void setLastConditionCheckValue(CommandConfiguration commandConfiguration, boolean value) {
        lastConditionCheckValues.put(commandConfiguration.getId(), value);
    }
}