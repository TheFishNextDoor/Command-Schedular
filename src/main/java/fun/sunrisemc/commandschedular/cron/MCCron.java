package fun.sunrisemc.commandschedular.cron;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandschedular.CommandSchedularPlugin;

public class MCCron {

    private @Nullable CronField second = null;
    private @Nullable CronField minute = null;
    private @Nullable CronField hour = null;
    private @Nullable CronField dayOfMonth = null;
    private @Nullable CronField month = null;
    private @Nullable CronField dayOfWeek = null;
    private @Nullable CronField year = null;

    public MCCron(@NotNull String expression) {
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
        if (second == null || minute == null || hour == null || dayOfMonth == null || month == null || dayOfWeek == null || year == null) {
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