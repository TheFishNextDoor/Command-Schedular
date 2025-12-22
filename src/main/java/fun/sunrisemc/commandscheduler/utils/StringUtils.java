package fun.sunrisemc.commandscheduler.utils;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

/**
 * String Utils Class Version 1.0.0
 */
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
        str = str.toLowerCase().replace("_", " ").replace("-", " ").replace(":", " ").replace(".", " ").trim();
        String[] words = str.split(" ");
        String kebabCase = "";
        for (String word : words) {
            kebabCase += word + "-";
        }
        return kebabCase.substring(0, kebabCase.length() - 1);
    }

    // Normalization

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