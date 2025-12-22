package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.utils.StringUtils;

public enum ExecuteOn {

    CONDITIONS_PASS, // Executes every time the conditions pass
    CONDITIONS_FAIL, // Executes every time the conditions fail
    CONDITIONS_CHANGE, // Executes only when the conditions change, regardless of pass or fail
    CONDITIONS_CHANGE_RISING_EDGE, // Executes only when the conditions change from failing to passing
    CONDITIONS_CHANGE_FALLING_EDGE; // Executes only when the conditions change from passing to failing

    public static Optional<ExecuteOn> parseExecuteOn(@NotNull String executeOnName) {
        String normalizedNameA = StringUtils.normalize(executeOnName);
        for (ExecuteOn executeOn : ExecuteOn.values()) {
            String normalizedNameB = StringUtils.normalize(executeOn.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(executeOn);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getExecuteOnNames() {
        ArrayList<String> names = new ArrayList<>();
        for (ExecuteOn executeOn : ExecuteOn.values()) {
            String formattedName = StringUtils.kebabCase(executeOn.name());
            names.add(formattedName);
        }
        return names;
    }
}