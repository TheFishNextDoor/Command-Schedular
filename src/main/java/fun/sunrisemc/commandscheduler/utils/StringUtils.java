package fun.sunrisemc.commandscheduler.utils;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

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