package fun.sunrisemc.command_schedular.tasks;

import org.bukkit.Bukkit;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfiguration;
import fun.sunrisemc.command_schedular.scheduled_command.CommandConfigurationManager;

public class CommandExecutionTask {

    private static final int INTERVAL_TICKS = 1; // 1 Tick

    private static int id = -1;

    private static int tickCount = 0;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimer(CommandSchedularPlugin.getInstance(), () -> {
            if (tickCount == Integer.MAX_VALUE) {
                tickCount = 0;
            }
            tickCount++;

            for (CommandConfiguration commandConfiguration : CommandConfigurationManager.getAll()) {
                if (commandConfiguration.shouldRun(tickCount)) {
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

    public static int getTickCount() {
        return tickCount;
    }
}