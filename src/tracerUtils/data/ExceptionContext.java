package tracerUtils.data;

public class ExceptionContext {

    private String contextMessage;
    private ThreadState threadState;
    private StackTraceElement contextCreator;

    public ExceptionContext(String contextMessage, ThreadState threadState) {
        this.contextMessage = contextMessage;
        this.threadState = threadState;
        contextCreator = threadState.getStack().getElements()[0];
    }

    public String getContextMessage() {
        return contextMessage;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public StackTraceElement getContextCreator() {
        return contextCreator;
    }
}
