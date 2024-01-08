package launcher;

import engine.engine.EngineCore;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.logger.entries.ConsoleColours;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.exitReport.ExitReport;
import launcher.launchConfig.DebugMode;
import tracerUtils.config.FatalExceptionHandling;
import launcher.launchConfig.LaunchConfig;
import tracerUtils.logger.entries.LogLevel;

import javax.swing.*;
import java.util.ArrayList;

public class Launcher {
    private static String[] args;

    /**
     * Holds the initial configuration passed into the program at first execution.<br>
     * <br>
     * Constructed from the String[] passed into {@link #main(String[])} by {@link #parseLaunchConfig(String[])}}.
     */
    private static LaunchConfig launchConfig;
    /**
     * Responsible for handling errors and unexpected behaviours and ensuring the {@link #engineCore} runs smoothly.<br>
     * <br>
     * Runs on the {@link #watchdogThread} separate from the main {@link #engineCore}.
     */
    private static EngineWatchdog engineWatchdog;
    /**
     * Hosts the {@link #engineWatchdog} separate from the main {@link #engineThread}.
     */
    private static Thread watchdogThread;
    /**
     * Responsible for all logic and functionality of the program, such as: graphics, physics, ui, ext.<br>
     * <br>
     * Runs on the {@link #engineThread} separate from the supervisory {@link #engineWatchdog}.
     */
    private static EngineCore engineCore;
    /**
     * Hosts the main {@link #engineCore} separate from the supervisory {@link #engineWatchdog}.
     */
    private static Thread engineThread;

