package launcher.watchdog.exitHandler.exitHandler;

import launcher.watchdog.EngineWatchdog;
import launcher.watchdog.exitHandler.crashHandler.CrashHandler;
import tracerUtils.exitReport.ExitReport;

/**
 * An abstract class where inheriting classes must implement a {@link #onExit(ExitReport)} method, which dictate the actions which should be undertaken when the {@link EngineWatchdog} receives a <b>clean</b> {@link ExitReport}.
 * @see CrashHandler
 * @see EngineWatchdog
 */
public abstract class ExitHandler {

    /**
     * The actions which should be undertaken when the {@link EngineWatchdog} receives a <b>clean</b> {@link ExitReport}.
     * @param exitReport The {@link ExitReport} the {@link EngineWatchdog} receives.
     */
    public abstract void onExit(ExitReport exitReport);
}
