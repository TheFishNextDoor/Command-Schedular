package fun.sunrisemc.commandscheduler.scheduledcommand;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.commandscheduler.utils.StringUtils;

public enum EventType {

    PLACE_BLOCK;

    public static Optional<EventType> parse(@NotNull String eventTypeName) {
        String normalizedNameA = StringUtils.normalize(eventTypeName);
        for (EventType eventType : values()) {
            String normalizedNameB = StringUtils.normalize(eventType.name());
            if (normalizedNameA.equals(normalizedNameB)) {
                return Optional.of(eventType);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        for (EventType eventType : values()) {
            String formattedName = StringUtils.kebabCase(eventType.name());
            names.add(formattedName);
        }
        return names;
    }
}