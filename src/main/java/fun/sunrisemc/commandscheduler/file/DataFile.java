package fun.sunrisemc.commandscheduler.file;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;

/**
 * File Package Version 1.0.0
 */
public class DataFile extends YAMLWrapper {

    // Instance //

    protected final String name;

    public DataFile(@NotNull String name, YamlConfiguration configuration) {
        super(configuration);
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
            CommandSchedulerPlugin.logSevere("Failed to save data file for " + this.name + ".yml.");
            return false;
        }
    }

    public boolean delete() {
        // Get the player data folder
        File dataFile = getFolder();
        
        // Get the file
        File file = new File(dataFile, this.name + ".yml");

        // Check if the file exists
        if (!file.exists()) {
            return true;
        }

        // Attempt to delete the file
        try {
            return file.delete();
        }
        catch (Exception e) {
            CommandSchedulerPlugin.logSevere("Failed to delete data file for " + name + ".yml.");
            return false;
        }
    }

    // Static Methods //

    @NotNull
    public static DataFile get(@NotNull String name) {
        // Get the file
        File file = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception e) {
                CommandSchedulerPlugin.logSevere("Failed to create data file for " + name + ".yml.");
                return new DataFile(name, new YamlConfiguration());
            }
        }
        
        // Load the configuration
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        return new DataFile(name, yamlConfiguration);
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
        File pluginFolder = ConfigFile.getFolder();

        // Get the data folder 
        File dataFolder = new File(pluginFolder, "data");

        // Create the data folder if it does not exist
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }
}