package fun.sunrisemc.template.scheduled_command;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;

import fun.sunrisemc.template.CommandSchedularPlugin;

public class CommandConfiguration {

    private final String id;

    private ArrayList<CommandExecutable> commands = new ArrayList<>();

    CommandConfiguration(YamlConfiguration config, String id) {
        this.id = id;

        for (String commandString : config.getStringList(id + ".commands")) {
            String[] commandStringSplit = commandString.split(":", 2);
            if (commandStringSplit.length != 2) {
                CommandSchedularPlugin.logWarning("Command configuration " + id + " has an invalid command syntax for command: " + commandString);
                CommandSchedularPlugin.logWarning("Correct syntax is <commandType>:<command>");
                continue;
            }

            Optional<CommandType> commandType = CommandType.fromString(commandStringSplit[0]);
            if (commandType.isEmpty()) {
                CommandSchedularPlugin.logWarning("Command configuration " + id + " has an invalid command type for command: " + commandString);
                CommandSchedularPlugin.logWarning("Valid command types are: " + String.join(", ", CommandType.getNames()));
                continue;
            }

            commands.add(new CommandExecutable(commandType.get(), commandStringSplit[1]));
        }
    }

    public String getId() {
        return id;
    }

    public void execute() {
        for (CommandExecutable command : commands) {
            command.execute();
        }
    }
}