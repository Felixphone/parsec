package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class InsufficientBindingsException extends TraceableException {

    public InsufficientBindingsException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("INSUFFICIENT_BINDINGS_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public InsufficientBindingsException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("INSUFFICIENT_BINDINGS_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
