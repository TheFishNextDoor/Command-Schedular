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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.cron.MCCron;
import fun.sunrisemc.command_schedular.utils.ConfigUtils;

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
        "must-be-in-water",
        "must-not-be-in-water"
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

    private @NotNull HashSet<@NotNull String> worlds = new HashSet<>();
    private @NotNull HashSet<@NotNull String> environments = new HashSet<>();
    private @NotNull HashSet<@NotNull String> biomes = new HashSet<>();
    private @NotNull HashSet<@NotNull String> gamemodes = new HashSet<>();

    private @NotNull ArrayList<@NotNull String> hasPermissions = new ArrayList<>();
    private @NotNull ArrayList<@NotNull String> missingPermissions = new ArrayList<>();

    private Optional<Integer> minX = Optional.empty();
    private Optional<Integer> maxX = Optional.empty();
    private Optional<Integer> minY = Optional.empty();
    private Optional<Integer> maxY = Optional.empty();
    private Optional<Integer> minZ = Optional.empty();
    private Optional<Integer> maxZ = Optional.empty();

    private boolean mustBeInWater = false;
    private boolean mustNotBeInWater = false;

    CommandConfiguration(@NotNull YamlConfiguration config, @NotNull String id) {
        this.id = id;

        // Settings Validation

        ConfigurationSection settingsSection = config.getConfigurationSection(id);
        if (settingsSection != null) {
            for (String setting : settingsSection.getKeys(false)) {
                if (!SETTINGS.contains(setting)) {
                    CommandSchedularPlugin.logWarning("Invalid setting for command configuration " + id + ": " + setting + ".");
                    CommandSchedularPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
                }
            }
        }

        if (config.contains(id + ".triggers")) {
            ConfigurationSection triggersSection = config.getConfigurationSection(id + ".triggers");
            if (triggersSection != null) {
                for (String trigger : triggersSection.getKeys(false)) {
                    if (!TRIGGERS.contains(trigger)) {
                        CommandSchedularPlugin.logWarning("Invalid trigger for command configuration " + id + ": " + trigger + ".");
                        CommandSchedularPlugin.logWarning("Valid triggers are: " + String.join(", ", TRIGGERS) + ".");
                    }
                }
            }
        }

        if (config.contains(id + ".execute-conditions")) {
            ConfigurationSection executeConditionsSection = config.getConfigurationSection(id + ".execute-conditions");
            if (executeConditionsSection != null) {
                for (String executeCondition : executeConditionsSection.getKeys(false)) {
                    if (!EXECUTE_CONDITIONS.contains(executeCondition)) {
                        CommandSchedularPlugin.logWarning("Invalid execute condition for command configuration " + id + ": " + executeCondition + ".");
                        CommandSchedularPlugin.logWarning("Valid execute conditions are: " + String.join(", ", EXECUTE_CONDITIONS) + ".");
                    }
                }
            }
        }

        if (config.contains(id + ".player-conditions")) {
            ConfigurationSection playerConditionsSection = config.getConfigurationSection(id + ".player-conditions");
            if (playerConditionsSection != null) {
                for (String condition : playerConditionsSection.getKeys(false)) {
                    if (!PLAYER_CONDITIONS.contains(condition)) {
                        CommandSchedularPlugin.logWarning("Invalid player condition for command configuration " + id + ": " + condition + ".");
                        CommandSchedularPlugin.logWarning("Valid player conditions are: " + String.join(", ", PLAYER_CONDITIONS) + ".");
                    }
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
            this.intervalTicks = Optional.of(ConfigUtils.getIntClamped(config, id + ".triggers.interval-ticks", 1, Integer.MAX_VALUE));
        }

        if (config.contains(id + ".triggers.ticks-from-server-start")) {
            Optional<String> ticksFromServerStartString = ConfigUtils.getString(config, id + ".triggers.ticks-from-server-start");
            if (ticksFromServerStartString.isPresent()) {
                String[] ticksFromServerStartSplit = ticksFromServerStartString.get().split(",");
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
        }

        if (config.contains(id + ".triggers.cron")) {
            Optional<String> cronExpression = ConfigUtils.getString(config, id + ".triggers.cron");
            if (cronExpression.isPresent()) {
                this.cron = Optional.of(new MCCron(cronExpression.get()));
            }
            
        }

        // Load Execute Conditions

        if (config.contains(id + ".execute-conditions.min-players-online")) {
            minPlayersOnlineToExecute = ConfigUtils.getIntClamped(config, id + ".execute-conditions.min-players-online", 0, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".execute-conditions.min-players-who-meet-conditions")) {
            minPlayersWhoMeetConditionsToExecute = ConfigUtils.getIntClamped(config, id + ".execute-conditions.min-players-who-meet-conditions", 0, Integer.MAX_VALUE);
        }

        if (config.contains(id + ".execute-conditions.max-players-who-meet-conditions")) {
            maxPlayersWhoMeetConditionsToExecute = ConfigUtils.getIntClamped(config, id + ".execute-conditions.max-players-who-meet-conditions", 0, Integer.MAX_VALUE);
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

        if (config.contains(id + ".player-conditions.min-x")) {
            this.minX = Optional.of(config.getInt(id + ".player-conditions.min-x"));
        }
        if (config.contains(id + ".player-conditions.max-x")) {
            this.maxX = Optional.of(config.getInt(id + ".player-conditions.max-x"));
        }
        if (config.contains(id + ".player-conditions.min-y")) {
            this.minY = Optional.of(config.getInt(id + ".player-conditions.min-y"));
        }
        if (config.contains(id + ".player-conditions.max-y")) {
            this.maxY = Optional.of(config.getInt(id + ".player-conditions.max-y"));
        }
        if (config.contains(id + ".player-conditions.min-z")) {
            this.minZ = Optional.of(config.getInt(id + ".player-conditions.min-z"));
        }
        if (config.contains(id + ".player-conditions.max-z")) {
            this.maxZ = Optional.of(config.getInt(id + ".player-conditions.max-z"));
        }

        if (config.contains(id + ".player-conditions.must-be-in-water")) {
            this.mustBeInWater = config.getBoolean(id + ".player-conditions.must-be-in-water");
        }
        if (config.contains(id + ".player-conditions.must-not-be-in-water")) {
            this.mustNotBeInWater = config.getBoolean(id + ".player-conditions.must-not-be-in-water");
        }

        this.playerConditionsEnabled = !worlds.isEmpty() || !environments.isEmpty() || !biomes.isEmpty()
            || minX.isPresent() || maxX.isPresent() || minY.isPresent() || maxY.isPresent() || minZ.isPresent() || maxZ.isPresent()
            || mustBeInWater || mustNotBeInWater;
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

        if (mustBeInWater && !player.isInWater()) {
            return false;
        }
        if (mustNotBeInWater && player.isInWater()) {
            return false;
        }

        return true;
    }

    @NotNull
    private String normalizeName(@NotNull String biomeName) {
        return biomeName.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}