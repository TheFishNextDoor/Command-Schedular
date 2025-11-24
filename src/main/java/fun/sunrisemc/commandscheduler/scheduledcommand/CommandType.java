package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public enum CommandType {

    CONSOLE, // Executes the command as the console
    CONSOLE_FOR_EACH_PLAYER, // Executes the command as the console for each online player, replacing {player} with the player's name
    FOR_EACH_PLAYER, // Executes the command as each online player, replacing {player} with the player's name
    BROADCAST, // Broadcasts a message to all online players
    MESSAGE; // Sends a message to all online players, replacing {player} with the player's name

    public static Optional<CommandType> fromString(@NotNull String type) {
        type = type.trim().replace("-", "_").replace(" ", "_");
        for (CommandType commandType : values()) {
            if (type.equalsIgnoreCase(commandType.name())) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static String[] getNames() {
        CommandType[] types = values();
        String[] typeStrings = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeStrings[i] = types[i].name().replace("_", "-").toLowerCase();
        }
        return typeStrings;
    }
}