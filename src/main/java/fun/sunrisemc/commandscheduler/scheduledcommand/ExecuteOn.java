package fun.sunrisemc.commandscheduler.scheduledcommand;

public enum ExecuteOn {

    PASS, // Executes every time the conditions pass
    FAIL, // Executes every time the conditions fail
    RISING_EDGE, // Executes only when the conditions change from failing to passing
    FALLING_EDGE, // Executes only when the conditions change from passing to failing
    CHANGE; // Executes only when the conditions change, regardless of pass or fail
    
}