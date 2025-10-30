package fun.sunrisemc.command_schedular.scheduled_command;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum CommandType {

    CONSOLE, // Executes the command as the console
    CONSOLE_FOR_EACH_PLAYER, // Executes the command as the console for each online player, replacing {player} with the player's name
    FOR_EACH_PLAYER, // Executes the command as each online player, replacing {player} with the player's name
    BROADCAST, // Broadcasts a message to all players
    MESSAGE; // Sends a message to all players

    public static Optional<CommandType> fromString(@NonNull String type) {
        type = type.trim().replace("-", "_").replace(" ", "_");
        for (CommandType commandType : values()) {
            if (type.equalsIgnoreCase(commandType.name())) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        CommandType[] types = values();
        String[] typeStrings = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeStrings[i] = types[i].name().replace("_", "-").toLowerCase();
        }
        return typeStrings;
    }
}