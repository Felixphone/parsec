package launcher.watchdog.exitHandler.exitHandler;

import launcher.Launcher;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.exitReport.ExitDialogue;
import tracerUtils.exitReport.ExitReport;
import tracerUtils.logger.entries.ConsoleColours;

public class DefaultExitHandler extends ExitHandler {

    @Override
    public void onExit(ExitReport exitReport) {
        ExitDialogue exitDialogue = new ExitDialogue(exitReport, EngineWatchdog.getLogger());
        exitDialogue.open();

        if (exitDialogue.shouldRelaunch()) {
            Launcher.relaunch(exitReport);
        }
        else {
            Launcher.exit(exitReport);
        }
    }
}
