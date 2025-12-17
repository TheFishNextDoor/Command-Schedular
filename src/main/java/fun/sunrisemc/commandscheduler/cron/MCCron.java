package fun.sunrisemc.commandscheduler.cron;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.commandscheduler.CommandSchedulerPlugin;

public class MCCron {

    private final @NotNull String expression;

    private @Nullable CronField second = null;
    private @Nullable CronField minute = null;
    private @Nullable CronField hour = null;
    private @Nullable CronField dayOfMonth = null;
    private @Nullable CronField month = null;
    private @Nullable CronField dayOfWeek = null;
    private @Nullable CronField year = null;

    public MCCron(@NotNull String expression) {
        this.expression = expression;

        String[] parts = expression.split(" ");
        if (parts.length != 7) {
            CommandSchedulerPlugin.logWarning("Invalid cron expression: " + expression);
            CommandSchedulerPlugin.logWarning("Cron expressions must have 7 parts (second, minute, hour, day of month, month, day of week, year).");
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

    @Override
    public @NotNull String toString() {
        return getExpression();
    }

    public @NotNull String getExpression() {
        return expression;
    }

    public boolean matches(int secondValue, int minuteValue, int hourValue, int dayOfMonthValue, int monthValue, int dayOfWeekValue, int yearValue) {
        return second != null && second.matches(secondValue) &&
               minute != null && minute.matches(minuteValue) &&
               hour != null && hour.matches(hourValue) &&
               dayOfMonth != null && dayOfMonth.matches(dayOfMonthValue) &&
               month != null && month.matches(monthValue) &&
               dayOfWeek != null && dayOfWeek.matches(dayOfWeekValue) &&
               year != null && year.matches(yearValue);
    }
}