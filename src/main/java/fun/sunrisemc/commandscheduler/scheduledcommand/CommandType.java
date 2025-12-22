package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.utils.StringUtils;

public enum CommandType {

    CONSOLE, // Executes the command as the console
    CONSOLE_FOR_EACH_PLAYER, // Executes the command as the console for each online player, replacing {player} with the player's name
    OP_FOR_EACH_PLAYER, // Executes the command as each online player as if they were ops, replacing {player} with the player's name
    FOR_EACH_PLAYER, // Executes the command as each online player, replacing {player} with the player's name
    BROADCAST, // Broadcasts a message to all online players
    MESSAGE; // Sends a message to all online players, replacing {player} with the player's name

    public static Optional<CommandType> parse(@NotNull String commandTypeName) {
        String normalizedNameA = StringUtils.normalize(commandTypeName);
        for (CommandType commandType : values()) {
            String normalizedNameB = StringUtils.normalize(commandType.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        for (CommandType commandType : values()) {
            String formattedName = StringUtils.kebabCase(commandType.name());
            names.add(formattedName);
        }
        return names;
    }
}