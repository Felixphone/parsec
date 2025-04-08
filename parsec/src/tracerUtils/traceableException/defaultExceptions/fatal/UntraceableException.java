package tracerUtils.traceableException.defaultExceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;
import tracerUtils.traceableException.TraceableException;

public class UntraceableException {

    private Throwable exception;

    public UntraceableException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
