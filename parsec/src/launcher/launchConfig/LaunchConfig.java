package launcher.launchConfig;

import tracerUtils.logger.entries.*;
import tracerUtils.config.FatalExceptionHandling;

/**
 * Holds the initial configuration passed into the program at first execution.
 * <br>
 * Used to change specific behaviors of the engine and watchdog systems.
 */
public class LaunchConfig {

    /**
     * Specifies the minimum {@link LogLevel} of which {@link tracerUtils.logger.entries.LogEntry}s will be logged.
     */
    public final LogLevel logLevel;
    /**
     * Specifies whether debug mode is enabled or not.
     */
    public final DebugMode debugMode;
    /**
     * Specifies how the watchdog should respond to fatal exceptions. <br>
     * <b>Note:</b> <u>This is intended for testing exception handling only, and should be removed before release.</u>
     */
    public final FatalExceptionHandling fatalExceptionHandling;
    public final float FPS;
    public final float UPS;

    public LaunchConfig(LogLevel logLevel, DebugMode debugMode,FatalExceptionHandling fatalExceptionHandling, float fps, float ups) {
        this.logLevel = logLevel;
        this.debugMode = debugMode;
        this.fatalExceptionHandling = fatalExceptionHandling;
        FPS = fps;
        UPS = ups;
    }
}
