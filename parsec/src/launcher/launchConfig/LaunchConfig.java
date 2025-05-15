package launcher.launchConfig;

import tracerUtils.config.FatalExceptionHandling;
import tracerUtils.logger.entries.LogLevel;

/**
 * Holds the initial configuration passed into the program at first execution.
 * <br>
 * Used to change specific behaviors of the engine and watchdog systems.
 */
public class LaunchConfig {

    /**
     * Specifies the minimum {@link LogLevel} of which {@link tracerUtils.logger.entries.LogEntry}s will be logged.
     */
    public LogLevel logLevel;
    /**
     * Specifies whether debug mode is enabled or not.
     */
    public DebugMode debugMode;
    /**
     * Specifies how the watchdog should respond to fatal exceptions. <br>
     * <b>Note:</b> <u>This is intended for testing exception handling only, and should be removed before release.</u>
     */
    public FatalExceptionHandling fatalExceptionHandling;

    public boolean daemon;

    public boolean useWatchdog;

    public LaunchConfig(LogLevel logLevel, DebugMode debugMode, FatalExceptionHandling fatalExceptionHandling, boolean daemon) {
        this.logLevel = logLevel;
        this.debugMode = debugMode;
        this.fatalExceptionHandling = fatalExceptionHandling;
        this.daemon = daemon;
    }

    public static LaunchConfig defaultConfig() {
        return new LaunchConfig(LogLevel.INFO, DebugMode.OFF, FatalExceptionHandling.CRASH, false);
    }
}
