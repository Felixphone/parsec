package launcher.watchdog.exitHandler.exitHandler;

import launcher.Launcher;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.ExitDialogue;
import tracerUtils.exitReport.ExitReport;

public class DefaultExitHandler extends ExitHandler {

    @Override
    public void onExit(ExitReport exitReport) {
        EngineWatchdog.getLogger().engineAttempt("Creating exit dialogue...", "", new ThreadState(Thread.currentThread()));
        ExitDialogue exitDialogue = new ExitDialogue(exitReport, EngineWatchdog.getLogger());
        EngineWatchdog.getLogger().engineAttempt("Opening exit dialogue...", "", new ThreadState(Thread.currentThread()));
        exitDialogue.open();

        EngineWatchdog.getLogger().engineSuccess("Exit dialogue closed!", "", new ThreadState(Thread.currentThread()));

        if (exitDialogue.shouldRelaunch()) {
            EngineWatchdog.getLogger().engineAttempt("Relaunch requested,", " Attempting to relaunch game", new ThreadState(Thread.currentThread()));
            Launcher.relaunch(exitReport);
        }
        else {
            Launcher.exit(exitReport);
        }
    }
}
