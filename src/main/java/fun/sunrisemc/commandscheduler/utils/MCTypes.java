package fun.sunrisemc.commandscheduler.utils;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import org.jetbrains.annotations.NotNull;

/**
 * MC Types Class Version 1.0.0
 */
public class MCTypes {

    // Environment

    public static Optional<Environment> parseEnvironment(@NotNull String environmentName) {
        String normalizedNameA = normalize(environmentName);
        for (Environment environment : Environment.values()) {
            String normalizedNameB = normalize(environment.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(environment);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getEnvironmentNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Environment environment : Environment.values()) {
            String formattedName = format(environment.name());
            names.add(formattedName);
        }
        return names;
    }

    // Biome

    public static Optional<Biome> parseBiome(@NotNull String biomeName) {
        String normalizedNameA = normalize(biomeName);
        for (Biome biome : Biome.values()) {
            String normalizedNameB = normalize(biome.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(biome);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getBiomeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Biome biome : Biome.values()) {
            String formattedName = format(biome.name());
            names.add(formattedName);
        }
        return names;
    }

    // Game Mode

    public static Optional<GameMode> parseGameMode(@NotNull String gameModeName) {
        String normalizedNameA = normalize(gameModeName);
        for (GameMode gameMode : GameMode.values()) {
            String normalizedNameB = normalize(gameMode.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(gameMode);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getGameModeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (GameMode gameMode : GameMode.values()) {
            String formattedName = format(gameMode.name());
            names.add(formattedName);
        }
        return names;
    }

    // Utils

    @NotNull
    private static String normalize(@NotNull String name) {
        String nameStripped = stripMinecraftTag(name);
        return StringUtils.normalize(nameStripped);
    }

    @NotNull
    private static String format(@NotNull String name) {
        String nameStripped = stripMinecraftTag(name);
        return StringUtils.kebabCase(nameStripped);
    }

    @NotNull
    private static String stripMinecraftTag(@NotNull String str) {
        if (str.toLowerCase().startsWith("minecraft:")) {
            return str.substring(10);
        }
        return str;
    }
}