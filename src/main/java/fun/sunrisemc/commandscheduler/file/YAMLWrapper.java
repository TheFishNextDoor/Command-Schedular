package fun.sunrisemc.commandscheduler.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;

/**
 * File Package Version 1.0.0
 */
public abstract class YAMLWrapper {

    // Fields

    protected final @NotNull YamlConfiguration config;

    // Constructor

    protected YAMLWrapper(@NotNull YamlConfiguration config) {
        this.config = config;
    }

    // YAML

    @NotNull
    public YamlConfiguration getYAML() {
        return this.config;
    }

    // Keys

    public boolean hasKey(@NotNull String path) {
        return this.config.contains(path);
    }

    @NotNull
    public HashSet<String> getKeys() {
        return new HashSet<>(this.config.getKeys(false));
    }

    @NotNull
    public HashSet<String> getKeys(@NotNull String path) {
        if (!this.config.contains(path)) {
            return new HashSet<>();
        }

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return new HashSet<>();
        }

        return new HashSet<>(section.getKeys(false));
    }

    public boolean renameKey(String oldKey, String newKey) {
        if (!this.config.contains(oldKey) || this.config.contains(newKey)) {
            return false;
        }

        Object value = this.config.get(oldKey);

        config.set(newKey, value);
        config.set(oldKey, null);

        return true;
    }

    public boolean moveKey(String oldKey, String newKey) {
        if (!this.config.contains(oldKey) || this.config.contains(newKey)) {
            return false;
        }

        Object value = this.config.get(oldKey);

        config.set(newKey, value);
        config.set(oldKey, null);

        return true;
    }

    // Reading Values

    public Optional<Boolean> getBoolean(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        boolean value = this.config.getBoolean(path);

        return Optional.of(value);
    }

    public Optional<Integer> getInt(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        int value = this.config.getInt(path);

        return Optional.of(value);
    }

    public Optional<Integer> getIntClamped(@NotNull String path, int min, int max) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        int value = this.config.getInt(path);

        int clampedValue = Math.clamp(value, min, max);
        if (clampedValue != value) {
            CommandSchedulerPlugin.logWarning("Value of '" + path + "' was clamped from " + value + " to " + clampedValue + ".");
        }

        return Optional.of(clampedValue);
    }

    public Optional<Long> getLong(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        long value = this.config.getLong(path);

        return Optional.of(value);
    }

    public Optional<Long> getLongClamped(@NotNull String path, long min, long max) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        long value = this.config.getLong(path);

        long clampedValue = Math.clamp(value, min, max);
        if (clampedValue != value) {
            CommandSchedulerPlugin.logWarning("Value of '" + path + "' was clamped from " + value + " to " + clampedValue + ".");
        }

        return Optional.of(clampedValue);
    }

    public Optional<Double> getDouble(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        double value = this.config.getDouble(path);

        return Optional.of(value);
    }

    public Optional<Double> getDoubleClamped(@NotNull String path, double min, double max) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        double value = this.config.getDouble(path);

        double clampedValue = Math.clamp(value, min, max);
        if (clampedValue != value) {
            CommandSchedulerPlugin.logWarning("Value of '" + path + "' was clamped from " + value + " to " + clampedValue + ".");
        }

        return Optional.of(clampedValue);
    }

    public Optional<String> getString(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        String value = this.config.getString(path);

        return Optional.ofNullable(value);
    }

    @NotNull
    public Optional<List<String>> getStringList(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        List<String> StringList = this.config.getStringList(path);

        if (StringList.isEmpty()) {
            String singleString = this.config.getString(path);
            if (singleString != null) {
                StringList = List.of(singleString);
            }
            else {
                return Optional.empty();
            }
        }

        return Optional.of(StringList);
    }

    public Optional<HashMap<String, Boolean>> getBooleanMap(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        HashMap<String, Boolean> map = new HashMap<>();

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return Optional.empty();
        }

        for (String key : section.getKeys(false)) {
            boolean value = section.getBoolean(key);
            map.put(key, value);
        }

        return Optional.of(map);
    }

    public Optional<HashMap<String, Integer>> getIntegerMap(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        HashMap<String, Integer> map = new HashMap<>();

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return Optional.empty();
        }

        for (String key : section.getKeys(false)) {
            int value = section.getInt(key);
            map.put(key, value);
        }

        return Optional.of(map);
    }

    public Optional<HashMap<String, Long>> getLongMap(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        HashMap<String, Long> map = new HashMap<>();

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return Optional.empty();
        }

        for (String key : section.getKeys(false)) {
            long value = section.getLong(key);
            map.put(key, value);
        }

        return Optional.of(map);
    }

    public Optional<HashMap<String, Double>> getDoubleMap(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        HashMap<String, Double> map = new HashMap<>();

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return Optional.empty();
        }

        for (String key : section.getKeys(false)) {
            double value = section.getDouble(key);
            map.put(key, value);
        }

        return Optional.of(map);
    }

    public Optional<HashMap<String, String>> getStringMap(@NotNull String path) {
        if (!this.config.contains(path)) {
            return Optional.empty();
        }

        HashMap<String, String> map = new HashMap<>();

        ConfigurationSection section = this.config.getConfigurationSection(path);
        if (section == null) {
            return Optional.empty();
        }

        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            if (value != null) {
                map.put(key, value);
            }
        }

        return Optional.of(map);
    }

    // Writing Values

    public void set(@NotNull String path, @NotNull Object value) {
        this.config.set(path, value);
    }

    public void setAll(@NotNull String path, @NotNull Map<String, ?> values) {
        for (String key : values.keySet()) {
            this.config.set(path + "." + key, values.get(key));
        }
    }

    public void clear(@NotNull String path) {
        this.config.set(path, null);
    }
}