package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class InvalidInputException extends TraceableException {

    public InvalidInputException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("INVALID_INPUT_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public InvalidInputException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("INVALID_INPUT_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
