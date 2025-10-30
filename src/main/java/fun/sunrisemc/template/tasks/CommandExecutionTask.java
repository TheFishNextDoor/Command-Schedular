package fun.sunrisemc.template.tasks;

import org.bukkit.Bukkit;
import fun.sunrisemc.template.CommandSchedularPlugin;

public class CommandExecutionTask {

    private static final int INTERVAL_TICKS = 1; // 1 Tick

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimer(CommandSchedularPlugin.getInstance(), () -> {
            run();
        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }

    private static void run() {
        // Code here
    }
}