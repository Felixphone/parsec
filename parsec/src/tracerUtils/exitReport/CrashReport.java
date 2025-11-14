package tracerUtils.exitReport;

import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;
import tracerUtils.traceableException.FatalTraceableException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Extends {@link ExitReport}. Holds a series of information about why and how the program exited or wants to exit.<br>
 * <br>
 * This is particularly useful for debugging and tracebacks.
 * @see ExitReport
 */
public class CrashReport extends ExitReport {

    private FatalTraceableException exception;
    private String phrase = "Splash not working? Oh dear!";

    public CrashReport(FatalTraceableException fatalCoreException, String programVersion, String programState, int additionalWarnings) {
        super(fatalCoreException.getQuitReason(), programVersion, programState, additionalWarnings);
        EngineWatchdog.getLogger().engineAttempt("Converting exit report to crash report...", "", new ThreadState(Thread.currentThread()));

        isClean = false;
        this.exception = fatalCoreException;

        // just for fun :)
        try {
            Random random = new Random();
            String[] phrases = new String[]{"Oopsie...", "Was that me?", "Here we go again...", "Ouch!", "Is that bad?...", "Is that a bad thing?", "Good dog!", "Nice catch!", "It's a big'n!", "Have a hug!", "Nooooooooooo...", "Arghhh!"};
            phrase = phrases[random.nextInt(phrases.length-1)];
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    public FatalTraceableException getException() {
        return exception;
    }

    public String getPhrase() {
        return phrase;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();

        info.add("################ Engine Crash report: ###############");
        info.add("[Start crash report @ [" + new Date().toString() + "]]");
        info.add("//" + phrase);

        info.add("");
        info.add("=== Exception info: ===");

        // get the exception info and add each line, concatenating the spaces before
        for (String line : exception.getInfo()) {
            info.add(" |  " + line);
        }

        // add basic engine info
        info.add("");
        info.add("=== Program Info: ===");
        info.add("Program version: " + programVersion);
        info.add("Program state: " + programState);

        // add system info
        info.add("");
        info.add("=== System Info: ===");
        //info.addAll(systemInfo.getInfo());

        info.add("");
        info.add("########## Additional reports: ##########");
        info.add("");
        info.add("=== Additional Reported Warnings: ===");

        for (LogEntry entry : EngineWatchdog.getLogger().getLog().getEntries()) {
            if (entry.getLogLevel() == LogLevel.WARNING) {
                ArrayList<String> exceptionInfo = entry.getInfo();

                for (String line : exceptionInfo) {
                    info.add(" | " + line);
                }
            }
        }

        info.add("");
        info.add("=== Additional Reported Errors: ===");

        for (LogEntry entry : EngineWatchdog.getLogger().getLog().getEntries()) {
            if (entry.getLogLevel() == LogLevel.ERROR) {
                ArrayList<String> exceptionInfo = entry.getInfo();

                for (String line : exceptionInfo) {
                    info.add(" | " + line);
                }
            }
        }

        info.add("[End crash report @ [" + new Date().toString() + "]]");
        info.add("###################################################");

        return info;
    }
}
