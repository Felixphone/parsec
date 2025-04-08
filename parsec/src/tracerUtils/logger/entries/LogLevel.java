package tracerUtils.logger.entries;

public enum LogLevel {
    ALL(0, "All", ConsoleColours.WHITE),
    ULTRA_FINE(1, "Ultra-fine", ConsoleColours.WHITE),
    FINE(2, "Fine", ConsoleColours.WHITE),
    INFO(3,"Info", ConsoleColours.WHITE),
    SUCCESS(4, "Success", ConsoleColours.GREEN),
    ATTEMPT(5, "Attempt", ConsoleColours.BLUE),
    CRITICAL_SUCCESS(6, "Critical-Success", ConsoleColours.GREEN),
    CRITICAL_ATTEMPT(7, "Critical-Attempt", ConsoleColours.CYAN),
    WARNING(8, "Warning", ConsoleColours.YELLOW),
    ERROR(9, "Error", ConsoleColours.RED),
    FATAL(10, "Fatal", ConsoleColours.RED),
    LAUNCHER(11, "Launcher", ConsoleColours.WHITE),
    OFF(12, "Off", ConsoleColours.WHITE);

    private final int level;
    private final String nameLower;
    private final String colour;
    LogLevel(int level, String nameLower, String colour) {
        this.level = level;
        this.nameLower = nameLower;
        this.colour = colour;
    }

    public int getLevel() {
        return level;
    }

    public String getNameLower() {
        return nameLower;
    }

    public String getColour() {
        return colour;
    }

    public static LogLevel getDefault() {
        return LogLevel.INFO;
    }
}
