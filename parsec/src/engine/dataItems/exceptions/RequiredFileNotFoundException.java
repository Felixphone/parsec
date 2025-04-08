package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class RequiredFileNotFoundException extends TraceableException {

    public RequiredFileNotFoundException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("REQUIRED_FILE_NOT_FOUND_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public RequiredFileNotFoundException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("REQUIRED_FILE_NOT_FOUND_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
