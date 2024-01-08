package game.dataItems.exeptions;

import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public class ConcurrentWorldLoadException extends TraceableException {

    public ConcurrentWorldLoadException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super("CONCURRENT_WORLD_LOAD_EXCEPTION", message, detailedMessage, causeMessage, threadState);
    }

    public ConcurrentWorldLoadException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super("CONCURRENT_WORLD_LOAD_EXCEPTION", message, detailedMessage, causeMessage, cause, threadState);
    }
}
