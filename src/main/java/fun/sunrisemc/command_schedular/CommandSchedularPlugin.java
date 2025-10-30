package fun.sunrisemc.command_schedular;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.command.CommandSchedular;
import fun.sunrisemc.command_schedular.repeating_task.CommandExecutionTask;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfigurationManager;

public class CommandSchedularPlugin extends JavaPlugin {

    private static CommandSchedularPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("commandschedular", new CommandSchedular());

        CommandExecutionTask.start();

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        CommandExecutionTask.stop();
        logInfo("Plugin disabled.");
    }

    public static void loadConfigs() {
        CommandConfigurationManager.loadConfig();
    }

    public static CommandSchedularPlugin getInstance() {
        return instance;
    }

    public static void logInfo(@NonNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NonNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NonNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NonNull String commandName, @NonNull CommandExecutor commandExecutor) {
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