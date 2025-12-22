package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.cron.Cron;
import fun.sunrisemc.commandscheduler.file.ConfigFile;
import fun.sunrisemc.commandscheduler.player.PlayerProfile;
import fun.sunrisemc.commandscheduler.player.PlayerProfileManager;
import fun.sunrisemc.commandscheduler.utils.StringUtils;

public class CommandConfiguration {

    // Settings Lists

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
        "execute-on",
        "all-players-meet-conditions",
        "min-players-online",
        "min-players-who-meet-conditions",
        "max-players-who-meet-conditions"
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
        "in-water",
        "sneaking",
        "blocking",
        "climbing",
        "gliding",
        "glowing",
        "riptiding",
        "in-vehicle",
        "sprinting",
        "flying",
        "on-fire",
        "frozen"
    );

    // Identification

    private final @NotNull String id;

    // Commands

    private @NotNull ArrayList<@NotNull CommandExecutable> commands = new ArrayList<>();

    // Behavior

    private boolean onlyRunOneRandomCommand = false;

    // Triggers

    private Optional<Integer> intervalTicks = Optional.empty();

    private @NotNull HashSet<Integer> ticksFromServerStart = new HashSet<>();

    private Optional<Cron> cron = Optional.empty();

    private @NotNull HashSet<EventType> events = new HashSet<>();

    // Execute Conditions

    private ExecuteOn executeOn = ExecuteOn.CONDITIONS_PASS;

    private boolean onlyExecuteIfAllPlayersMeetConditions = false;

    private int minPlayersOnlineToExecute;
    private int maxPlayersOnlineToExecute;

    private int minPlayersWhoMeetConditionsToExecute;
    private int maxPlayersWhoMeetConditionsToExecute;

    // Player Conditions

    private boolean playerConditionsEnabled = false;

    private @NotNull HashSet<String> worlds = new HashSet<>();
    private @NotNull HashSet<Environment> environments = new HashSet<>();
    private @NotNull HashSet<Biome> biomes = new HashSet<>();
    private @NotNull HashSet<GameMode> gamemodes = new HashSet<>();

    private @NotNull ArrayList<String> hasPermissions = new ArrayList<>();
    private @NotNull ArrayList<String> missingPermissions = new ArrayList<>();

    private Optional<Integer> minX = Optional.empty();
    private Optional<Integer> maxX = Optional.empty();
    private Optional<Integer> minY = Optional.empty();
    private Optional<Integer> maxY = Optional.empty();
    private Optional<Integer> minZ = Optional.empty();
    private Optional<Integer> maxZ = Optional.empty();

    private Optional<Boolean> inWater = Optional.empty();

    private Optional<Boolean> sneaking = Optional.empty();

    private Optional<Boolean> blocking = Optional.empty();

    private Optional<Boolean> climbing = Optional.empty();

    private Optional<Boolean> gliding = Optional.empty();

    private Optional<Boolean> glowing = Optional.empty();

    private Optional<Boolean> riptiding = Optional.empty();

    private Optional<Boolean> inVehicle = Optional.empty();

    private Optional<Boolean> sprinting = Optional.empty();

    private Optional<Boolean> flying = Optional.empty();

    private Optional<Boolean> onFire = Optional.empty();

    private Optional<Boolean> frozen = Optional.empty();

    protected CommandConfiguration(@NotNull ConfigFile config, @NotNull String id) {
        this.id = id;

        // Settings Validation

        for (String setting : config.getKeys(id)) {
            if (!SETTINGS.contains(setting)) {
                CommandSchedulerPlugin.logWarning("Invalid setting for command configuration " + id + ": " + setting + ".");
                CommandSchedulerPlugin.logWarning("Valid settings are: " + String.join(", ", SETTINGS) + ".");
            }
        }

        for (String trigger : config.getKeys(id + ".triggers")) {
            if (!TRIGGERS.contains(trigger)) {
                CommandSchedulerPlugin.logWarning("Invalid trigger for command configuration " + id + ": " + trigger + ".");
                CommandSchedulerPlugin.logWarning("Valid triggers are: " + String.join(", ", TRIGGERS) + ".");
            }
        }

        for (String executeCondition : config.getKeys(id + ".execute-conditions")) {
            if (!EXECUTE_CONDITIONS.contains(executeCondition)) {
                CommandSchedulerPlugin.logWarning("Invalid execute condition for command configuration " + id + ": " + executeCondition + ".");
                CommandSchedulerPlugin.logWarning("Valid execute conditions are: " + String.join(", ", EXECUTE_CONDITIONS) + ".");
            }
        }

        for (String condition : config.getKeys(id + ".player-conditions")) {
            if (!PLAYER_CONDITIONS.contains(condition)) {
                CommandSchedulerPlugin.logWarning("Invalid player condition for command configuration " + id + ": " + condition + ".");
                CommandSchedulerPlugin.logWarning("Valid player conditions are: " + String.join(", ", PLAYER_CONDITIONS) + ".");
            }
        }

        // Load Commands

        Optional<List<String>> commandsInput = config.getStringList(id + ".commands");
        if (commandsInput.isPresent()) {
            for (String commandString : commandsInput.get()) {
                String[] commandStringSplit = commandString.split(":", 2);
                if (commandStringSplit.length != 2) {
                    CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid command syntax for command: " + commandString);
                    CommandSchedulerPlugin.logWarning("Correct syntax is <commandType>:<command>");
                    continue;
                }

                Optional<CommandType> commandType = StringUtils.parseCommandType(commandStringSplit[0]);
                if (commandType.isEmpty()) {
                    CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid command type for command: " + commandString);
                    CommandSchedulerPlugin.logWarning("Valid command types are: " + String.join(", ", StringUtils.getCommandTypeNames()));
                    continue;
                }

                this.commands.add(new CommandExecutable(commandType.get(), commandStringSplit[1]));
            }
        }

        // Load Behavior

        this.onlyRunOneRandomCommand = config.getBoolean(id + ".only-run-one-random-command").orElse(this.onlyRunOneRandomCommand);

        // Load Triggers

        this.intervalTicks = config.getIntClamped(id + ".triggers.interval-ticks", 1, Integer.MAX_VALUE);

        Optional<String> ticksFromServerStartString = config.getString(id + ".triggers.ticks-from-server-start");
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

        Optional<String> cronExpression = config.getString(id + ".triggers.cron");
        if (cronExpression.isPresent()) {
            this.cron = Optional.of(new Cron(cronExpression.get()));
        }

        // Load Execute Conditions

        Optional<String> executeOnString = config.getString(id + ".execute-conditions.execute-on");
        if (executeOnString.isPresent()) {
            Optional<ExecuteOn> executeOnInput = StringUtils.parseExecuteOn(executeOnString.get());
            if (executeOnInput.isEmpty()) {
                CommandSchedulerPlugin.logWarning("Command configuration " + id + " has an invalid execute-on value: " + executeOnString.get());
                CommandSchedulerPlugin.logWarning("Valid execute-on values are: " + String.join(", ", StringUtils.getExecuteOnNames()) + ".");
            }
            else {
                this.executeOn = executeOnInput.get();
            }
        }

        this.onlyExecuteIfAllPlayersMeetConditions = config.getBoolean(id + ".execute-conditions.all-players-meet-conditions").orElse(this.onlyExecuteIfAllPlayersMeetConditions);

        this.minPlayersOnlineToExecute = config.getIntClamped(id + ".execute-conditions.min-players-online", 0, Integer.MAX_VALUE).orElse(0);
        this.maxPlayersOnlineToExecute = config.getIntClamped(id + ".execute-conditions.max-players-online", 0, Integer.MAX_VALUE).orElse(Integer.MAX_VALUE);

        this.minPlayersWhoMeetConditionsToExecute = config.getIntClamped(id + ".execute-conditions.min-players-who-meet-conditions", 0, Integer.MAX_VALUE).orElse(0);
        this.maxPlayersWhoMeetConditionsToExecute = config.getIntClamped(id + ".execute-conditions.max-players-who-meet-conditions", 0, Integer.MAX_VALUE).orElse(Integer.MAX_VALUE);

        // Load Player Conditions

        for (String worldName : config.getStringList(id + ".player-conditions.worlds").orElse(new ArrayList<>())) {
            this.worlds.add(StringUtils.normalize(worldName));
        }

        for (String environmentName : config.getStringList(id + ".player-conditions.environments").orElse(new ArrayList<>())) {
            Optional<Environment> environment = StringUtils.parseEnvironment(environmentName);
            if (environment.isEmpty()) {
                CommandSchedulerPlugin.logWarning("Invalid environment " + environmentName + " in conditional effect " + id + ".");
                CommandSchedulerPlugin.logWarning("Valid environments are: " + String.join(", ", StringUtils.getEnvironmentNames()) + ".");
                continue;
            }
            this.environments.add(environment.get());
        }

        for (String biomeName : config.getStringList(id + ".player-conditions.biomes").orElse(new ArrayList<>())) {
            Optional<Biome> biome = StringUtils.parseBiome(biomeName);
            if (biome.isEmpty()) {
                CommandSchedulerPlugin.logWarning("Invalid biome " + biomeName + " in conditional effect " + id + ".");
                CommandSchedulerPlugin.logWarning("Valid biomes are: " + String.join(", ", StringUtils.getBiomeNames()) + ".");
                continue;
            }
            this.biomes.add(biome.get());
        }

        for (String gamemode : config.getStringList(id + ".player-conditions.gamemodes").orElse(new ArrayList<>())) {
            Optional<GameMode> gameMode = StringUtils.parseGameMode(gamemode);
            if (gameMode.isEmpty()) {
                CommandSchedulerPlugin.logWarning("Invalid gamemode " + gamemode + " in conditional effect " + id + ".");
                CommandSchedulerPlugin.logWarning("Valid gamemodes are: " + String.join(", ", StringUtils.getGameModeNames()) + ".");
                continue;
            }
            this.gamemodes.add(gameMode.get());
        }

        for (String permission : config.getStringList(id + ".player-conditions.has-permissions").orElse(new ArrayList<>())) {
            this.hasPermissions.add(permission);
        }

        for (String permission : config.getStringList(id + ".player-conditions.missing-permissions").orElse(new ArrayList<>())) {
            this.missingPermissions.add(permission);
        }

        this.minX = config.getInt(id + ".player-conditions.min-x");
        this.maxX = config.getInt(id + ".player-conditions.max-x");
        this.minY = config.getInt(id + ".player-conditions.min-y");
        this.maxY = config.getInt(id + ".player-conditions.max-y");
        this.minZ = config.getInt(id + ".player-conditions.min-z");
        this.maxZ = config.getInt(id + ".player-conditions.max-z");

        this.inWater = config.getBoolean(id + ".player-conditions.in-water");

        this.sneaking = config.getBoolean(id + ".player-conditions.sneaking");
        
        this.blocking = config.getBoolean(id + ".player-conditions.blocking");

        this.climbing = config.getBoolean(id + ".player-conditions.climbing");

        this.gliding = config.getBoolean(id + ".player-conditions.gliding");

        this.glowing = config.getBoolean(id + ".player-conditions.glowing");

        this.riptiding = config.getBoolean(id + ".player-conditions.riptiding");

        this.inVehicle = config.getBoolean(id + ".player-conditions.in-vehicle");

        this.sprinting = config.getBoolean(id + ".player-conditions.sprinting");

        this.flying = config.getBoolean(id + ".player-conditions.flying");

        this.onFire = config.getBoolean(id + ".player-conditions.on-fire");

        this.frozen = config.getBoolean(id + ".player-conditions.frozen");
        this.playerConditionsEnabled = !worlds.isEmpty() || !environments.isEmpty() || !biomes.isEmpty()
            || minX.isPresent() || maxX.isPresent() || minY.isPresent() || maxY.isPresent() || minZ.isPresent() || maxZ.isPresent()
            || inWater.isPresent() || sneaking.isPresent() || blocking.isPresent() || climbing.isPresent() || gliding.isPresent()
            || glowing.isPresent() || riptiding.isPresent() || inVehicle.isPresent() || sprinting.isPresent() || flying.isPresent()
            || onFire.isPresent() || frozen.isPresent();
    }

    // Identification

    @NotNull
    public String getId() {
        return id;
    }

    // Commands

    @NotNull
    public List<@NotNull CommandExecutable> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    // Execution

    public void execute() {
        // Execute Conditions Check
        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        if (minPlayersOnlineToExecute > onlinePlayers.size()) {
            return;
        }
        if (maxPlayersOnlineToExecute < onlinePlayers.size()) {
            return;
        }

        Collection<? extends Player> playersWhoMeetConditions = playerConditionsEnabled ? getPlayersWhoMeetConditions() : onlinePlayers;
        if (minPlayersWhoMeetConditionsToExecute > playersWhoMeetConditions.size()) {
            return;
        }
        if (maxPlayersWhoMeetConditionsToExecute < playersWhoMeetConditions.size()) {
            return;
        }
        if (onlyExecuteIfAllPlayersMeetConditions && playersWhoMeetConditions.size() != onlinePlayers.size()) {
            return;
        }

        ArrayList<Player> playersToExecuteOn = new ArrayList<>();
        if (playerConditionsEnabled) {
            for (Player player : onlinePlayers) {
                if (player == null) {
                    continue;
                }

                PlayerProfile playerProfile = PlayerProfileManager.get(player);

                boolean currentConditionCheckValue = playersWhoMeetConditions.contains(player);
                boolean lastConditionCheckValue = playerProfile.getLastConditionCheckValue(this);

                if (this.executeOn == ExecuteOn.CONDITIONS_PASS && currentConditionCheckValue) {
                    playersToExecuteOn.add(player);
                }
                else if (this.executeOn == ExecuteOn.CONDITIONS_FAIL && !currentConditionCheckValue) {
                    playersToExecuteOn.add(player);
                }
                else if (this.executeOn == ExecuteOn.CONDITIONS_CHANGE_RISING_EDGE && currentConditionCheckValue && !lastConditionCheckValue) {
                    playersToExecuteOn.add(player);
                }
                else if (this.executeOn == ExecuteOn.CONDITIONS_CHANGE_FALLING_EDGE && !currentConditionCheckValue && lastConditionCheckValue) {
                    playersToExecuteOn.add(player);
                }
                else if (this.executeOn == ExecuteOn.CONDITIONS_CHANGE && currentConditionCheckValue != lastConditionCheckValue) {
                    playersToExecuteOn.add(player);
                }

                playerProfile.setLastConditionCheckValue(this, currentConditionCheckValue);
            }
        }

        // Execute Commands
        if (onlyRunOneRandomCommand) {
            if (commands.isEmpty()) {
                return;
            }

            int randomIndex = (int) (Math.random() * commands.size());
            commands.get(randomIndex).execute(playerConditionsEnabled ? playersToExecuteOn : onlinePlayers);
        }
        else {
            for (CommandExecutable command : commands) {
                command.execute(playerConditionsEnabled ? playersToExecuteOn : onlinePlayers);
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

    // Event Check

    public void onEvent(@NotNull EventType eventType, @NotNull Location location, @Nullable Entity entity, @Nullable ItemStack item, @Nullable Block block, int amount) {
        if (!events.contains(eventType)) {
            return;
        }

        execute();
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

        if (!worlds.isEmpty() && !worlds.contains(StringUtils.normalize(world.getName()))) {
            return false;
        }

        if (!environments.isEmpty() && !environments.contains(world.getEnvironment())) {
            return false;
        }

        if (!biomes.isEmpty() && !biomes.contains(block.getBiome())) {
            return false;
        }

        if (!gamemodes.isEmpty() && !gamemodes.contains(player.getGameMode())) {
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

        if (sneaking.isPresent()) {
            if (sneaking.get() && !player.isSneaking()) {
                return false;
            }
            if (!sneaking.get() && player.isSneaking()) {
                return false;
            }
        }

        if (blocking.isPresent()) {
            if (blocking.get() && !player.isBlocking()) {
                return false;
            }
            if (!blocking.get() && player.isBlocking()) {
                return false;
            }
        }

        if (climbing.isPresent()) {
            if (climbing.get() && !player.isClimbing()) {
                return false;
            }
            if (!climbing.get() && player.isClimbing()) {
                return false;
            }
        }

        if (gliding.isPresent()) {
            if (gliding.get() && !player.isGliding()) {
                return false;
            }
            if (!gliding.get() && player.isGliding()) {
                return false;
            }
        }

        if (glowing.isPresent()) {
            if (glowing.get() && !player.isGlowing()) {
                return false;
            }
            if (!glowing.get() && player.isGlowing()) {
                return false;
            }
        }

        if (riptiding.isPresent()) {
            if (riptiding.get() && !player.isRiptiding()) {
                return false;
            }
            if (!riptiding.get() && player.isRiptiding()) {
                return false;
            }
        }

        if (inVehicle.isPresent()) {
            if (inVehicle.get() && !player.isInsideVehicle()) {
                return false;
            }
            if (!inVehicle.get() && player.isInsideVehicle()) {
                return false;
            }
        }

        if (sprinting.isPresent()) {
            if (sprinting.get() && !player.isSprinting()) {
                return false;
            }
            if (!sprinting.get() && player.isSprinting()) {
                return false;
            }
        }

        if (flying.isPresent()) {
            if (flying.get() && !player.isFlying()) {
                return false;
            }
            if (!flying.get() && player.isFlying()) {
                return false;
            }
        }

        if (onFire.isPresent()) {
            if (onFire.get() && !player.isVisualFire()) {
                return false;
            }
            if (!onFire.get() && player.isVisualFire()) {
                return false;
            }
        }

        if (frozen.isPresent()) {
            if (frozen.get() && !player.isFrozen()) {
                return false;
            }
            if (!frozen.get() && player.isFrozen()) {
                return false;
            }
        }

        return true;
    }

    // ------------ //
    // Data Getters //
    // ------------ //

    // Behavior Data

    public boolean isOnlyRunOneRandomCommand() {
        return onlyRunOneRandomCommand;
    }

    public @NotNull ExecuteOn getExecuteOn() {
        return executeOn;
    }

    // Execution Conditions Data

    public int getMinPlayersOnlineToExecute() {
        return minPlayersOnlineToExecute;
    }

    public int getMaxPlayersOnlineToExecute() {
        return maxPlayersOnlineToExecute;
    }

    public int getMinPlayersWhoMeetConditionsToExecute() {
        return minPlayersWhoMeetConditionsToExecute;
    }

    public int getMaxPlayersWhoMeetConditionsToExecute() {
        return maxPlayersWhoMeetConditionsToExecute;
    }

    public boolean isOnlyExecuteIfAllPlayersMeetConditions() {
        return onlyExecuteIfAllPlayersMeetConditions;
    }

    // Tick Check Data

    public Optional<Integer> getIntervalTicks() {
        return intervalTicks;
    }

    public @NotNull HashSet<Integer> getTicksFromServerStart() {
        return ticksFromServerStart;
    }

    // Cron Check Data
    
    public Optional<Cron> getCron() {
        return cron;
    }

    // Condition Check Data

    public boolean isPlayerConditionsEnabled() {
        return playerConditionsEnabled;
    }

    public @NotNull HashSet<String> getWorlds() {
        return worlds;
    }

    public @NotNull HashSet<Environment> getEnvironments() {
        return environments;
    }

    public @NotNull HashSet<Biome> getBiomes() {
        return biomes;
    }

    public @NotNull HashSet<GameMode> getGamemodes() {
        return gamemodes;
    }

    public @NotNull ArrayList<String> getHasPermissions() {
        return hasPermissions;
    }

    public @NotNull ArrayList<String> getMissingPermissions() {
        return missingPermissions;
    }

    public Optional<Integer> getMinX() {
        return minX;
    }

    public Optional<Integer> getMaxX() {
        return maxX;
    }

    public Optional<Integer> getMinY() {
        return minY;
    }

    public Optional<Integer> getMaxY() {
        return maxY;
    }

    public Optional<Integer> getMinZ() {
        return minZ;
    }

    public Optional<Integer> getMaxZ() {
        return maxZ;
    }

    public Optional<Boolean> getInWater() {
        return inWater;
    }

    public Optional<Boolean> getSneaking() {
        return sneaking;
    }

    public Optional<Boolean> getBlocking() {
        return blocking;
    }

    public Optional<Boolean> getClimbing() {
        return climbing;
    }

    public Optional<Boolean> getGliding() {
        return gliding;
    }

    public Optional<Boolean> getGlowing() {
        return glowing;
    }

    public Optional<Boolean> getRiptiding() {
        return riptiding;
    }

    public Optional<Boolean> getInVehicle() {
        return inVehicle;
    }

    public Optional<Boolean> getSprinting() {
        return sprinting;
    }

    public Optional<Boolean> getFlying() {
        return flying;
    }

    public Optional<Boolean> getOnFire() {
        return onFire;
    }

    public Optional<Boolean> getFrozen() {
        return frozen;
    }
}