package fun.sunrisemc.command_schedular;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.command_schedular.command.CommandSchedular;
import fun.sunrisemc.command_schedular.repeating_task.CronCommandExecutionTask;
import fun.sunrisemc.command_schedular.repeating_task.TickCommandExecutionTask;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfigurationManager;

public class CommandSchedularPlugin extends JavaPlugin {

    private static @Nullable CommandSchedularPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("commandschedular", new CommandSchedular());

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
    public static CommandSchedularPlugin getInstance() {
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