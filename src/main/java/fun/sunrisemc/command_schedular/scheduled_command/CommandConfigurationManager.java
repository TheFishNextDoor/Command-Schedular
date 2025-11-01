package fun.sunrisemc.command_schedular.scheduled_command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.file.ConfigFile;

public class CommandConfigurationManager {

    private static HashMap<String, CommandConfiguration> scheduledCommandsMap = new HashMap<>();
    private static List<CommandConfiguration> scheduledCommandsList = new ArrayList<>();

    public static Optional<CommandConfiguration> get(@NonNull String id) {
        return Optional.ofNullable(scheduledCommandsMap.get(id));
    }

    public static List<CommandConfiguration> getAll() {
        return scheduledCommandsList;
    }

    public static List<String> getIds() {
        return new ArrayList<>(scheduledCommandsMap.keySet());
    }

    public static void loadConfig() {
        scheduledCommandsMap.clear();

        YamlConfiguration config = ConfigFile.get("commands", false);
        for (String id : config.getKeys(false)) {
            CommandConfiguration scheduledCommand = new CommandConfiguration(config, id);
            scheduledCommandsMap.put(id, scheduledCommand);
        }

        scheduledCommandsList = Collections.unmodifiableList(new ArrayList<>(scheduledCommandsMap.values()));

        CommandSchedularPlugin.logInfo("Loaded " + scheduledCommandsMap.size() + " command configurations.");
    }
}