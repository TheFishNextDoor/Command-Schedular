package fun.sunrisemc.commandscheduler.scheduler;

import org.bukkit.Bukkit;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfiguration;
import fun.sunrisemc.commandscheduler.scheduledcommand.CommandConfigurationManager;

public class TickCommandExecutionTask {

    private static final int INTERVAL_TICKS = 1; // 1 Tick

    private static int id = -1;

    private static int tickCount = 0;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimer(CommandSchedulerPlugin.getInstance(), () -> {
            if (tickCount == Integer.MAX_VALUE) {
                tickCount = 0;
            }
            tickCount++;

            for (CommandConfiguration commandConfiguration : CommandConfigurationManager.getAll()) {
                if (commandConfiguration.shouldRunFromTick(tickCount)) {
                    commandConfiguration.execute();
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

    public static int getTicksFromServerStart() {
        return tickCount;
    }
}