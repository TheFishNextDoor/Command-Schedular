package fun.sunrisemc.command_schedular.repeating_task;

import java.time.LocalDateTime;

import org.bukkit.Bukkit;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfiguration;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfigurationManager;

public class CronCommandExecutionTask {

    private static final int INTERVAL_TICKS = 20; // 1 Second

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(CommandSchedularPlugin.getInstance(), () -> {
            LocalDateTime now = LocalDateTime.now();
            for (CommandConfiguration commandConfiguration : CommandConfigurationManager.getAll()) {
                if (commandConfiguration.shouldRunFromCron(now)) {
                    // Run on main thread
                    Bukkit.getScheduler().runTask(CommandSchedularPlugin.getInstance(), () -> {
                        commandConfiguration.execute();
                    });
                }
            }
        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}