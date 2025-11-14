package tracerUtils.logger;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.ExitReport;
import tracerUtils.logger.entries.ConsoleColours;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessage;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessageTextElement;
import tracerUtils.traceableException.FatalTraceableException;
import tracerUtils.traceableException.TraceableException;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger {

    private static final String THIS_THREAD_SOURCE = "[TRACER_UTILS/LOGGER]";
    private static Thread loggerThread;

    private Log log;

    private volatile boolean shouldProcessEntries = true;
    private volatile BlockingQueue<LogEntry> pendingEntries = new LinkedBlockingQueue<>();

    public Logger() {
        startProcessingEntries();
    }

    public Log getLog() {
        return log;
    }

    public void newLog(String logName, String logFilePath, LogLevel consoleLogLevel, LogLevel fileLogLevel) {
        log = new Log(logName, logFilePath, consoleLogLevel, fileLogLevel);
        log.generateFiles();
        System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " File logging is enabled!" + ConsoleColours.RESET);
    }

    public void newConsoleLog(String logName, String logFilePath, LogLevel consoleLogLevel) {
        log = new Log(logName, logFilePath, consoleLogLevel, LogLevel.OFF);
        System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " File logging is disabled! (enable it in LoggerConstants)" + ConsoleColours.RESET);
    }
    
    public void newLine() {
        System.out.println(" ");
        log.newLine();
    }

    public void launcherLog(String msg, String detailedMessage, ThreadState threadState) {
        try {
            log(new LogEntry(LogLevel.LAUNCHER, msg, detailedMessage, threadState));
        } catch (Exception e) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: <!> COULDN'T LOG MESSAGE <!>: " + e.getMessage() + ConsoleColours.RESET);
        }
    }

    public void ultraFine(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.ULTRA_FINE, msg, detailedMessage, threadState));
    }

    public void fine(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.FINE, msg, detailedMessage, threadState));
    }

    public void info(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.INFO, msg, detailedMessage, threadState));
    }

    public void attempt(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.ATTEMPT, msg, detailedMessage, threadState));
    }

    public void success(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.SUCCESS, msg, detailedMessage, threadState));
    }

    public void engineAttempt(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.CRITICAL_ATTEMPT, msg, detailedMessage, threadState));
    }

    public void engineSuccess(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.CRITICAL_SUCCESS, msg, detailedMessage, threadState));
    }

    public void warn(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.WARNING, msg, detailedMessage, threadState));
    }

    public void error(String msg, String detailedMessage, ThreadState threadState) {
        log(new LogEntry(LogLevel.ERROR, msg, detailedMessage, threadState));
    }

    public void error(String msg, String detailedMessage, Throwable e, ThreadState threadState) {
        ExtendedMessage extendedMessage = new ExtendedMessage();
        if (e instanceof TraceableException) {
            extendedMessage.add(new ExtendedMessageTextElement(((TraceableException) e).getInfo()));
            log(new LogEntry(LogLevel.ERROR, msg, detailedMessage, extendedMessage, threadState));
        }
        else {
            extendedMessage.add(new ExtendedMessageTextElement(formatStackTrace(e.getStackTrace())));
            log(new LogEntry(LogLevel.ERROR, msg, detailedMessage, extendedMessage, threadState));
        }
    }

    public void fatal(String msg, String detailedMessage, FatalTraceableException e, ThreadState threadState) {
        ExtendedMessage extendedMessage = new ExtendedMessage();
        extendedMessage.add(new ExtendedMessageTextElement(e.getInfo()));
        log(new LogEntry(LogLevel.FATAL, msg, detailedMessage, extendedMessage, threadState));
    }

    public void log(LogEntry entry) {
        pendingEntries.add(entry);
    }

    private void startProcessingEntries() {
        loggerThread = new Thread(this :: processPendingEntries);
        loggerThread.setName("LOGGER_THREAD");
        loggerThread.start();
    }

    private void processPendingEntries() {
        while (shouldProcessEntries) {
            if (log != null) {
                ArrayList<LogEntry> entries = new ArrayList<>();
                pendingEntries.drainTo(entries);
                for (LogEntry logEntry : entries) {
                    log.add(logEntry); // TODO: error: nullptr to log if log still being created
                }
            }
        }
    }

    public static ArrayList<String> formatStackTrace(StackTraceElement[] stackTraceElements) {
        ArrayList<String> stack = new ArrayList<>();
        stack.add(" ==== Stack ====");
        stack.add(" [top of stack]");
        for (StackTraceElement traceElement : stackTraceElements) {
            stack.add(" =>   " + traceElement.toString());
        }
        return stack;
    }

    public void close(ExitReport exitReport) {

        shouldProcessEntries = false;
        log.close();

        System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " DELETE_LOG_ON_CLEAN_EXIT = " + LoggerConstants.DELETE_LOG_ON_CLEAN_EXIT + " | DELETE_LOG_FILE_REGARDLESS = " + LoggerConstants.DELETE_LOG_FILE_REGARDLESS + "" + ConsoleColours.RESET);

        if (exitReport.isClean()) {
            if (LoggerConstants.DELETE_LOG_ON_CLEAN_EXIT || LoggerConstants.DELETE_LOG_FILE_REGARDLESS) {
                log.delete();
            }
            System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " Clean exit... goodbye!");
            return;
        }
        System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Unclean exit... oh dear!");
    }
}
