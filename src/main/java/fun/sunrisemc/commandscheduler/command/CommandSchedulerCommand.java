package fun.sunrisemc.commandscheduler.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.cron.Cron;
import fun.sunrisemc.commandscheduler.permission.Permissions;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandExecutable;
import fun.sunrisemc.commandscheduler.scheduledcommand.ExecuteOn;
import fun.sunrisemc.commandscheduler.scheduler.TickCommandExecutionTask;
import fun.sunrisemc.commandscheduler.utils.StringUtils;

public class CommandSchedulerCommand implements CommandExecutor, TabCompleter {

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        // /commandscheduler <subcommand>
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<>();
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(Permissions.VIEW_PERMISSION)) {
                completions.add("view");
            }
            if (sender.hasPermission(Permissions.EXECUTE_PERMISSION)) {
                completions.add("execute");
            }
            if (sender.hasPermission(Permissions.TIME_PERMISSION)) {
                completions.add("time");
            }
            return completions;
        }
        else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            // /commandscheduler view <commandId>
            if (sender.hasPermission(Permissions.VIEW_PERMISSION) && subcommand.equals("view")) {
                return CommandConfigurationManager.getIds();
            }
            // /commandscheduler execute <commandId>
            else if (subcommand.equals("execute") && sender.hasPermission(Permissions.EXECUTE_PERMISSION)) {
                return CommandConfigurationManager.getIds();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Reload
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION) && subCommand.equals("reload")) {
            CommandSchedulerPlugin.reload();
            sender.sendMessage(ChatColor.YELLOW + "Configuration reloaded.");
            return true;
        }
        // View
        else if (sender.hasPermission(Permissions.VIEW_PERMISSION) && subCommand.equals("view")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a command ID to execute.");
                return true;
            }

            String commandId = args[1];
            Optional<CommandConfiguration> commandConfigOptional = CommandConfigurationManager.get(commandId);
            if (commandConfigOptional.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No scheduled command found with ID: " + commandId);
                return true;
            }

            CommandConfiguration commandConfig = commandConfigOptional.get();

            // Header

            sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Command Configuration Details");

            // Command Id
            
            sender.sendMessage(ChatColor.YELLOW + "Command Id: " + ChatColor.WHITE + commandConfig.getId());

            // Behavior

            boolean onlyRunRandom = commandConfig.isOnlyRunOneRandomCommand();
            if (onlyRunRandom) {
                sender.sendMessage(ChatColor.YELLOW + "Execute: " + ChatColor.WHITE + "One Random Command");
            } 
            else {
                sender.sendMessage(ChatColor.YELLOW + "Execute: " + ChatColor.WHITE + "All Commands");
            }

            // Commands

            List<CommandExecutable> commands = commandConfig.getCommands();
            if (!commands.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "Commands:");
            }
            for (CommandExecutable commandExecutable : commands) {
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + StringUtils.titleCase(commandExecutable.getType().name()) + " -> " + ChatColor.WHITE + commandExecutable.getCommand());
            }

            // Triggers

            Optional<Integer> intervalTicks = commandConfig.getIntervalTicks();
            HashSet<Integer> ticksFromServerStart = commandConfig.getTicksFromServerStart();
            Optional<Cron> cron = commandConfig.getCron();

            if (intervalTicks.isPresent() || !ticksFromServerStart.isEmpty() || cron.isPresent()) {
                sender.sendMessage(ChatColor.YELLOW + "Triggers:");
            }

            if (intervalTicks.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Tick Interval: " + ChatColor.WHITE + intervalTicks.get());
            }

            if (!ticksFromServerStart.isEmpty()) {
                String ticksFromServerStartString = String.join(", ", ticksFromServerStart.stream().map(String::valueOf).toArray(String[]::new));
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Ticks From Server Start: " + ChatColor.WHITE + ticksFromServerStartString);
            }

            if (cron.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Cron: " + ChatColor.WHITE + cron.get().toString());
            }

            // Execute Conditions

            sender.sendMessage(ChatColor.YELLOW + "Execute Conditions:");

            ExecuteOn executeOn = commandConfig.getExecuteOn();
            sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Execute On: " + ChatColor.WHITE + StringUtils.titleCase(executeOn.name()));

            boolean onlyExecuteIfAllPlayersMeetConditions = commandConfig.isOnlyExecuteIfAllPlayersMeetConditions();
            if (onlyExecuteIfAllPlayersMeetConditions) {
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Execute If: " + ChatColor.WHITE + "All Players Meet Conditions");
            }
            else {
                sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Execute If: " + ChatColor.WHITE + "Any Player Meets Conditions");
            }

            int minPlayersOnlineToExecute = commandConfig.getMinPlayersOnlineToExecute();
            int maxPlayersOnlineToExecute = commandConfig.getMaxPlayersOnlineToExecute();
            if (minPlayersOnlineToExecute > 0 && maxPlayersOnlineToExecute == Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Online " + ChatColor.YELLOW + " at least " + ChatColor.WHITE + minPlayersOnlineToExecute);
            }
            else if (minPlayersOnlineToExecute == 0 && maxPlayersOnlineToExecute < Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Online " + ChatColor.YELLOW + " at most " + ChatColor.WHITE + maxPlayersOnlineToExecute);
            }
            else if (minPlayersOnlineToExecute > 0 && maxPlayersOnlineToExecute < Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Online " + ChatColor.YELLOW + " between " + ChatColor.WHITE + minPlayersOnlineToExecute + ChatColor.YELLOW + " and " + ChatColor.WHITE + maxPlayersOnlineToExecute);
            }

            int minPlayersWhoMeetConditionsToExecute = commandConfig.getMinPlayersWhoMeetConditionsToExecute();
            int maxPlayersWhoMeetConditionsToExecute = commandConfig.getMaxPlayersWhoMeetConditionsToExecute();
            if (minPlayersWhoMeetConditionsToExecute > 0 && maxPlayersWhoMeetConditionsToExecute == Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Who Meet Conditions " + ChatColor.YELLOW + " at least " + ChatColor.WHITE + minPlayersWhoMeetConditionsToExecute);
            }
            else if (minPlayersWhoMeetConditionsToExecute == 0 && maxPlayersWhoMeetConditionsToExecute < Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Who Meet Conditions " + ChatColor.YELLOW + " at most " + ChatColor.WHITE + maxPlayersWhoMeetConditionsToExecute);
            }
            else if (minPlayersWhoMeetConditionsToExecute > 0 && maxPlayersWhoMeetConditionsToExecute < Integer.MAX_VALUE) {
                sender.sendMessage(ChatColor.WHITE + "- Players Who Meet Conditions " + ChatColor.YELLOW + " between " + ChatColor.WHITE + minPlayersWhoMeetConditionsToExecute + ChatColor.YELLOW + " and " + ChatColor.WHITE + maxPlayersWhoMeetConditionsToExecute);
            }

            // Player Conditions

            if (commandConfig.isPlayerConditionsEnabled()) {
                sender.sendMessage(ChatColor.YELLOW + "Player Conditions:");
            }

            HashSet<String> worlds = commandConfig.getWorlds();
            if (!worlds.isEmpty()) {
                String orString = ChatColor.YELLOW + " or " + ChatColor.WHITE;
                String worldsString = String.join(orString, worlds);
                sender.sendMessage(ChatColor.WHITE + "- World " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + worldsString);
            }

            HashSet<Environment> environments = commandConfig.getEnvironments();
            if (!environments.isEmpty()) {
                String orString = ChatColor.YELLOW + " or " + ChatColor.WHITE;
                String environmentsString = String.join(orString, environments.stream().map(Enum::name).toArray(String[]::new));
                sender.sendMessage(ChatColor.WHITE + "- Environment " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + environmentsString);
            }

            HashSet<Biome> biomes = commandConfig.getBiomes();
            if (!biomes.isEmpty()) {
                String orString = ChatColor.YELLOW + " or " + ChatColor.WHITE;
                String biomesString = String.join(orString, biomes.stream().map(Enum::name).toArray(String[]::new));
                sender.sendMessage(ChatColor.WHITE + "- Biome " + ChatColor.YELLOW + " equals " + ChatColor.WHITE + biomesString);
            }

            HashSet<GameMode> gameModes = commandConfig.getGamemodes();
            if (!gameModes.isEmpty()) {
                String orString = ChatColor.YELLOW + " or " + ChatColor.WHITE;
                String gameModesString = String.join(orString, gameModes.stream().map(Enum::name).toArray(String[]::new));
                sender.sendMessage(ChatColor.WHITE + "- Game Mode " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + gameModesString);
            }

            ArrayList<String> hasPermissions = commandConfig.getHasPermissions();
            if (!hasPermissions.isEmpty()) {
                String andString = ChatColor.YELLOW + " and " + ChatColor.WHITE;
                String hasPermissionsString = String.join(andString, hasPermissions);
                sender.sendMessage(ChatColor.WHITE + "- Has Permissions: " + hasPermissionsString);
            }

            ArrayList<String> missingPermissions = commandConfig.getMissingPermissions();
            if (!missingPermissions.isEmpty()) {
                String orString = ChatColor.YELLOW + " or " + ChatColor.WHITE;
                String missingPermissionsString = String.join(orString, missingPermissions);
                sender.sendMessage(ChatColor.WHITE + "- Does Not Have Permissions: " + missingPermissionsString);
            }

            Optional<Integer> minX = commandConfig.getMinX();
            Optional<Integer> maxX = commandConfig.getMaxX();
            if (minX.isPresent() && maxX.isEmpty()) {
                sender.sendMessage(ChatColor.WHITE + "- Position X " + ChatColor.YELLOW+ "greater than " + ChatColor.WHITE + minX.get().toString());
            }
            else if (minX.isEmpty() && maxX.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position X " + ChatColor.YELLOW+ "less than " + ChatColor.WHITE + maxX.get().toString());
            }
            else if (minX.isPresent() && maxX.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position X " + ChatColor.YELLOW+ "between " + ChatColor.WHITE + minX.get().toString() + ChatColor.YELLOW + " and " + ChatColor.WHITE + maxX.get().toString());
            }

            Optional<Integer> minY = commandConfig.getMinY();
            Optional<Integer> maxY = commandConfig.getMaxY();
            if (minY.isPresent() && maxY.isEmpty()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Y " + ChatColor.YELLOW+ "greater than " + ChatColor.WHITE + minY.get().toString());
            }
            else if (minY.isEmpty() && maxY.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Y " + ChatColor.YELLOW+ "less than " + ChatColor.WHITE + maxY.get().toString());
            }
            else if (minY.isPresent() && maxY.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Y " + ChatColor.YELLOW+ "between " + ChatColor.WHITE + minY.get().toString() + ChatColor.YELLOW + " and " + ChatColor.WHITE + maxY.get().toString());
            }

            Optional<Integer> minZ = commandConfig.getMinZ();
            Optional<Integer> maxZ = commandConfig.getMaxZ();
            if (minZ.isPresent() && maxZ.isEmpty()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Z " + ChatColor.YELLOW+ "greater than " + ChatColor.WHITE + minZ.get().toString());
            }
            else if (minZ.isEmpty() && maxZ.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Z " + ChatColor.YELLOW+ "less than " + ChatColor.WHITE + maxZ.get().toString());
            }
            else if (minZ.isPresent() && maxZ.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Position Z " + ChatColor.YELLOW+ "between " + ChatColor.WHITE + minZ.get().toString() + ChatColor.YELLOW + " and " + ChatColor.WHITE + maxZ.get().toString());
            }

            Optional<Boolean> inWater = commandConfig.getInWater();
            if (inWater.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- In Water " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(inWater.get().toString()));
            }

            Optional<Boolean> sneaking = commandConfig.getSneaking();
            if (sneaking.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Sneaking " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(sneaking.get().toString()));
            }

            Optional<Boolean> blocking = commandConfig.getBlocking();
            if (blocking.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Blocking " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(blocking.get().toString()));
            }

            Optional<Boolean> climbing = commandConfig.getClimbing();
            if (climbing.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Climbing " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(climbing.get().toString()));
            }

            Optional<Boolean> gliding = commandConfig.getGliding();
            if (gliding.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Gliding " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(gliding.get().toString()));
            }

            Optional<Boolean> glowing = commandConfig.getGlowing();
            if (glowing.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Glowing " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(glowing.get().toString()));
            }

            Optional<Boolean> riptiding = commandConfig.getRiptiding();
            if (riptiding.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Riptiding " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(riptiding.get().toString()));
            }

            Optional<Boolean> inVehicle = commandConfig.getInVehicle();
            if (inVehicle.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- In Vehicle " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(inVehicle.get().toString()));
            }

            Optional<Boolean> sprinting = commandConfig.getSprinting();
            if (sprinting.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Sprinting " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(sprinting.get().toString()));
            }

            Optional<Boolean> flying = commandConfig.getFlying();
            if (flying.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Flying " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(flying.get().toString()));
            }

            Optional<Boolean> onFire = commandConfig.getOnFire();
            if (onFire.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- On Fire " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(onFire.get().toString()));
            }

            Optional<Boolean> frozen = commandConfig.getFrozen();
            if (frozen.isPresent()) {
                sender.sendMessage(ChatColor.WHITE + "- Frozen " + ChatColor.YELLOW + "equals " + ChatColor.WHITE + StringUtils.titleCase(frozen.get().toString()));
            }

            return true;
            
        }
        // Execute
        else if (sender.hasPermission(Permissions.EXECUTE_PERMISSION) && subCommand.equals("execute")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a command ID to execute.");
                return true;
            }

            String commandId = args[1];
            Optional<CommandConfiguration> commandConfig = CommandConfigurationManager.get(commandId);
            if (commandConfig.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No scheduled command found with ID: " + commandId);
                return true;
            }

            // Run on main thread
            Bukkit.getScheduler().runTask(CommandSchedulerPlugin.getInstance(), () -> {
                commandConfig.get().execute();
            });
            sender.sendMessage(ChatColor.YELLOW + "Command executed successfully.");
            return true;
        }
        // Time
        else if (sender.hasPermission(Permissions.TIME_PERMISSION) && subCommand.equals("time")) {
            int ticksFromServerStart = TickCommandExecutionTask.getTicksFromServerStart();
            int tick = ticksFromServerStart % 20;
            LocalDateTime dateTime = LocalDateTime.now();
            sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Current Server Time");
            sender.sendMessage(ChatColor.YELLOW + "Day of Week: " + ChatColor.WHITE + dateTime.getDayOfWeek().toString() + " (" + dateTime.getDayOfWeek().getValue() + ")");
            sender.sendMessage(ChatColor.YELLOW + "Month: " + ChatColor.WHITE + dateTime.getMonth().toString() + " (" + dateTime.getMonthValue() + ")");
            sender.sendMessage(ChatColor.YELLOW + "Day of Month: " + ChatColor.WHITE + dateTime.getDayOfMonth());
            sender.sendMessage(ChatColor.YELLOW + "Hour: " + ChatColor.WHITE + dateTime.getHour());
            sender.sendMessage(ChatColor.YELLOW + "Minute: " + ChatColor.WHITE + dateTime.getMinute());
            sender.sendMessage(ChatColor.YELLOW + "Second: " + ChatColor.WHITE + dateTime.getSecond());
            sender.sendMessage(ChatColor.YELLOW + "Tick: " + ChatColor.WHITE + tick);
            sender.sendMessage(ChatColor.YELLOW + "Ticks from Server Start: " + ChatColor.WHITE + ticksFromServerStart);
            return true;
        }
        // Help
        else {
            sendHelpMessage(sender);
            return true;
        }
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Command Scheduler Help");
        sender.sendMessage(ChatColor.YELLOW + "/commandscheduler help " + ChatColor.WHITE + "Show this help message.");
        if (sender.hasPermission(Permissions.VIEW_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler view <commandId> " + ChatColor.WHITE + "View details of a scheduled command.");
        }
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler reload " + ChatColor.WHITE + "Reload the plugin.");
        }
        if (sender.hasPermission(Permissions.EXECUTE_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler execute <commandId> " + ChatColor.WHITE + "Execute a scheduled command immediately.");
        }
        if (sender.hasPermission(Permissions.TIME_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler time " + ChatColor.WHITE + "Check the current server time. Useful for configuring a cron.");
        }
        sender.sendMessage(ChatColor.YELLOW + "Note: Commands you do not have permission for will not be shown.");
    }
}