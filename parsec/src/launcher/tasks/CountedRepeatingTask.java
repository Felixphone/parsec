package launcher.tasks;

public class CountedRepeatingTask extends RepeatingTask {

    private int runCount;

    public CountedRepeatingTask(Runnable runnable, long nanoInterval, long nanoDelay, int runCount) {
        super(runnable, nanoInterval, nanoDelay);
        this.runCount = runCount;
    }

    @Override
    public void update() {
        if (taskState == TaskState.RUNNING) {
            if (runCount <= 0) {
                taskState = TaskState.STOPPED;
                return;
            }
            runCount--;
        }
        super.update();
    }
}
