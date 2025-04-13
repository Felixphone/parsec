package launcher.watchdog;

import engine.engine.EngineCore;
import launcher.Launcher;
import launcher.launchConfig.LaunchConfig;
import launcher.watchdog.exitHandler.crashHandler.CrashHandler;
import launcher.watchdog.exitHandler.crashHandler.DefaultCrashHandler;
import launcher.watchdog.exitHandler.exitHandler.DefaultExitHandler;
import launcher.watchdog.exitHandler.exitHandler.ExitHandler;
import launcher.watchdog.notifications.IsAliveNotification;
import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.CrashReport;
import tracerUtils.exitReport.ExitReport;
import tracerUtils.logger.Logger;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;
import tracerUtils.traceableException.defaultExceptions.fatal.FailedToQuitOrCrashNormallyException;

import java.util.Date;

/**
 * Responsible for handling errors and unexpected behaviours and ensuring the {@link EngineCore} runs smoothly.
 */
public class EngineWatchdog {

    /**
     * Holds the number of times the watchdog has attempted to exit the current instance.<br>
     * <br>
     * If this exceeds {@link #MAX_EXIT_ATTEMPTS}, the current instance is forcefully terminated.
     */
    private static int exitAttempts;
    /**
     * The maximum number of times the watchdog is permitted to attempt to exit.<br>
     * <br>
     * Once {@link #exitAttempts} exceeds this value, the program will be forcefully terminated.
     */
    private static final int MAX_EXIT_ATTEMPTS = 10;
    /**
     * The current {@link ExitHandler} to be used when the current instance exits upon an {@link ExitReport} being received by {@link #onExitReport(ExitReport)}.
     */
    private ExitHandler exitHandler;
    /**
     * The current {@link CrashHandler} to be used when the current instance crashes upon a {@link CrashReport} being received by {@link #onExitReport(ExitReport)}.
     */
    private CrashHandler crashHandler;
    /**
     * <b>Null</b> until an {@link ExitReport} is received by {@link #submitExitReport(ExitReport)}, which sets this value to the {@link ExitReport} received.<br>
     * <br>
     * This causes the <b>WATCHDOG_THREAD</b> to break out of the while loop in {@link #start()}, and handle the {@link ExitReport} accordingly.
     */
    private static volatile ExitReport exitReport;

    private static volatile IsAliveNotification lastAliveNotification;

    /**
     * The {@link Logger} to be used by both the {@link EngineWatchdog} and {@link EngineCore} to record {@link LogEntry}s.
     */
    private static Logger logger;

    /**
     * Instantiates the watchdog, creating the {@link Logger} and exitHandlers.
     * @param launchConfig The {@link LaunchConfig} created by the {@link Launcher} upon first execution.
     */
    public EngineWatchdog(LaunchConfig launchConfig) {
        String logPath = "logs/parsec_" + String.valueOf(new Date()).replace(" ", "_").replace(":", "_") + ".log";
        logger = new Logger();
        logger.newLog("PARSEC_LOG", logPath, launchConfig.logLevel, LogLevel.ALL);

        exitHandler = new DefaultExitHandler();
        crashHandler = new DefaultCrashHandler();
    }

    /**
     * Starts the watchdog.<br>
     * <br>
     * Enters a while loop, calling {@link #update()} repeatedly until an {@link ExitReport} is submitted to {@link #submitExitReport(ExitReport)}, and {@link #exitReport} is no longer <b>null</b>.<br>
     * <br>
     * The watchdog will then attempt to handle the {@link ExitReport} by calling {@link #onExitReport(ExitReport)}. If it encounters an exception, it will try to exit again, incrementing the value of {@link #exitAttempts} until it exceeds the value of {@link #MAX_EXIT_ATTEMPTS}, at which point the program will be forcefully terminated.
     */
    public void start() {
        while (exitReport == null) {
            update();
        }

        // ExitReport submitted, try to handle accordingly
        while (exitAttempts < MAX_EXIT_ATTEMPTS) {
            exitAttempts++;
            try {
                onExitReport(exitReport);
                return;
            } catch (Throwable t) {
                t.printStackTrace();
                CrashReport crashReport = new CrashReport(new FailedToQuitOrCrashNormallyException("Something went wrong...", "An exception was encountered whilst trying to quit the program for an unknown reason", "Exception encountered whilst trying to quit", t, new ThreadState(Thread.currentThread())), EngineCore.PHOENIX3D_EL_VERSION,"CLOSED", 0);
                onExitReport(crashReport);
            }
        }

        // Exceeded maximum quit attempts, forcefully terminate program
        System.out.println("<!> EXCEEDED MAXIMUM QUIT ATTEMPTS, kept encountering exceptions whilst trying to quit <!>");
        Launcher.exit(exitReport);
    }

    /**
     * Runs tasks to ensure the {@link EngineCore} is running as expected.
     */
    public void update() {
        //System.out.println("TEST");
    }

    /**
     * Sets the {@link #exitReport} to the {@link ExitReport} passed in.<br>
     * <br>
     * This causes the <b>WATCHDOG_THREAD</b> to break out of the while loop in {@link #start()}, to then handle the report accordingly.
     * @param exitReport The {@link ExitReport} which {@link #exitReport} should be set to.
     */
    public static void submitExitReport(ExitReport exitReport) {
        EngineWatchdog.exitReport = exitReport;
    }

    /**
     * Handles exiting of program when an {@link ExitReport} is submitted.<br>
     * <br>
     * Called by {@link #start()} when {@link #exitReport} becomes non-null, signifying the program wants to exit.<br>
     * <br>
     * Logs the {@link ExitReport}, and then calls either {@link ExitHandler#onExit(ExitReport)} on {@link #exitHandler}, or {@link CrashHandler#onCrash(CrashReport)} on {@link #crashHandler}, depending on the value of {@link ExitReport#isClean()}, to handle the report accordingly.
     * @param exitReport
     */
    private void onExitReport(ExitReport exitReport) {

        // log report
        logger.newLine();
        logger.launcherLog("################ Engine Exit report: ###############", "\n" + exitReport.toString(), new ThreadState(Thread.currentThread()));

        if (exitReport.isClean()) {
            exitHandler.onExit(exitReport);
        }
        else {
            crashHandler.onCrash((CrashReport) exitReport);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Closes the watchdog when the program is ready to terminate or restart
     */
    public void close() {
        // reset static variables
        exitReport = null;
        lastAliveNotification = null;

        // close logger
        try {
            logger.engineAttempt("Closing Logger...", "", new ThreadState(Thread.currentThread()));
            logger.close(exitReport);
        } catch (Throwable e) {
            logger.warn("Failed to close log:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }
    }
}
