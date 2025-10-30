package fun.sunrisemc.template.scheduled_command;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.template.CommandSchedularPlugin;

public class CommandConfiguration {

    private final String id;

    private ArrayList<CommandExecutable> commands = new ArrayList<>();

    private boolean executeNextTick = false;

    private Integer interval = null;

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

        if (config.contains(id + ".triggers.interval")) {
            this.interval = getIntClamped(config, id + ".triggers.interval", 1, Integer.MAX_VALUE);
        }
    }

    public String getId() {
        return id;
    }

    public void execute() {
        this.executeNextTick = false;
        for (CommandExecutable command : commands) {
            command.execute();
        }
    }

    public void executeNextTick() {
        this.executeNextTick = true;
    }

    public boolean shouldRun(int tickCount) {
        return executeNextTick || checkInterval(tickCount);
    }

    private boolean checkInterval(int tickCount) {
        if (interval == null) {
            return false;
        }
        return tickCount % interval == 0;
    }

    private int getIntClamped(@NonNull YamlConfiguration config, @NonNull String path, int min, int max) {
        int value = config.getInt(path);
        return Math.clamp(value, min, max);
    }
}