package tracerUtils.data;

import java.util.ArrayList;
import java.util.Date;

public class ThreadState {

    private String threadName;
    private int threadPriority;
    private boolean wasAlive;
    private boolean wasDaemon;
    private boolean wasInterrupted;
    private Thread.State state;
    private Stack stack;
    private ClassLoader classLoader;
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private Date stateDateTime;

    public ThreadState(Thread thread) {
        threadName = thread.getName();
        threadPriority = thread.getPriority();
        wasAlive = thread.isAlive();
        wasDaemon = thread.isDaemon();
        wasInterrupted = thread.isInterrupted();
        state = thread.getState();
        classLoader = thread.getContextClassLoader();
        exceptionHandler = thread.getUncaughtExceptionHandler();
        stateDateTime = new Date();

        StackTraceElement[] currentStackTrace = thread.getStackTrace();
        StackTraceElement[] stackTrace = new StackTraceElement[currentStackTrace.length - 2];
        for (int i = 2; i < currentStackTrace.length; i++) {
            stackTrace[i - 2] = currentStackTrace[i];
        }

        stack = new Stack(stackTrace);
    }

    public String getThreadName() {
        return threadName;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public boolean wasAlive() {
        return wasAlive;
    }

    public boolean wasDaemon() {
        return wasDaemon;
    }

    public boolean wasInterrupted() {
        return wasInterrupted;
    }

    public Thread.State getState() {
        return state;
    }

    public Stack getStack() {
        return stack;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Thread.UncaughtExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public Date getStateDateTime() {
        return stateDateTime;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();

        info.add("Thread info:");
        info.add(" - Throw time: " + String.valueOf(stateDateTime));
        info.add(" - Thread name: " + threadName);
        info.add(" - Thread priority: " + threadPriority);
        info.add(" - Alive: " + wasAlive);
        info.add(" - Daemon: " + wasDaemon);
        info.add(" - Interrupted: " + wasInterrupted);
        info.add(" - State: " + state);
        info.add(" - Class loader: " + classLoader);
        info.add(" - Exception handler: " + exceptionHandler);

        return info;
    }

    @Override
    public String toString() {
        String str = "";
        for (String line : getInfo()) {
            str += line + "\n";
        }

        return str;
    }
}
