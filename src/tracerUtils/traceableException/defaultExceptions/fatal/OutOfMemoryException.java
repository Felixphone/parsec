package tracerUtils.traceableException.defaultExceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class OutOfMemoryException extends FatalTraceableException {

    public OutOfMemoryException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.OUT_OF_MEMORY, "OUT_OF_MEMORY_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }

    public OutOfMemoryException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.OUT_OF_MEMORY, "OUT_OF_MEMORY_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }
}
