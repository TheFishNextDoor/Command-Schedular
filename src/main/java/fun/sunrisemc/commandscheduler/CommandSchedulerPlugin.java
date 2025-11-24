package fun.sunrisemc.commandscheduler;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.commandscheduler.command.CommandSchedulerCommand;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;
import fun.sunrisemc.commandscheduler.scheduler.CronCommandExecutionTask;
import fun.sunrisemc.commandscheduler.scheduler.TickCommandExecutionTask;

public class CommandSchedulerPlugin extends JavaPlugin {

    private static @Nullable CommandSchedulerPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("commandscheduler", new CommandSchedulerCommand());

        TickCommandExecutionTask.start();
        CronCommandExecutionTask.start();

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        TickCommandExecutionTask.stop();
        CronCommandExecutionTask.stop();
        logInfo("Plugin disabled.");
    }

    public static void loadConfigs() {
        CommandConfigurationManager.loadConfig();
    }

    @NotNull
    public static CommandSchedulerPlugin getInstance() {
        if (instance != null) {
            return instance;
        }
        else {
            throw new IllegalStateException("Plugin instance is not initialized.");
        }
    }

    public static void logInfo(@NotNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NotNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NotNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NotNull String commandName, @NotNull CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            logSevere("Command '" + commandName + "' not found in plugin.yml.");
            return false;
        }

        command.setExecutor(commandExecutor);

        if (commandExecutor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) commandExecutor);
        }

        return true;
    }
}