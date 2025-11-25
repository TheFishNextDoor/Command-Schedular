package fun.sunrisemc.commandscheduler.scheduledcommand;

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

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.cron.MCCron;
import fun.sunrisemc.commandscheduler.utils.StringUtils;
import fun.sunrisemc.commandscheduler.utils.YAMLUtils;

public class CommandConfiguration {

    private final @NotNull List<String> SETTINGS = List.of(
        "commands",
        "triggers",
        "execute-conditions",
        "player-conditions",
        "only-run-one-random-command"
    );

    private final @NotNull List<String> TRIGGERS = List.of(
        "interval-ticks",
        "cron",
        "ticks-from-server-start"
    );

    private final @NotNull List<String> EXECUTE_CONDITIONS = List.of(
        "min-players-online",
        "min-players-who-meet-conditions",
        "max-players-who-meet-conditions",
        "all-players-meet-conditions"
    );

    private final @NotNull List<String> PLAYER_CONDITIONS = List.of(
        "worlds",
        "environments",
        "biomes",
        "gamemodes",
        "has-permissions",
        "missing-permissions",
        "min-x",
        "min-y",
        "min-z",
        "max-x",
        "max-y",
        "max-z",
        "in-water"
    );

    private final @NotNull String id;

    // Commands

    private @NotNull ArrayList<@NotNull CommandExecutable> commands = new ArrayList<>();

    // Behavior

    private boolean onlyRunOneRandomCommand = false;

    // Triggers

    private Optional<Integer> intervalTicks = Optional.empty();

    private @NotNull HashSet<Integer> ticksFromServerStart = new HashSet<>();

    private Optional<MCCron> cron = Optional.empty();

    // Execute Conditions

    private int minPlayersOnlineToExecute = 0;

    private int minPlayersWhoMeetConditionsToExecute = 0;
    private int maxPlayersWhoMeetConditionsToExecute = Integer.MAX_VALUE;

    private boolean onlyExecuteIfAllPlayersMeetConditions = false;

    // Player Conditions

    private boolean playerConditionsEnabled = false;

    private @NotNull HashSet<String> worlds = new HashSet<>();
    private @NotNull HashSet<String> environments = new HashSet<>();
    private @NotNull HashSet<String> biomes = new HashSet<>();
    private @NotNull HashSet<String> gamemodes = new HashSet<>();

    private @NotNull ArrayList<String> hasPermissions = new ArrayList<>();
    private @NotNull ArrayList<String> missingPermissions = new ArrayList<>();

    private Optional<Integer> minX = Optional.empty();
    private Optional<Integer> maxX = Optional.empty();
    private Optional<Integer> minY = Optional.empty();
    private Optional<Integer> maxY = Optional.empty();
    private Optional<Integer> minZ = Optional.empty();
    private Optional<Integer> maxZ = Optional.empty();

    private Optional<Boolean> inWater = Optional.empty();

