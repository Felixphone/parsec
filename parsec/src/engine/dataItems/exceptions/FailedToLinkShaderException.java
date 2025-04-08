package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class FailedToLinkShaderException extends TraceableException {

    public FailedToLinkShaderException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("FAILED_TO_LINK_SHADER_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public FailedToLinkShaderException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("FAILED_TO_LINK_SHADER_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
