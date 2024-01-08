package game.dataItems.exeptions;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.traceableException.FatalTraceableException;

public class PlayerPosOutOfBoundsException extends FatalTraceableException {

    public PlayerPosOutOfBoundsException(String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        super(QuitReason.EXEDED_LIMIT_EXCEPTION, "PLAYER_POS_OUT_OF_BOUNDS", message, detailedMessage, causeMessage, cause, threadState);
    }

    public PlayerPosOutOfBoundsException(String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        super(QuitReason.EXEDED_LIMIT_EXCEPTION, "PLAYER_POS_OUT_OF_BOUNDS", message, detailedMessage, causeMessage, threadState);
    }
}