    CommandConfiguration(@NotNull YamlConfiguration config, @NotNull String id) {
        this.id = id;

        // Settings Validation

        for (String setting : YAMLUtils.getKeys(config, id)) {
            if (!SETTINGS.contains(setting)) {
                CommandSchedulerPlugin.logWarning("Invalid setting for command configuration " + id + ": " + setting + ".");
                CommandSchedulerPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
            }
        }

        for (String trigger : YAMLUtils.getKeys(config, id + ".triggers")) {
            if (!TRIGGERS.contains(trigger)) {
                CommandSchedulerPlugin.logWarning("Invalid trigger for command configuration " + id + ": " + trigger + ".");
                CommandSchedulerPlugin.logWarning("Valid triggers are: " + String.join(", ", TRIGGERS) + ".");
            }
        }

        for (String executeCondition : YAMLUtils.getKeys(config, id + ".execute-conditions")) {
            if (!EXECUTE_CONDITIONS.contains(executeCondition)) {
                CommandSchedulerPlugin.logWarning("Invalid execute condition for command configuration " + id + ": " + executeCondition + ".");
                CommandSchedulerPlugin.logWarning("Valid execute conditions are: " + String.join(", ", EXECUTE_CONDITIONS) + ".");
            }
        }

        for (String condition : YAMLUtils.getKeys(config, id + ".player-conditions")) {
            if (!PLAYER_CONDITIONS.contains(condition)) {
                CommandSchedulerPlugin.logWarning("Invalid player condition for command configuration " + id + ": " + condition + ".");
                CommandSchedulerPlugin.logWarning("Valid player conditions are: " + String.join(", ", PLAYER_CONDITIONS) + ".");
            }
        }

        // Load Commands

        for (String commandString : config.getStringList(id + ".commands")) {
            String[] commandStringSplit = commandString.split(":", 2);
            if (commandStringSplit.length != 2) {
                CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid command syntax for command: " + commandString);
                CommandSchedulerPlugin.logWarning("Correct syntax is <commandType>:<command>");
                continue;
            }

            Optional<CommandType> commandType = StringUtils.parseCommandType(commandStringSplit[0]);
            if (commandType.isEmpty()) {
                CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid command type for command: " + commandString);
                CommandSchedulerPlugin.logWarning("Valid command types are: " + String.join(", ", getCommandTypeNames()));
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
            this.intervalTicks = Optional.of(YAMLUtils.getIntClamped(config, id + ".triggers.interval-ticks", 1, Integer.MAX_VALUE, 1));
        }

        if (config.contains(id + ".triggers.ticks-from-server-start")) {
            Optional<String> ticksFromServerStartString = YAMLUtils.getString(config, id + ".triggers.ticks-from-server-start");
            if (ticksFromServerStartString.isPresent()) {
                String[] ticksFromServerStartSplit = ticksFromServerStartString.get().split(",");
                for (String tickString : ticksFromServerStartSplit) {
                    Optional<Integer> tick = StringUtils.parseInteger(tickString);
                    if (tick.isEmpty()) {
                        CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid ticks from server start: " + tickString);
                        continue;
                    }
                    
                    this.ticksFromServerStart.add(tick.get());
                }
            }
        }

        if (config.contains(id + ".triggers.cron")) {
            Optional<String> cronExpression = YAMLUtils.getString(config, id + ".triggers.cron");
            if (cronExpression.isPresent()) {
                this.cron = Optional.of(new MCCron(cronExpression.get()));
            }
            
        }

        // Load Execute Conditions

        if (config.contains(id + ".execute-conditions.min-players-online")) {
            minPlayersOnlineToExecute = YAMLUtils.getIntClamped(config, id + ".execute-conditions.min-players-online", 0, Integer.MAX_VALUE, 0);
        }

        if (config.contains(id + ".execute-conditions.min-players-who-meet-conditions")) {
            minPlayersWhoMeetConditionsToExecute = YAMLUtils.getIntClamped(config, id + ".execute-conditions.min-players-who-meet-conditions", 0, Integer.MAX_VALUE, 0);
        }

        if (config.contains(id + ".execute-conditions.max-players-who-meet-conditions")) {
            maxPlayersWhoMeetConditionsToExecute = YAMLUtils.getIntClamped(config, id + ".execute-conditions.max-players-who-meet-conditions", 0, Integer.MAX_VALUE, 0);
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

        for (String permission : config.getStringList(id + ".player-conditions.has-permissions")) {
            this.hasPermissions.add(permission);
        }

        for (String permission : config.getStringList(id + ".player-conditions.missing-permissions")) {
            this.missingPermissions.add(permission);
        }

        this.minX = YAMLUtils.getInt(config, id + ".player-conditions.min-x");
        this.maxX = YAMLUtils.getInt(config, id + ".player-conditions.max-x");
        this.minY = YAMLUtils.getInt(config, id + ".player-conditions.min-y");
        this.maxY = YAMLUtils.getInt(config, id + ".player-conditions.max-y");
        this.minZ = YAMLUtils.getInt(config, id + ".player-conditions.min-z");
        this.maxZ = YAMLUtils.getInt(config, id + ".player-conditions.max-z");

        this.inWater = YAMLUtils.getBoolean(config, id + ".player-conditions.in-water");

        this.playerConditionsEnabled = !worlds.isEmpty() || !environments.isEmpty() || !biomes.isEmpty()
            || minX.isPresent() || maxX.isPresent() || minY.isPresent() || maxY.isPresent() || minZ.isPresent() || maxZ.isPresent()
            || inWater.isPresent();
    }

    @NotNull
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
        if (intervalTicks.isEmpty()) {
            return false;
        }
        return tickCount % intervalTicks.get() == 0;
    }

    private boolean checkTicksFromServerStart(int tickCount) {
        if (ticksFromServerStart.isEmpty()) {
            return false;
        }
        return ticksFromServerStart.contains(tickCount);
    }

    // Cron Check

    public boolean shouldRunFromCron(@NotNull LocalDateTime dateTime) {
        if (cron.isEmpty()) {
            return false;
        }

        int second = dateTime.getSecond();
        int minute = dateTime.getMinute();
        int hour = dateTime.getHour();
        int dayOfMonth = dateTime.getDayOfMonth();
        int month = dateTime.getMonthValue();
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        int year = dateTime.getYear();

        return cron.get().matches(second, minute, hour, dayOfMonth, month, dayOfWeek, year);
    }

    // Player Condition Checks

    @NotNull
    private Collection<Player> getPlayersWhoMeetConditions() {
        HashSet<Player> playersWhoMeetConditions = new HashSet<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        for (Player player : onlinePlayers) {
            if (player == null) {
                continue;
            }
            if (conditionsMet(player)) {
                playersWhoMeetConditions.add(player);
            }
        }

        return playersWhoMeetConditions;
    }

    private boolean conditionsMet(@NotNull Player player) {
        Location location = player.getLocation();
        Block block = location.getBlock();
        
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

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

        for (String permission : hasPermissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }

        for (String permission : missingPermissions) {
            if (player.hasPermission(permission)) {
                return false;
            }
        }

        if (minX.isPresent() && location.getBlockX() < minX.get()) {
            return false;
        }
        if (minY.isPresent() && location.getBlockY() < minY.get()) {
            return false;
        }
        if (minZ.isPresent() && location.getBlockZ() < minZ.get()) {
            return false;
        }
        if (maxX.isPresent() && location.getBlockX() > maxX.get()) {
            return false;
        }
        if (maxY.isPresent() && location.getBlockY() > maxY.get()) {
            return false;
        }
        if (maxZ.isPresent() && location.getBlockZ() > maxZ.get()) {
            return false;
        }

        if (inWater.isPresent()) {
            if (inWater.get() && !player.isInWater()) {
                return false;
            }
            if (!inWater.get() && player.isInWater()) {
                return false;
            }
        }

        return true;
    }

    @NotNull
    private String normalizeName(@NotNull String biomeName) {
        return biomeName.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }

    private static ArrayList<String> getCommandTypeNames() {
        ArrayList<String> names = new ArrayList<>();
        for (CommandType commandType : CommandType.values()) {
            String formattedName = StringUtils.formatName(commandType.name());
            names.add(formattedName);
        }
        return names;
    }
}