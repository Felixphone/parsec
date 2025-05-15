package tracerUtils.logger.entries;

import java.awt.*;

public enum LogLevel {
    ALL(0, "All", ConsoleColours.WHITE, Color.LIGHT_GRAY, Color.WHITE),
    ULTRA_FINE(1, "Ultra-fine", ConsoleColours.WHITE, new Color(192, 192, 192), new Color(236, 235, 235)),
    FINE(2, "Fine", ConsoleColours.WHITE, new Color(192, 192, 192), new Color(236, 235, 235)),
    INFO(3, "Info", ConsoleColours.WHITE, new Color(128, 128, 128), new Color(209, 209, 209)),
    SUCCESS(4, "Success", ConsoleColours.GREEN, new Color(93, 244, 75), new Color(182, 250, 184)),
    ATTEMPT(5, "Attempt", ConsoleColours.BLUE, new Color(94, 158, 253), new Color(180, 215, 255)),
    CRITICAL_SUCCESS(6, "Critical-Success", ConsoleColours.GREEN, new Color(58, 158, 47), new Color(108, 213, 109)),
    CRITICAL_ATTEMPT(7, "Critical-Attempt", ConsoleColours.CYAN, new Color(0, 208, 255), new Color(150, 255, 232)),
    WARNING(8, "Warning", ConsoleColours.YELLOW, new Color(255, 153, 0), new Color(255, 225, 142)),
    ERROR(9, "Error", ConsoleColours.RED, new Color(255, 0, 0), new Color(255, 157, 157, 255)),
    FATAL(10, "Fatal", ConsoleColours.RED, new Color(255, 0, 0), new Color(255, 157, 157, 255)),
    LAUNCHER(11, "Launcher", ConsoleColours.PURPLE, new Color(143, 97, 255), new Color(195, 174, 251)),
    OFF(12, "Off", ConsoleColours.WHITE, Color.LIGHT_GRAY, Color.WHITE);

    private final int level;
    private final String nameLower;
    private final String colour;
    private final Color primaryDisplayColour;
    private final Color secondaryDisplayColour;

    LogLevel(int level, String nameLower, String colour, Color primaryDisplayColour, Color secondaryDisplayColour) {
        this.level = level;
        this.nameLower = nameLower;
        this.colour = colour;
        this.primaryDisplayColour = primaryDisplayColour;
        this.secondaryDisplayColour = secondaryDisplayColour;
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

    public Color getPrimaryDisplayColour() {
        return primaryDisplayColour;
    }

    public Color getSecondaryDisplayColour() {
        return secondaryDisplayColour;
    }

    public static LogLevel getDefault() {
        return LogLevel.INFO;
    }
}
