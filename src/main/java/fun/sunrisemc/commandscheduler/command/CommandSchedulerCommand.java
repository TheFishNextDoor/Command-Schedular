package fun.sunrisemc.commandscheduler.command;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.md_5.bungee.api.ChatColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.permission.Permissions;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;
import fun.sunrisemc.commandscheduler.scheduler.TickCommandExecutionTask;

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

            // /commandscheduler execute <commandId>
            if (subcommand.equals("execute") && sender.hasPermission(Permissions.EXECUTE_PERMISSION)) {
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
        else {
            sendHelpMessage(sender);
            return true;
        }
    }

    private void sendHelpMessage(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Command Scheduler Help");
        sender.sendMessage(ChatColor.YELLOW + "/commandscheduler help " + ChatColor.WHITE + "Show this help message");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler reload " + ChatColor.WHITE + "Reload the plugin");
        }
        if (sender.hasPermission(Permissions.EXECUTE_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/commandscheduler execute <commandId> " + ChatColor.WHITE + "Execute a scheduled command immediately");
        }
    }
}