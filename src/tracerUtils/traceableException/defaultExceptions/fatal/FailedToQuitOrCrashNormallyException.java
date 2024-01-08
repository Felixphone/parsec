package tracerUtils.traceableException.defaultExceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class FailedToQuitOrCrashNormallyException extends FatalTraceableException {

    public FailedToQuitOrCrashNormallyException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.FAILED_TO_QUIT_OR_CRASH_NORMALLY, "UHH... just BAD i guess", message, detailedMessage, causeMessage, cause, threadState);
    }
}
