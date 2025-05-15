package launcher.tasks;

public abstract class Task {

    protected TaskState taskState;
    protected long startTime = 0;
    protected long firstExecutionTime = 0;
    protected long lastExecutionTime = 0;
    private Runnable runnable;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    private boolean executeImmediatelyOnResume = false;

    public Task(Runnable runnable) {
        this.runnable = runnable;
        taskState = TaskState.READY;
    }

    public void update() {
        if (taskState == TaskState.RESUMING) {
            if (executeImmediatelyOnResume) {
                execute();
            }
        }
    }

    public void start() {
        startTime = System.nanoTime();
        taskState = TaskState.DELAYED;
    }

    public void pause() {
        if (taskState == TaskState.RUNNING) {
            taskState = TaskState.PAUSED;
        }
    }

    public void resume(boolean executeImmediatelyOnResume) {
        if (taskState == TaskState.PAUSED) {
            this.executeImmediatelyOnResume = executeImmediatelyOnResume;
            taskState = TaskState.RESUMING;
        }
    }

    public void stop() {
        taskState = TaskState.STOPPED;
    }

    protected void execute() {
        try {
            lastExecutionTime = System.nanoTime();

            if (firstExecutionTime == 0) {
                firstExecutionTime = lastExecutionTime;
            }

            runnable.run();
        } catch (Throwable t) {
            uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
        }
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
}
