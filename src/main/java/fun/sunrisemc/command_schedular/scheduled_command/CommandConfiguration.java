package fun.sunrisemc.command_schedular.scheduled_command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.cron.MCCron;

public class CommandConfiguration {

    private final List<String> SETTINGS = List.of(
        "commands",
        "triggers"
    );

    private final List<String> TRIGGERS = List.of(
        "interval-ticks",
        "cron"
    );

    private final String id;

    private ArrayList<CommandExecutable> commands = new ArrayList<>();

    private boolean executeNextTick = false;

    private Integer intervalTicks = null;

    private MCCron cron = null;

    CommandConfiguration(YamlConfiguration config, String id) {
        this.id = id;

        // Settings Validation

        for (String setting : config.getConfigurationSection(id).getKeys(false)) {
            if (!SETTINGS.contains(setting)) {
                CommandSchedularPlugin.logWarning("Invalid setting for command configuration " + id + ": " + setting + ".");
                CommandSchedularPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
            }
        }

        if (config.contains(id + ".triggers")) {
            for (String trigger : config.getConfigurationSection(id + ".triggers").getKeys(false)) {
                if (!TRIGGERS.contains(trigger)) {
                    CommandSchedularPlugin.logWarning("Invalid trigger for command configuration " + id + ": " + trigger + ".");
                    CommandSchedularPlugin.logWarning("Valid triggers are: " + String.join(", ", TRIGGERS) + ".");
                }
            }
        }

        // Load Commands

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

        // Load Triggers

        if (config.contains(id + ".triggers.interval-ticks")) {
            this.intervalTicks = getIntClamped(config, id + ".triggers.interval-ticks", 1, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".triggers.cron")) {
            String cronExpression = config.getString(id + ".triggers.cron");
            this.cron = new MCCron(cronExpression);
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
        return executeNextTick || checkInterval(tickCount) || checkCron(tickCount);
    }

    private boolean checkInterval(int tickCount) {
        if (intervalTicks == null) {
            return false;
        }
        return tickCount % intervalTicks == 0;
    }

    private boolean checkCron(int tickCount) {
        if (cron == null) {
            return false;
        }

        int tick = tickCount % 20;

        LocalDateTime dateTime = LocalDateTime.now();
        int second = dateTime.getSecond();
        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();
        int dayOfMonth = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int dayOfWeek = dateTime.getDayOfWeek().getValue();

        return cron.matches(tick, second, minute, hour, dayOfMonth, month, dayOfWeek);
    }

    private int getIntClamped(@NonNull YamlConfiguration config, @NonNull String path, int min, int max) {
        int value = config.getInt(path);
        return Math.clamp(value, min, max);
    }
}