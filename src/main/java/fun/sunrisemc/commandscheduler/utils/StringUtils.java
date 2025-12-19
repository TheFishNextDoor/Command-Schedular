package fun.sunrisemc.commandscheduler.utils;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.scheduledcommand.CommandType;
import fun.sunrisemc.commandscheduler.scheduledcommand.ExecuteOn;

public class StringUtils {

    // Number Parsing

    public static Optional<Integer> parseInteger(@NotNull String str) {
        try {
            int value = Integer.parseInt(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(@NotNull String str) {
        try {
            double value = Double.parseDouble(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(@NotNull String str) {
        try {
            long value = Long.parseLong(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    // Command Type

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

    @NotNull
    public static ArrayList<String> getCommandTypeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (CommandType commandType : CommandType.values()) {
            String formattedName = kebabCase(commandType.name());
            names.add(formattedName);
        }
        return names;
    }

    // Execute On

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

    @NotNull
    public static ArrayList<String> getExecuteOnNames() {
        ArrayList<String> names = new ArrayList<>();
        for (ExecuteOn executeOn : ExecuteOn.values()) {
            String formattedName = kebabCase(executeOn.name());
            names.add(formattedName);
        }
        return names;
    }

    // Environment

    public static Optional<Environment> parseEnvironment(@NotNull String environmentName) {
        String normalizedNameA = normalize(stripMinecraftTag(environmentName));
        for (Environment environment : Environment.values()) {
            String normalizedNameB = normalize(stripMinecraftTag(environment.name()));
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
            String formattedName = kebabCase(environment.name());
            names.add(formattedName);
        }
        return names;
    }

    // Biome

    public static Optional<Biome> parseBiome(@NotNull String biomeName) {
        String normalizedNameA = normalize(stripMinecraftTag(biomeName));
        for (Biome biome : Biome.values()) {
            String normalizedNameB = normalize(stripMinecraftTag(biome.name()));
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
            String formattedName = kebabCase(biome.name());
            names.add(formattedName);
        }
        return names;
    }

    // Game Mode

    public static Optional<GameMode> parseGameMode(@NotNull String gameModeName) {
        String normalizedNameA = normalize(stripMinecraftTag(gameModeName));
        for (GameMode gameMode : GameMode.values()) {
            String normalizedNameB = normalize(stripMinecraftTag(gameMode.name()));
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
            String formattedName = kebabCase(gameMode.name());
            names.add(formattedName);
        }
        return names;
    }
    
    // Casing

    @NotNull
    public static String titleCase(@NotNull String str) {
        str = str.replace("_", " ").replace("-", " ").replace(":", " ").replace(".", " ");
        String[] words = str.split(" ");
        String titleCase = "";
        for (String word : words) {
            titleCase += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
        }
        return titleCase.trim();
    }

    @NotNull
    public static String kebabCase(@NotNull String str) {
        str = str.replace("_", " ").replace("-", " ").replace(":", " ").replace(".", " ");
        String[] words = str.split(" ");
        String kebabCase = "";
        for (String word : words) {
            kebabCase += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + "-";
        }
        return kebabCase.trim();
    }

    // Normalization

    @NotNull
    public static String stripMinecraftTag(@NotNull String str) {
        return str.replace("minecraft:", "");
    }

    @NotNull
    public static String normalize(@NotNull String str) {
        return str.toLowerCase()
                  .replace(" ", "")
                  .replace("_", "")
                  .replace("-", "")
                  .replace(".", "")
                  .replace(":", "")
                  .trim();
    }
}