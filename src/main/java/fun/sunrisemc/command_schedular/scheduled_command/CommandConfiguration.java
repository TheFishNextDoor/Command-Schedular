package fun.sunrisemc.command_schedular.scheduled_command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.cron.MCCron;
import fun.sunrisemc.command_schedular.file.ConfigFile;

public class CommandConfiguration {

    private final List<String> SETTINGS = List.of(
        "commands",
        "triggers",
        "execute-conditions",
        "player-conditions",
        "only-run-one-random-command"
    );

    private final List<String> TRIGGERS = List.of(
        "interval-ticks",
        "cron",
        "ticks-from-server-start"
    );

    private final List<String> EXECUTE_CONDITIONS = List.of(
        "min-players-online",
        "atleast-one-player-meets-conditions",
        "all-players-meet-conditions",
        "no-players-meet-conditions"
    );

    private final List<String> PLAYER_CONDITIONS = List.of(
        "worlds",
        "environments",
        "biomes",
        "gamemodes",
        "min-x",
        "min-y",
        "min-z",
        "max-x",
        "max-y",
        "max-z",
        "must-be-in-water",
        "must-not-be-in-water"
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

    // Execute Conditions

    private int minPlayersOnlineToExecute = 0;

    private int minPlayersWhoMeetConditionsToExecute = 0;
    private int maxPlayersWhoMeetConditionsToExecute = Integer.MAX_VALUE;

    private boolean onlyExecuteIfAllPlayersMeetConditions = false;

    // Player Conditions

    private boolean playerConditionsEnabled = false;

    private HashSet<String> worlds = new HashSet<>();
    private HashSet<String> environments = new HashSet<>();
    private HashSet<String> biomes = new HashSet<>();
    private HashSet<String> gamemodes = new HashSet<>();

    private Integer minX = null;
    private Integer maxX = null;
    private Integer minY = null;
    private Integer maxY = null;
    private Integer minZ = null;
    private Integer maxZ = null;

    private boolean mustBeInWater = false;
    private boolean mustNotBeInWater = false;

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

        if (config.contains(id + ".execute-conditions")) {
            for (String executeCondition : config.getConfigurationSection(id + ".execute-conditions").getKeys(false)) {
                if (!EXECUTE_CONDITIONS.contains(executeCondition)) {
                    CommandSchedularPlugin.logWarning("Invalid execute condition for command configuration " + id + ": " + executeCondition + ".");
                    CommandSchedularPlugin.logWarning("Valid execute conditions are: " + String.join(", ", EXECUTE_CONDITIONS) + ".");
                }
            }
        }

        if (config.contains(id + ".player-conditions")) {
            for (String condition : config.getConfigurationSection(id + ".player-conditions").getKeys(false)) {
                if (!PLAYER_CONDITIONS.contains(condition)) {
                    CommandSchedularPlugin.logWarning("Invalid player condition for command configuration " + id + ": " + condition + ".");
                    CommandSchedularPlugin.logWarning("Valid player conditions are: " + String.join(", ", PLAYER_CONDITIONS) + ".");
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

        // Load Execute Conditions

        if (config.contains(id + ".execute-conditions.min-players-online")) {
            minPlayersOnlineToExecute = ConfigFile.getIntClamped(config, id + ".execute-conditions.min-players-online", 0, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".execute-conditions.min-players-who-meet-conditions")) {
            minPlayersWhoMeetConditionsToExecute = ConfigFile.getIntClamped(config, id + ".execute-conditions.min-players-who-meet-conditions", 0, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".execute-conditions.max-players-who-meet-conditions")) {
            maxPlayersWhoMeetConditionsToExecute = ConfigFile.getIntClamped(config, id + ".execute-conditions.max-players-who-meet-conditions", 0, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".execute-conditions.all-players-meet-conditions")) {
            onlyExecuteIfAllPlayersMeetConditions = config.getBoolean(id + ".execute-conditions.all-players-meet-conditions");
        }

        // Load Player Conditions

        for (String worldName : config.getStringList(id + ".player-conditions.worlds")) {
            this.worlds.add(worldName);
        }

        for (String environmentName : config.getStringList(id + ".player-conditions.environments")) {
            this.environments.add(environmentName);
        }

        for (String biomeName : config.getStringList(id + ".player-conditions.biomes")) {
            this.biomes.add(normalizeName(biomeName));
        }

        for (String gamemodeName : config.getStringList(id + ".player-conditions.gamemodes")) {
            this.gamemodes.add(normalizeName(gamemodeName));
        }

        if (config.contains(id + ".player-conditions.min-x")) {
            this.minX = config.getInt(id + ".player-conditions.min-x");
        }
        if (config.contains(id + ".player-conditions.max-x")) {
            this.maxX = config.getInt(id + ".player-conditions.max-x");
        }
        if (config.contains(id + ".player-conditions.min-y")) {
            this.minY = config.getInt(id + ".player-conditions.min-y");
        }
        if (config.contains(id + ".player-conditions.max-y")) {
            this.maxY = config.getInt(id + ".player-conditions.max-y");
        }
        if (config.contains(id + ".player-conditions.min-z")) {
            this.minZ = config.getInt(id + ".player-conditions.min-z");
        }
        if (config.contains(id + ".player-conditions.max-z")) {
            this.maxZ = config.getInt(id + ".player-conditions.max-z");
        }

        if (config.contains(id + ".player-conditions.must-be-in-water")) {
            this.mustBeInWater = config.getBoolean(id + ".player-conditions.must-be-in-water");
        }
        if (config.contains(id + ".player-conditions.must-not-be-in-water")) {
            this.mustNotBeInWater = config.getBoolean(id + ".player-conditions.must-not-be-in-water");
        }

        this.playerConditionsEnabled = !worlds.isEmpty() || !environments.isEmpty() || !biomes.isEmpty()
            || minX != null || maxX != null || minY != null || maxY != null || minZ != null || maxZ != null
            || mustBeInWater || mustNotBeInWater;
    }

    public String getId() {
        return id;
    }

    public void execute() {
        // Execute Conditions Check
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        if (minPlayersOnlineToExecute >= onlinePlayers.size()) {
            return;
        }

        Collection<? extends Player> playersWhoMeetConditions = playerConditionsEnabled ? getPlayersWhoMeetConditions() : onlinePlayers;
        if (minPlayersWhoMeetConditionsToExecute >= playersWhoMeetConditions.size()) {
            return;
        }
        if (maxPlayersWhoMeetConditionsToExecute <= playersWhoMeetConditions.size()) {
            return;
        }
        if (onlyExecuteIfAllPlayersMeetConditions && playersWhoMeetConditions.size() != onlinePlayers.size()) {
            return;
        }

        // Execute Commands
        if (onlyRunOneRandomCommand) {
            if (commands.isEmpty()) {
                return;
            }

            int randomIndex = (int) (Math.random() * commands.size());
            commands.get(randomIndex).execute(playersWhoMeetConditions);
        }
        else {
            for (CommandExecutable command : commands) {
                command.execute(playersWhoMeetConditions);
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

    public boolean shouldRunFromCron(@NonNull LocalDateTime dateTime) {
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

    // Player Condition Checks

    private Collection<Player> getPlayersWhoMeetConditions() {
        HashSet<Player> playersWhoMeetConditions = new HashSet<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (conditionsMet(player)) {
                playersWhoMeetConditions.add(player);
            }
        }

        return playersWhoMeetConditions;
    }

    private boolean conditionsMet(@NonNull Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Block block = location.getBlock();

        if (!worlds.isEmpty() && !worlds.contains(world.getName())) {
            return false;
        }

        if (!environments.isEmpty() && !environments.contains(world.getEnvironment().name())) {
            return false;
        }

        if (!biomes.isEmpty() && !biomes.contains(normalizeName(block.getBiome().name()))) {
            return false;
        }

        if (!gamemodes.isEmpty() && !gamemodes.contains(normalizeName(player.getGameMode().name()))) {
            return false;
        }

        if (minX != null && location.getBlockX() < minX) {
            return false;
        }
        if (minY != null && location.getBlockY() < minY) {
            return false;
        }
        if (minZ != null && location.getBlockZ() < minZ) {
            return false;
        }
        if (maxX != null && location.getBlockX() > maxX) {
            return false;
        }
        if (maxY != null && location.getBlockY() > maxY) {
            return false;
        }
        if (maxZ != null && location.getBlockZ() > maxZ) {
            return false;
        }

        if (mustBeInWater && !player.isInWater()) {
            return false;
        }
        if (mustNotBeInWater && player.isInWater()) {
            return false;
        }

        return true;
    }

    private String normalizeName(@NonNull String biomeName) {
        return biomeName.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}