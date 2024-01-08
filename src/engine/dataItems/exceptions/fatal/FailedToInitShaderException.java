package engine.dataItems.exceptions.fatal;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class FailedToInitShaderException extends FatalTraceableException {

    public FailedToInitShaderException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.FAILED_TO_INIT_SHADER_EXCEPTION, "FAILED_TO_INIT_SHADER_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }

    public FailedToInitShaderException(
            String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.FAILED_TO_INIT_SHADER_EXCEPTION, "FAILED_TO_INIT_SHADER_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }
}
