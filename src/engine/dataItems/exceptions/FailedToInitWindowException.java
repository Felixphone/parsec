package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class FailedToInitWindowException extends TraceableException {

    public FailedToInitWindowException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("FAILED_TO_INIT_WINDOW_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public FailedToInitWindowException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("FAILED_TO_INIT_WINDOW_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
