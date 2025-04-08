package tracerUtils.traceableException;

import tracerUtils.data.ExceptionContext;
import tracerUtils.data.Stack;
import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.QuitReason;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessage;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessageElement;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessageGroupElement;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessageTextElement;
import tracerUtils.traceableException.defaultExceptions.fatal.UntraceableException;

import java.util.ArrayList;
import java.util.List;

public class FatalTraceableException extends RuntimeException {

    private QuitReason quitReason;
    protected String exceptionName;
    protected String source;
    protected String message;
    protected String detailedMessage;
    protected String causeMessage;
    protected TraceableException cause;
    protected ThreadState threadState;
    protected List<ExceptionContext> contexts;
    protected Stack stackTrace;

    public FatalTraceableException(QuitReason quitReason, String exceptionName, String message, String detailedMessage, String causeMessage, Throwable cause, ThreadState threadState) {
        this.quitReason = quitReason;

        this.exceptionName = exceptionName;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.causeMessage = causeMessage;

        if (cause instanceof TraceableException) {
            this.cause = (TraceableException) cause;
        } else {
            this.cause = new TraceableException(new UntraceableException(cause));
        }

        this.threadState = threadState;
        stackTrace = threadState.getStack();

        contexts = new ArrayList<>();
        source = threadState.getStack().getElements()[1].getClassName();
    }

    public FatalTraceableException(QuitReason quitReason, String exceptionName, String message, String detailedMessage, String causeMessage, ThreadState threadState) {
        this.exceptionName = exceptionName;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.causeMessage = causeMessage;
        this.cause = null;
        this.threadState = threadState;

        contexts = new ArrayList<>();
        source = threadState.getStack().getElements()[1].getClassName();
    }

    public void contextualise(ExceptionContext context) {
        contexts.add(context);
    }

    public void contextualiseAndRethrow(ExceptionContext context) {
        contexts.add(context);
        throw this;
    }

    public ArrayList<String> getInfo() {

        ArrayList<String> info = new ArrayList<>();
        info.addAll(getDetailsInfo());

        info.add("-------------------------------------------");

        info.addAll(getContextInfo(contexts));

        info.add("-------------------------------------------");

        info.addAll(getThreadInfo(threadState));

        info.add("-------------------------------------------");

        info.addAll(stackTrace.getInfo());

        info.add("===========================================");

        info.addAll(getCauseInfo(cause));

        return info;
    }

    public LogEntry toLogEntry(ThreadState threadState) {
        ExtendedMessage extendedMessage = new ExtendedMessage();

        extendedMessage.add(new ExtendedMessageTextElement(getDetailsInfo()));
        extendedMessage.add(new ExtendedMessageTextElement(getContextInfo(contexts)));
        extendedMessage.add(new ExtendedMessageTextElement(getThreadInfo(threadState)));
        extendedMessage.add(new ExtendedMessageTextElement(threadState.getStack().getInfo()));
        if (cause != null) {
            extendedMessage.add(new ExtendedMessageGroupElement("Caused by:", cause.getExtendedMessage()));
        } else {
            ArrayList<String> contents = new ArrayList<>();
            contents.add(" No cause available");
            extendedMessage.add(new ExtendedMessageTextElement(contents));
        }
        return new LogEntry(LogLevel.FATAL, exceptionName + ": " + message, detailedMessage, extendedMessage, threadState);
    }

    public ArrayList<ExtendedMessageElement> getExtendedMessage() {
        ArrayList<ExtendedMessageElement> extendedMessageElements = new ArrayList<>();

        extendedMessageElements.add(new ExtendedMessageTextElement(getDetailsInfo()));
        extendedMessageElements.add(new ExtendedMessageTextElement(getContextInfo(contexts)));
        extendedMessageElements.add(new ExtendedMessageTextElement(getThreadInfo(threadState)));
        extendedMessageElements.add(new ExtendedMessageTextElement(getStackInfo(threadState)));
        if (cause != null) {
            extendedMessageElements.add(new ExtendedMessageGroupElement("Caused by:", cause.getExtendedMessage()));
        } else {
            ArrayList<String> contents = new ArrayList<>();
            contents.add(" No cause available");
            extendedMessageElements.add(new ExtendedMessageTextElement(contents));
        }
        return extendedMessageElements;
    }

    public String asString() {
        String str = "";

        for (String line : getInfo()) {
            str += line + "\n";
        }
        return str;
    }

    protected ArrayList<String> getDetailsInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("=== " + exceptionName.toUpperCase() + " ===");
        info.add(" Details:");
        info.add(" - Exception: " + this);
        info.add(" - Exception Class Name: " + this.getClass().getSimpleName());
        info.add(" - Exception Package Name: " + this.getClass().getPackageName());
        info.add(" - Fatal: TRUE");
        info.add(" - Suppressed: " + getSuppressed());
        info.add(" - Type: " + exceptionName);
        info.add(" - Called by: " + source);
        info.add(" - Message: " + message);
        info.add(" - Detailed Message: " + detailedMessage);
        info.add(" - Cause Message: " + causeMessage);
        info.add(" - Quit Reason: " + quitReason.name());
        info.add(" - Exit Code: " + quitReason.getExitCode());

        return info;
    }

    protected ArrayList<String> getContextInfo(List<ExceptionContext> exceptionContexts) {
        ArrayList<String> info = new ArrayList<>();

        if (exceptionContexts.isEmpty()) {
            info.add(" No context available");
            return info;
        }

        info.add("Context: [most recent action first]:");
        for (ExceptionContext exceptionContext : exceptionContexts) {
            info.add(" => "  + exceptionContext.getContextCreator().getFileName() + ": " + exceptionContext.getContextCreator().getClassName() + "." + exceptionContext.getContextCreator().getMethodName() + "()[" + exceptionContext.getContextCreator().getLineNumber() + "]: " + exceptionContext.getContextMessage());
        }
        return info;
    }

    protected ArrayList<String> getThreadInfo(ThreadState threadState) {

        if (threadState == null) {
            ArrayList<String> info = new ArrayList<>();
            info.add(" No thread info available");
            return info;
        }

        return threadState.getInfo();
    }

    protected ArrayList<String> getStackInfo(ThreadState threadState) {
        if (threadState == null) {
            ArrayList<String> info = new ArrayList<>();
            info.add(" No stack info available");
            return info;
        }

        return threadState.getStack().getInfo();
    }

    protected ArrayList<String> getCauseInfo(TraceableException cause) {
        ArrayList<String> info = new ArrayList<>();

        if (cause == null) {
            info.add(" No cause available");
            return info;
        }

        info.add("");
        info.add("Caused by: ");
        ArrayList<String> causeInfo = cause.getInfo();
        for (int i = 0; i < causeInfo.size(); i++) {
            if (i == 0) {
                info.add(" *  " + causeInfo.get(i));
            }
            else {
                info.add(" |  " + causeInfo.get(i));
            }
        }
        return info;
    }

    public QuitReason getQuitReason() {
        return quitReason;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public List<ExceptionContext> getContexts() {
        return contexts;
    }

}
