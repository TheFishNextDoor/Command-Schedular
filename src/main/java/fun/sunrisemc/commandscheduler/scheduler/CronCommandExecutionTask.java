package fun.sunrisemc.commandscheduler.scheduler;

import java.time.LocalDateTime;

import org.bukkit.Bukkit;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;

public class CronCommandExecutionTask {

    private static final int INTERVAL_TICKS = 20; // 1 Second

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(CommandSchedulerPlugin.getInstance(), () -> {
            LocalDateTime now = LocalDateTime.now();
            for (CommandConfiguration commandConfiguration : CommandConfigurationManager.getAll()) {
                if (commandConfiguration.shouldRunFromCron(now)) {
                    // Run on main thread
                    Bukkit.getScheduler().runTask(CommandSchedulerPlugin.getInstance(), () -> {
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