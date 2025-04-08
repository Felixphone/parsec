package launcher.watchdog.exitHandler.crashHandler;

import launcher.Launcher;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.crash.CrashDialogue;
import tracerUtils.exitReport.CrashReport;

public class DefaultCrashHandler extends CrashHandler {

    @Override
    public void onCrash(CrashReport crashReport) {
        CrashDialogue crashDialogue = new CrashDialogue(crashReport, EngineWatchdog.getLogger());
        crashDialogue.open();

        if (crashDialogue.shouldRelaunch()) {
            Launcher.relaunch(crashReport);
        } else {
            Launcher.exit(crashReport);
        }
    }
}
