package tracerUtils.logger;

import tracerUtils.crash.LiveLog;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.entries.ConsoleColours;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Log {

    private static final String THIS_THREAD_SOURCE = "[TRACER_UTILS/LOG]";

    private String name;
    private Date initTime;
    private String logFilePath;
    private File log;
    private FileWriter logWriter;
    private boolean generatedFiles = false;
    private boolean closed = false;
    private boolean deleted = false;

    private LogLevel fileLogLevel;
    private LogLevel consoleLogLevel;

    private ArrayList<LogEntry> entries;
    private LiveLog liveLog;

    public Log(String name, String path, LogLevel consoleLogLevel, LogLevel fileLogLevel) {
        initTime = new Date();
        entries = new ArrayList<>();
        liveLog = new LiveLog();
        //liveLog.open();

        this.name = name;
        this.logFilePath = path;
        this.consoleLogLevel = consoleLogLevel;
        this.fileLogLevel = fileLogLevel;
    }

    public void generateFiles() {
        if (generatedFiles) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Already generated log file" + ConsoleColours.RESET);
            return;
        }

        try {
            log = new File(logFilePath);

            if (log.getParentFile().mkdirs()) { //check if it needs to generate parent dirs
                System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " Generated log directory at: " + logFilePath + ConsoleColours.RESET);
            }

            if (log.createNewFile()) {
                System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " Created log file at: " + log.getAbsolutePath() + ConsoleColours.RESET);
            } else {
                System.out.println(ConsoleColours.YELLOW + THIS_THREAD_SOURCE + " Overwriting log file at: " + log.getAbsolutePath() + ConsoleColours.RESET);
            }
            generatedFiles = true;
        } catch (IOException e) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Failed to create log: " + e.getMessage() + ConsoleColours.RESET);
        }

        try {
            logWriter = new FileWriter(logFilePath);
            logWriter.write("============ " + name + ":  Initiated at: " + initTime + " ============ \n");
        } catch (IOException e) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Failed to create logWriter " + e.getMessage() + ConsoleColours.RESET);
        }
    }

    public void add(LogEntry logEntry) {
        entries.add(logEntry);
        //liveLog.addEntry(logEntry);
        if (logEntry.getLogLevel().getLevel() >= consoleLogLevel.getLevel()) {
            print(logEntry);
        }

        if (logEntry.getLogLevel().getLevel() >= fileLogLevel.getLevel()) {
            writeToFile(logEntry);
        }
    }

    private void print(LogEntry logEntry) {
        System.out.println(logEntry.toColouredString() + " { from log }"); //TODO: doesnt work for some reason
    }

    private void writeToFile(LogEntry logEntry) {
        if (!generatedFiles) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: <!> COULDN'T LOG TO FILE <!>: log file not yet generated!" + ConsoleColours.RESET);
            return;
        }
        if (closed) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: <!> COULDN'T LOG TO FILE <!>: log has already been closed!" + ConsoleColours.RESET);
            return;
        }

        try {
            logWriter.append(logEntry.toString()).append(String.valueOf('\n'));
            logWriter.flush();
        } catch (Exception e) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE +  " <!> COULDN'T LOG TO FILE! <!> : " + e.getMessage()  + ConsoleColours.RESET);
        }
    }

    public void newLine() {
        try {
            logWriter.append(String.valueOf('\n'));
            logWriter.flush();
        } catch (Exception e) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE +  " <!> COULDN'T LOG TO FILE! <!> : " + e.getMessage()  + ConsoleColours.RESET);
        }
    }

    public String getName() {
        return name;
    }

    public Date getInitTime() {
        return initTime;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public LogLevel getFileLogLevel() {
        return fileLogLevel;
    }

    public void setFileLogLevel(LogLevel fileLogLevel) {
        this.fileLogLevel = fileLogLevel;
    }

    public LogLevel getConsoleLogLevel() {
        return consoleLogLevel;
    }

    public void setConsoleLogLevel(LogLevel consoleLogLevel) {
        this.consoleLogLevel = consoleLogLevel;
    }

    public ArrayList<LogEntry> getEntries() {
        return entries;
    }

    public boolean close() {
        if (closed) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Couldn't close log as log has already been closed!" + ConsoleColours.RESET);
            return false;
        }

        add(new LogEntry(LogLevel.ATTEMPT, "Closing log ( Entry count: " + String.format("%,d", entries.size()) + " ) ...", "", new ThreadState(Thread.currentThread())));
        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Couldn't close log: " + e.getMessage() + ConsoleColours.RESET);
                return false;
            }
        }
        closed = true;
        System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " Saved log to: " + logFilePath + ConsoleColours.RESET);
        return true;
    }

    public boolean delete() {
        if (!closed) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Couldn't delete log as log has not yet been closed!" + ConsoleColours.RESET);
            return false;
        }

        if (deleted) {
            System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error: Couldn't delete log as log has already been deleted!" + ConsoleColours.RESET);
            return false;
        }

        if (log.delete()) {
            deleted = true;
            System.out.println(ConsoleColours.GREEN + THIS_THREAD_SOURCE + " Deleted log file!" + ConsoleColours.RESET);
            return true;
        }

        System.out.println(ConsoleColours.RED + THIS_THREAD_SOURCE + " Error trying to delete log file!" + ConsoleColours.RESET);
        return false;
    }
}
