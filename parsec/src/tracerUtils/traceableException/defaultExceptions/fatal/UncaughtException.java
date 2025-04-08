package tracerUtils.traceableException.defaultExceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class UncaughtException extends FatalTraceableException {


    public UncaughtException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.UNCAUGHT_EXCEPTION, "UNCAUGHT_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }

    public UncaughtException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.UNCAUGHT_EXCEPTION, "UNCAUGHT_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }
}
