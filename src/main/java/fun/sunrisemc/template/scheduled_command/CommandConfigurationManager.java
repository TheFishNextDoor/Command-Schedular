package fun.sunrisemc.template.scheduled_command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.template.CommandSchedularPlugin;
import fun.sunrisemc.template.file.ConfigFile;

public class CommandConfigurationManager {

    private static HashMap<String, CommandConfiguration> scheduledCommands = new HashMap<>();

    public static Optional<CommandConfiguration> get(String id) {
        return Optional.ofNullable(scheduledCommands.get(id));
    }

    public static List<String> getIds() {
        return new ArrayList<>(scheduledCommands.keySet());
    }

    public static void loadConfig() {
        YamlConfiguration config = ConfigFile.get("commands", false);
        for (String id : config.getKeys(false)) {
            CommandConfiguration scheduledCommand = new CommandConfiguration(config, id);
            scheduledCommands.put(id, scheduledCommand);
        }

        CommandSchedularPlugin.logInfo("Loaded " + scheduledCommands.size() + " commands configurations.");
    }
}