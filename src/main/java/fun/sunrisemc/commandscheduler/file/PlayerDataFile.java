package fun.sunrisemc.commandscheduler.file;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;

/**
 * File Package Version 1.0.0
 */
public class PlayerDataFile extends YAMLWrapper {

    // Instance //

    protected final @NotNull String name;

    public PlayerDataFile(@NotNull String name, @NotNull YamlConfiguration config) {
        super(config);
        this.name = name;
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
            CommandSchedulerPlugin.logSevere("Failed to save player data file for " + name + ".yml.");
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
            CommandSchedulerPlugin.logSevere("Failed to delete player data file for " + this.name + ".yml.");
            return false;
        }
    }

    // Static Methods //

    @NotNull
    public static PlayerDataFile get(@NotNull UUID uuid) {
        // Get the name
        String name = uuid.toString();

        // Get the file
        File playerDataFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            }
            catch (Exception e) {
                CommandSchedulerPlugin.logSevere("Failed to create player data file for " + name + ".yml.");
                return new PlayerDataFile(name, new YamlConfiguration());
            }
        }

        // Load the configuration
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
        
        return new PlayerDataFile(name, yamlConfiguration);
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
        // Get data folder
        File dataFolder = DataFile.getFolder();

        // Get player data folder
        File playerDataFolder = new File(dataFolder, "players");

        // Create the player data folder if it does not exist
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
        
        return playerDataFolder;
    }
}