    /**
     * The main entry point of the entire engine system and other components.<br>
     * <br>
     * <b>Note:</b> This main() method is also called when relaunching the program by {@link #relaunch(ExitReport)}.
     * @param args The runtime arguments passed into the program by the JVM when the program initially starts.
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("LAUNCHER_THREAD");
        Launcher.args = args;

        // save all log entries created before the loggers instantiation to be logged once instantiated
        ArrayList<LogEntry> startupLogEntries = new ArrayList<>();

        // construct launch config from runtime arguments
        launchConfig = parseLaunchConfig(args);

        startupLogEntries.add(new LogEntry(LogLevel.LAUNCHER, "Log level is set to " + launchConfig.logLevel.name() + "!", "", new ThreadState(Thread.currentThread())));

        // start watchdog
        startupLogEntries.add(new LogEntry(LogLevel.LAUNCHER, "Starting watchdog thread...", "", new ThreadState(Thread.currentThread())));
        engineWatchdog = new EngineWatchdog(launchConfig);
        watchdogThread = new Thread(engineWatchdog::start, "WATCHDOG_THREAD");
        watchdogThread.start();

        // start engine core
        startupLogEntries.add(new LogEntry(LogLevel.LAUNCHER, "Starting engine thread...", "", new ThreadState(Thread.currentThread())));
        engineCore = new EngineCore(launchConfig, EngineWatchdog.getLogger());
        engineThread = new Thread(engineCore::start, "CORE_ENGINE_THREAD");
        engineThread.start();

        // log all log entries created before logger instantiation
        for (LogEntry logEntry : startupLogEntries) {
            EngineWatchdog.getLogger().log(logEntry);
        }

        EngineWatchdog.getLogger().launcherLog("Successfully launched program!", "", new ThreadState(Thread.currentThread()));
    }

    /**
     * Constructs a {@link LaunchConfig} object from the String[] provided.
     * @param args A {@link String}[], where each element corresponds to configuration settings, which should be passed in as follows:<br>
     *              <br>
     *              0. <b>Log level:</b> must be a valid name of {@link LogLevel} (defaults to {@link LogLevel#INFO} if left unspecified)<br>
     *              1. <b>Debug mode:</b> must be a valid name of {@link DebugMode} (defaults to {@link DebugMode#OFF} if left unspecified)<br>
     *              2. <b>Fatal exception handling:</b> must be a valid name of {@link FatalExceptionHandling} (defaults to {@link FatalExceptionHandling#CRASH} if left unspecified)<br> <u><i>[this is intended for testing exception handling only and should be removed before release!]</i></u><br>
     *              <br>
     *             Arguments may be left unspecified to use default configuration, as long as all following arguments are also left unspecified.
     * @return {@link LaunchConfig} containing the configurations parsed from the String[].
     * @throws IllegalArgumentException if arguments do not conform to the above specification.
     * Warns: If args.length() is greater than 3.
     */
    private static LaunchConfig parseLaunchConfig(String[] args) throws IllegalArgumentException {

        // define defaults
        LogLevel logLevel = LogLevel.getDefault();
        DebugMode debugMode = DebugMode.OFF;
        FatalExceptionHandling fatalExceptionHandling = FatalExceptionHandling.CRASH;

        // parse args[0] for LogLevel
        if (args.length > 0) {
            try {
                logLevel = LogLevel.valueOf(args[0].toUpperCase());
            }
            catch (IllegalArgumentException e) {
                EngineWatchdog.getLogger().error("Illegal runtime argument at position 0.", "Found: \"" + args[0].toUpperCase() + "\" expected: \"ALL\", \"ULTRA_FINE\", \"FINE\", \"INFO\", \"ATTEMPT\", \"SUCCESS\", \"CRITICAL_ATTEMPT\", \"CRITICAL_SUCCESS\", \"WARNING\", \"ERROR\", \"FATAL\", \"LAUNCHER\", \"OFF\", (or \"\" for default where no argument follows)", new ThreadState(Thread.currentThread()));
                throw new IllegalArgumentException("Illegal runtime argument at position 0. Found: \"" + args[0].toUpperCase() + "\" expected: \"ALL\", \"ULTRA_FINE\", \"FINE\", \"INFO\", \"ATTEMPT\", \"SUCCESS\", \"CRITICAL_ATTEMPT\", \"CRITICAL_SUCCESS\", \"WARNING\", \"ERROR\", \"FATAL\", \"LAUNCHER\", \"OFF\", (or \"\" for default where no argument follows)");
            }
        }

        // parse args[1] for DebugMode
        if (args.length > 1) {
            try {
                debugMode = DebugMode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                EngineWatchdog.getLogger().error("Illegal runtime argument at position 1.", "Found: \"" + args[1].toUpperCase() + "\" expected: \"ON\", \"OFF\", (or \"\" for default where no argument follows)", new ThreadState(Thread.currentThread()));
                throw new IllegalArgumentException("Illegal runtime argument at position 1. Found: \"" + args[1].toUpperCase() + "\" expected: \"ON\", \"OFF\", (or \"\" for default where no argument follows)");
            }
        }

        // parse args[2] for FatalExceptionHandling
        if (args.length > 2) {
            try {
                fatalExceptionHandling = FatalExceptionHandling.valueOf(args[2].toUpperCase());
            }
            catch (IllegalArgumentException e) {
                EngineWatchdog.getLogger().error("Illegal runtime argument at position 2.", "Found: \"" + args[1].toUpperCase() + "\" expected: \"CRASH\", \"IGNORE\", (or \"\" for default where no argument follows)", new ThreadState(Thread.currentThread()));
                throw new IllegalArgumentException("Illegal runtime argument at position 2. Found: \"" + args[1].toUpperCase() + "\" expected: \"CRASH\", \"IGNORE\", (or \"\" for default where no argument follows)");
            }
        }

        // warn if additional arguments given
        if (args.length > 3) {
            EngineWatchdog.getLogger().warn("Unexpected runtime argument at position 3.", "Found \"" + args[3] + "\" expected nothing", new ThreadState(Thread.currentThread()));
        }

        // construct and return LaunchConfig
        return new LaunchConfig(logLevel, debugMode, fatalExceptionHandling, 0, 0);
    }

    /**
     * Relaunches the program.<br>
     * <br>
     * Ends the current instance of execution with {@link #endCurrentInstance(ExitReport)}, before calling the {@link #main(String[])} method, passing in the original {@link String}[] {@link #args} first supplied at initial execution.
     * @param exitReport The {@link ExitReport} containing the exit details of the current instance of execution.
     */
    public static void relaunch(ExitReport exitReport) {
        if (Thread.currentThread().getName() != "WATCHDOG_THREAD") {
            EngineWatchdog.getLogger().warn("<!> Relaunch() called from non-WATCHDOG_THREAD thread! <!>", "This method should ONLY ever be called from the WATCHDOG_THREAD", new ThreadState(Thread.currentThread()));
            return;
        }

        // end current instance to reset classes and variables
        endCurrentInstance(exitReport);

        System.out.println("");
        System.out.println(ConsoleColours.PURPLE + "############################### RELAUNCH: ##############################");
        System.out.println(ConsoleColours.PURPLE + "Attempting to relaunch engine..." + ConsoleColours.RESET);

        // launch new instance
        main(args);
        System.out.println(ConsoleColours.PURPLE + "Relaunched, ending old thread..." + ConsoleColours.RESET);
        // thread should end here
    }

