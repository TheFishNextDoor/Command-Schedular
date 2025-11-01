package fun.sunrisemc.command_schedular.scheduled_command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.cron.MCCron;
import fun.sunrisemc.command_schedular.file.ConfigFile;

public class CommandConfiguration {

    private final List<String> SETTINGS = List.of(
        "commands",
        "triggers",
        "only-run-one-random-command"
    );

    private final List<String> TRIGGERS = List.of(
        "interval-ticks",
        "cron",
        "ticks-from-server-start"
    );

    private final String id;

    // Commands

    private ArrayList<CommandExecutable> commands = new ArrayList<>();

    // Behavior

    private boolean onlyRunOneRandomCommand = false;

    // Triggers

    private Integer intervalTicks = null;

    private HashSet<Integer> ticksFromServerStart = new HashSet<>();

    private MCCron cron = null;

    CommandConfiguration(@NonNull YamlConfiguration config, @NonNull String id) {
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

        // Load Behavior

        if (config.contains(id + ".only-run-one-random-command")) {
            onlyRunOneRandomCommand = config.getBoolean(id + ".only-run-one-random-command");
        }

        // Load Triggers

        if (config.contains(id + ".triggers.interval-ticks")) {
            this.intervalTicks = ConfigFile.getIntClamped(config, id + ".triggers.interval-ticks", 1, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".triggers.ticks-from-server-start")) {
            String ticksFromServerStartString = config.getString(id + ".triggers.ticks-from-server-start");
            String[] ticksFromServerStartSplit = ticksFromServerStartString.split(",");
            for (String tickString : ticksFromServerStartSplit) {
                int tick;
                try {
                    tick = Integer.parseInt(tickString.trim());
                    ticksFromServerStart.add(tick);
                } 
                catch (NumberFormatException e) {
                    CommandSchedularPlugin.logWarning("Command configuration " + id + " has an invalid ticks from server start: " + tickString);
                    continue;
                }
            }
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
        if (onlyRunOneRandomCommand) {
            if (commands.isEmpty()) {
                return;
            }

            int randomIndex = (int) (Math.random() * commands.size());
            commands.get(randomIndex).execute();
        }
        else {
            for (CommandExecutable command : commands) {
                command.execute();
            }
        }
    }

    // Tick Check

    public boolean shouldRunFromTick(int tickCount) {
        return checkInterval(tickCount) || checkTicksFromServerStart(tickCount);
    }

    private boolean checkInterval(int tickCount) {
        if (intervalTicks == null) {
            return false;
        }
        return tickCount % intervalTicks == 0;
    }

    private boolean checkTicksFromServerStart(int tickCount) {
        if (ticksFromServerStart.isEmpty()) {
            return false;
        }
        return ticksFromServerStart.contains(tickCount);
    }

    // Cron Check

    public boolean shouldRunFromCron(LocalDateTime dateTime) {
        if (cron == null) {
            return false;
        }

        int second = dateTime.getSecond();
        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();
        int dayOfMonth = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        int year = dateTime.getYear();

        return cron.matches(second, minute, hour, dayOfMonth, month, dayOfWeek, year);
    }
}