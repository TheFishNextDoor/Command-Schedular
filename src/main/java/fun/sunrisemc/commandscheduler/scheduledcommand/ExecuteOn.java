package fun.sunrisemc.commandscheduler.scheduledcommand;

public enum ExecuteOn {

    CONDITIONS_PASS, // Executes every time the conditions pass
    CONDITIONS_FAIL, // Executes every time the conditions fail
    CONDITIONS_CHANGE, // Executes only when the conditions change, regardless of pass or fail
    CONDITIONS_CHANGE_RISING_EDGE, // Executes only when the conditions change from failing to passing
    CONDITIONS_CHANGE_FALLING_EDGE; // Executes only when the conditions change from passing to failing
    
}