package fun.sunrisemc.command_schedular.cron;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.command_schedular.CommandSchedularPlugin;

public class MCCron {

    private CronField second = null;
    private CronField minute = null;
    private CronField hour = null;
    private CronField dayOfMonth = null;
    private CronField month = null;
    private CronField dayOfWeek = null;
    private CronField year = null;

    public MCCron(@NonNull String expression) {
        String[] parts = expression.split(" ");
        if (parts.length != 7) {
            CommandSchedularPlugin.logWarning("Invalid cron expression: " + expression);
            CommandSchedularPlugin.logWarning("Cron expressions must have 7 parts (second, minute, hour, day of month, month, day of week, year).");
            return;
        }

        second = new CronField(parts[0], 0, 59);
        minute = new CronField(parts[1], 0, 59);
        hour = new CronField(parts[2], 0, 23);
        dayOfMonth = new CronField(parts[3], 1, 31);
        month = new CronField(parts[4], 1, 12);
        dayOfWeek = new CronField(parts[5], 0, 6);
        year = new CronField(parts[6], 1970, 2106);
    }

    public boolean matches(int secondValue, int minuteValue, int hourValue, int dayOfMonthValue, int monthValue, int dayOfWeekValue, int yearValue) {
        if (second == null) {
            return false;
        }
        return second.matches(secondValue) &&
               minute.matches(minuteValue) &&
               hour.matches(hourValue) &&
               dayOfMonth.matches(dayOfMonthValue) &&
               month.matches(monthValue) &&
               dayOfWeek.matches(dayOfWeekValue) &&
               year.matches(yearValue);
    }
}