package fun.sunrisemc.command_schedular.cron;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;

public class MCCron {

    private CronField tick = null;
    private CronField second = null;
    private CronField minute = null;
    private CronField hour = null;
    private CronField dayOfMonth = null;
    private CronField month = null;
    private CronField dayOfWeek = null;

    public MCCron(String expression) {
        String[] parts = expression.split(" ");
        if (parts.length != 7) {
            CommandSchedularPlugin.logWarning("Invalid cron expression: " + expression);
            CommandSchedularPlugin.logWarning("Cron expressions must have 7 parts (tick, second, minute, hour, day of month, month, day of week).");
            return;
        }

        tick = new CronField(parts[0], 0, 19);
        second = new CronField(parts[1], 0, 59);
        minute = new CronField(parts[2], 0, 59);
        hour = new CronField(parts[3], 0, 23);
        dayOfMonth = new CronField(parts[4], 1, 31);
        month = new CronField(parts[5], 1, 12);
        dayOfWeek = new CronField(parts[6], 0, 6);
    }

    public boolean matches(int tickValue, int secondValue, int minuteValue, int hourValue, int dayOfMonthValue, int monthValue, int dayOfWeekValue) {
        if (tick == null) {
            return false;
        }
        return tick.matches(tickValue) &&
               second.matches(secondValue) &&
               minute.matches(minuteValue) &&
               hour.matches(hourValue) &&
               dayOfMonth.matches(dayOfMonthValue) &&
               month.matches(monthValue) &&
               dayOfWeek.matches(dayOfWeekValue);
    }
}