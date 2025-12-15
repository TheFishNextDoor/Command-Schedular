package fun.sunrisemc.commandscheduler.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;

/**
 * File Package Version 1.0.0
 */
public class ConfigFile extends YAMLWrapper {

    // Instance //

    protected final @NotNull String name;

    public ConfigFile(@NotNull String name, @NotNull YamlConfiguration config) {
        super(config);
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean save() {
        // Get the file
        File file = new File(getFolder(), this.name + ".yml");

        // Save the configuration
        try {
            this.config.save(file);
            return true;
        } 
        catch (Exception e) {
            CommandSchedulerPlugin.logSevere("Failed to save configuration file for " + this.name + ".yml.");
            return false;
        }
    }

    public boolean delete() {
        // Get the player data folder
        File playerDataFolder = getFolder();
        
        // Get the file
        File file = new File(playerDataFolder, this.name + ".yml");

        // Check if the file exists
        if (!file.exists()) {
            return true;
        }

        // Attempt to delete the file
        try {
            return file.delete();
        }
        catch (Exception e) {
            CommandSchedulerPlugin.logSevere("Failed to delete configuration file for " + this.name + ".yml.");
            return false;
        }
    }

    // Static Methods //

    @NotNull
    public static ConfigFile get(@NotNull String name, boolean copyMissingDefaults) {
        // Get the file
        File file = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
        if (!file.exists()) {
            try {
                CommandSchedulerPlugin.getInstance().saveResource(name + ".yml", false);
            } 
            catch (Exception e) {
                CommandSchedulerPlugin.logWarning("Failed to create configuration file for " + name + ".yml.");
                e.printStackTrace();
                return new ConfigFile(name, new YamlConfiguration());
            }
        }

        // Load the configuration
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Copy missing default values
        if (copyMissingDefaults) {
            // Get default configuration
            YamlConfiguration defaultConfig = new YamlConfiguration();
            try {
                InputStream resourceStream = CommandSchedulerPlugin.getInstance().getResource(name + ".yml");
                InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                defaultConfig.load(reader);
            } 
            catch (Exception e) {
                CommandSchedulerPlugin.logSevere("Failed to get default configuration for " + name + ".yml.");
                return new ConfigFile(name, config);
            }

            // Copy missing keys
            boolean changed = false;
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    changed = true;
                }
            }

            if (changed) {
                ConfigFile configFile = new ConfigFile(name, config);
                configFile.save();
                CommandSchedulerPlugin.logInfo("Added missing default values to " + name + ".yml.");
                return configFile;
            }
        }

        return new ConfigFile(name, config);
    }

    @NotNull
    public static ArrayList<String> getNames() {
        // Get folder
        File folder = getFolder();
        
        // Get all yaml files in the folder
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return new ArrayList<>();
        }

        // Create list of names
        ArrayList<String> names = new ArrayList<>();
        for (File file : files) {
            // Get file name
            String nameWithExtension = file.getName();

            // Remove .yml extension
            String name = nameWithExtension.substring(0, nameWithExtension.length() - 4);

            // Add to list
            names.add(name);
        }

        return names;
    }

    @NotNull
    protected static File getFolder() {
        // Get plugin folder
        File pluginFolder = CommandSchedulerPlugin.getInstance().getDataFolder();

        // Create folder if it does not exist
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        return pluginFolder;
    }
}