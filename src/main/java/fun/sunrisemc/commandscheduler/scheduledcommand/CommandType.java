package fun.sunrisemc.commandscheduler.scheduledcommand;

public enum CommandType {

    CONSOLE, // Executes the command as the console
    CONSOLE_FOR_EACH_PLAYER, // Executes the command as the console for each online player, replacing {player} with the player's name
    FOR_EACH_PLAYER, // Executes the command as each online player, replacing {player} with the player's name
    BROADCAST, // Broadcasts a message to all online players
    MESSAGE; // Sends a message to all online players, replacing {player} with the player's name

}