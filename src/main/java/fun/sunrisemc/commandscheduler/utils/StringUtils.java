package fun.sunrisemc.commandscheduler.utils;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.scheduledcommand.CommandType;
import fun.sunrisemc.commandscheduler.scheduledcommand.ExecuteOn;

public class StringUtils {

    // Parsing

    public static Optional<Integer> parseInteger(@NotNull String str) {
        try {
            int value = Integer.parseInt(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(@NotNull String str) {
        try {
            double value = Double.parseDouble(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(@NotNull String str) {
        try {
            long value = Long.parseLong(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<CommandType> parseCommandType(@NotNull String commandTypeName) {
        String normalizedNameA = normalize(commandTypeName);
        for (CommandType commandType : CommandType.values()) {
            String normalizedNameB = normalize(commandType.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }

    public static Optional<ExecuteOn> parseExecuteOn(@NotNull String executeOnName) {
        String normalizedNameA = normalize(executeOnName);
        for (ExecuteOn executeOn : ExecuteOn.values()) {
            String normalizedNameB = normalize(executeOn.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(executeOn);
            }
        }
        return Optional.empty();
    }

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

    // Formatting

    @NotNull
    public static String formatName(@NotNull String name) {
        return name.toLowerCase()
                   .replace("minecraft:", "")
                   .replace(" ", "-")
                   .replace("_", "-")
                   .replace(":", "-")
                   .trim();
    }

    // Normalization

    @NotNull
    public static String normalize(@NotNull String str) {
        return str.toLowerCase()
                  .replace("minecraft:", "")
                  .replace(" ", "")
                  .replace("_", "")
                  .replace("-", "")
                  .replace(":", "")
                  .trim();
    }   
}