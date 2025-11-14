package tracerUtils.exitReport;

import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Holds a series of information about why and how the program exited or wants to exit.<br>
 * <br>
 * This is particularly useful for debugging and tracebacks in case of unexpected exits or crashes.
 * @see CrashReport
 */
public class ExitReport {
    protected QuitReason quitReason;
    protected int exitCode;
    protected boolean isClean;
    protected String programVersion;
    protected String programState;
    protected int additionalWarnings;

    public ExitReport(QuitReason quitReason, String programVersion, String programState, int additionalWarnings) {
        EngineWatchdog.getLogger().engineAttempt("Constructing exit report, please wait...", "", new ThreadState(Thread.currentThread()));
        this.quitReason = quitReason;
        exitCode = quitReason.getExitCode();
        this.programVersion = programVersion;
        this.programState = programState;
        this.additionalWarnings = additionalWarnings;
        isClean = true;
    }

    public QuitReason getQuitReason() {
        return quitReason;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isClean() {
        return isClean;
    }

    public String getProgramVersion() {
        return programVersion;
    }

    public String getProgramState() {
        return programState;
    }

    public int getAdditionalWarnings() {
        return additionalWarnings;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();

        info.add("################ Engine Exit report: ###############");
        info.add("[Start exit report @ [" + new Date().toString() + "]]");
        info.add("");

        info.add("=== Exit Info: ===");
        info.add("Type: " + quitReason.name());
        info.add("Exit code: " + exitCode);

        info.add("");
        info.add("=== Program Info: ===");
        info.add("Program version: " + programVersion);
        info.add("Program state: " + programState);

        info.add("");
        info.add("=== System Info: ===");
        //info.addAll(systemInfo.getInfo());

        info.add("");
        info.add("########## Additional reports: ##########");
        info.add("");
        info.add("=== Additional Reported Warnings: ===");
        info.add("[most recent last]");

        for (LogEntry entry : EngineWatchdog.getLogger().getLog().getEntries()) { // TODO: Concurrent modification exception thrown here when quitting program
            if (entry.getLogLevel() == LogLevel.WARNING) {
                ArrayList<String> exceptionInfo = entry.getInfo();

                for (String line : exceptionInfo) {
                    info.add(" | " + line);
                }
            }
        }

        info.add("");
        info.add("=== Additional Reported Errors: ===");
        info.add("[most recent last]");

        for (LogEntry entry : EngineWatchdog.getLogger().getLog().getEntries()) {
            if (entry.getLogLevel() == LogLevel.ERROR) {
                ArrayList<String> exceptionInfo = entry.getInfo();

                for (String line : exceptionInfo) {
                    info.add(" | " + line);
                }
            }
        }

        info.add("[End exit report @ [" + new Date().toString() + "]]");
        info.add("###################################################");

        return info;
    }

    @Override
    public String toString() {
        String msg = "";

        for (String line : getInfo()) {
            msg += line + "\n";
        }

        return msg;
    }
}