    /**
     * Exits the entire JVM instance and the program execution completely.<br>
     * <br>
     * Calls {@link #endCurrentInstance(ExitReport)} and then {@link System#exit(int)}.
     * @param exitReport The {@link ExitReport} containing the exit details of the current instance of execution.
     */
    public static void exit(ExitReport exitReport) {
        if (Thread.currentThread().getName() != "WATCHDOG_THREAD") {
            EngineWatchdog.getLogger().warn("<!> Exit() called from non-WATCHDOG_THREAD thread! <!>", "This method should ONLY ever be called from the WATCHDOG_THREAD", new ThreadState(Thread.currentThread()));
            return;
        }
        // end current instance
        endCurrentInstance(exitReport);

        // exit JVM
        System.exit(exitReport.getExitCode());
    }

    /**
     * Exits the current instance of execution.<br>
     * <br>
     * Closes the {@link #engineCore} by calling {@link EngineCore#close()}, then waits for the {@link #engineThread} to finish execution with {@link Thread#interrupt()} and {@link Thread#join()}, before finally closing the {@link #engineWatchdog} with {@link EngineWatchdog#close()}.
     * @param exitReport
     */
    private static void endCurrentInstance(ExitReport exitReport) {
        EngineWatchdog.getLogger().engineAttempt("Ending program ( Exit code: " + exitReport.getExitCode() + " / " + exitReport.getQuitReason().name() + " ) ...", "", new ThreadState(Thread.currentThread()));

        // stop engine
        engineCore.close();

        unsafeExit(0, "test");

        // wait for engineThread to finish execution
        EngineWatchdog.getLogger().engineAttempt("Terminating engine thread...", "", new ThreadState(Thread.currentThread()));
        engineThread.interrupt();
        try {
            engineThread.join();
        } catch (InterruptedException e) {
            EngineWatchdog.getLogger().error("Error trying to terminate current instance: Thread interrupted whilst trying to join engineThread:", e.getMessage() + ". The WATCHDOG_THREAD should never be interrupted, please check that you have not unexpectedly interrupted the wrong thread. Program will be unsafely terminated.", new ThreadState(Thread.currentThread()));
            unsafeExit(exitReport.getExitCode(), "WATCHDOG_THREAD interrupted whilst trying to join engineThread");
        }
        engineThread = null;

        //close watchdog
        EngineWatchdog.getLogger().engineAttempt("Closing Watchdog...", "", new ThreadState(Thread.currentThread()));
        engineWatchdog.close();
        // no need to stop watchdog thread as it should be the current thread (and may become the new launcher thread)

        System.out.println("Exited launcher ( Exit code: " + exitReport.getExitCode() + " / " + exitReport.getQuitReason().name() + " )");
    }

    private static void unsafeExit(int exitCode, String reason) {
        System.out.println(ConsoleColours.RED + "[Launcher]: <!> CRITICAL ERROR <!>: unsafeExit() was called with reason: \"" + reason + "\", and exit code: " + exitCode + ". This means that a severe error was encountered which could not be resolved, even at the launcher level. The program will be unsafely terminated, with no further action, which may result in data loss or corruption. If you would like to submit a bug report, please visit: https://parsec-thegame.com/bugreport.");
        try {
            String ls = System.lineSeparator();
            JOptionPane.showMessageDialog(null, "Critical error, unsafeExit() called:" + ls
                    + "====================================================================================================" + ls
                    + "Reason: " + reason + ls
                    + "Exit code: " + exitCode + ls
                    + "====================================================================================================" + ls
                    + "If you are seeing this, it means that a severe error was encountered which could not be resolved, even at the launcher level." + ls
                    + "The program will be unsafely terminated, with no further action, which may result in data loss or corruption." + ls
                    + "If you would like to submit a bug report, please visit: https://parsec-thegame.com/bugreport." + ls
                    + "====================================================================================================",
                    "PARSEC | CRITICAL ERROR:", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            System.out.println("Error displaying critical-error box? Come on...! " + t.getMessage());
            // might as well give up at this point
        }
        System.exit(exitCode);
    }
}
