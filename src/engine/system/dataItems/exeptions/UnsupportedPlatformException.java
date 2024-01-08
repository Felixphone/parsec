package engine.system.dataItems.exeptions;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class UnsupportedPlatformException extends FatalTraceableException {

    public UnsupportedPlatformException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.UNSUPPORTED_PLATFORM_EXCEPTION, "UNSUPPORTED_PLATFORM_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }

    public UnsupportedPlatformException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.UNSUPPORTED_PLATFORM_EXCEPTION, "UNSUPPORTED_PLATFORM_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }
}
