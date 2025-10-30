package fun.sunrisemc.template.scheduled_command;

import java.util.Optional;

public enum CommandType {

    CONSOLE, // Executes the command as the console
    CONSOLE_FOR_EACH_PLAYER, // Executes the command as the console for each online player, replacing {player} with the player's name
    FOR_EACH_PLAYER; // Executes the command as each online player, replacing {player} with the player's name

    public static Optional<CommandType> fromString(String type) {
        type = type.trim();
        for (CommandType commandType : values()) {
            if (commandType.name().equalsIgnoreCase(type)) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        CommandType[] types = values();
        String[] typeStrings = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeStrings[i] = types[i].name();
        }
        return typeStrings;
    }
}