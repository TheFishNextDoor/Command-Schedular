package fun.sunrisemc.commandschedular.cron;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandschedular.CommandSchedularPlugin;

public class CronField {

    private @NotNull Set<Integer> values = new HashSet<>();

    private boolean all;

    public CronField(@NotNull String expression, int min, int max) {
        if (expression.equals("*")) {
            all = true;
            return;
        }

        String[] parts = expression.split(",");
        for (String part : parts) {
            if (part.contains("/")) {
                String[] stepParts = part.split("/");

                String base = stepParts[0];

                int step;
                try {
                    step = Integer.parseInt(stepParts[1]);
                } catch (NumberFormatException e) {
                    CommandSchedularPlugin.logWarning("Invalid cron expression: " + part);
                    continue;
                }

                int start;
                try {
                    start = base.equals("*") ? min : Integer.parseInt(base);
                } catch (NumberFormatException e) {
                    CommandSchedularPlugin.logWarning("Invalid cron expression: " + part);
                    continue;
                }

                for (int i = start; i <= max; i += step) {
                    values.add(i);
                }
            }
            else if (part.contains("-")) {
                String[] rangeParts = part.split("-");
                if (rangeParts.length != 2) {
                    CommandSchedularPlugin.logWarning("Invalid cron expression: " + part);
                    continue;
                }

                int rangeStart;
                int rangeEnd;
                try {
                    rangeStart = Integer.parseInt(rangeParts[0]);
                    rangeEnd = Integer.parseInt(rangeParts[1]);
                } catch (NumberFormatException e) {
                    CommandSchedularPlugin.logWarning("Invalid cron expression: " + part);
                    continue;
                }

                for (int i = rangeStart; i <= rangeEnd; i++) {
                    values.add(i);
                }
            }
            else {
                values.add(Integer.parseInt(part));
            }
        }
    }

    public boolean matches(int value) {
        return all || values.contains(value);
    }
}