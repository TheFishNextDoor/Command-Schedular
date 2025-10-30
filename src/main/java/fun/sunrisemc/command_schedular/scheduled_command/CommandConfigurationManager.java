package fun.sunrisemc.command_schedular.scheduled_command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.file.ConfigFile;

public class CommandConfigurationManager {

    private static HashMap<String, CommandConfiguration> scheduledCommands = new HashMap<>();

    public static Optional<CommandConfiguration> get(@NonNull String id) {
        return Optional.ofNullable(scheduledCommands.get(id));
    }

    public static List<CommandConfiguration> getAll() {
        return new ArrayList<>(scheduledCommands.values());
    }

    public static List<String> getIds() {
        return new ArrayList<>(scheduledCommands.keySet());
    }

    public static void loadConfig() {
        scheduledCommands.clear();

        YamlConfiguration config = ConfigFile.get("commands", false);
        for (String id : config.getKeys(false)) {
            CommandConfiguration scheduledCommand = new CommandConfiguration(config, id);
            scheduledCommands.put(id, scheduledCommand);
        }

        CommandSchedularPlugin.logInfo("Loaded " + scheduledCommands.size() + " commands configurations.");
    }
}