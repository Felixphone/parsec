package engine.dataItems.exceptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class FailedToCompileShaderException extends TraceableException {

    public FailedToCompileShaderException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("FAILED_TO_COMPILE_SHADER_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public FailedToCompileShaderException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("FAILED_TO_COMPILE_SHADER_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
