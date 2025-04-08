package launcher.watchdog.exitHandler.crashHandler;

import tracerUtils.exitReport.*;
import launcher.watchdog.exitHandler.exitHandler.ExitHandler;
import launcher.watchdog.EngineWatchdog;

/**
 * An abstract class where inheriting classes must implement a {@link #onCrash(CrashReport)} method, which dictate the actions which should be undertaken when the {@link EngineWatchdog} receives an <b>unclean</b> {@link CrashReport}.
 * @see ExitHandler
 * @see EngineWatchdog
 */
public abstract class CrashHandler {

    /**
     * The actions which should be undertaken when the {@link EngineWatchdog} receives an <b>unclean</b> {@link CrashReport}.
     * @param crashReport The {@link CrashReport} the {@link EngineWatchdog} receives.
     */
    public abstract void onCrash(CrashReport crashReport);
}
