package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class FailedToInitGLFWException extends TraceableException {

    public FailedToInitGLFWException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("FAILED_TO_INIT_GLFW_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public FailedToInitGLFWException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("FAILED_TO_INIT_GLFW_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
