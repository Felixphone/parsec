package tracerUtils.traceableException.defaultExceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class SimulatedException extends FatalTraceableException {

    public SimulatedException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.SIMULATED_EXCEPTION, "SIMULATED_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }

    public SimulatedException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.SIMULATED_EXCEPTION, "SIMULATED_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }
}
