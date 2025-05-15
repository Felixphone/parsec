package launcher.tasks;

import java.time.Duration;

public class DelayedTask extends Task {

    private final double delay;

    public DelayedTask(Runnable runnable, Duration delay) {
        super(runnable);
        this.delay = delay.toNanos();
    }

    @Override
    public void update() {
        long time = System.nanoTime();

        if (taskState == TaskState.DELAYED) {
            if (time > startTime + delay) {
                execute();
                taskState = TaskState.STOPPED;
            }
        }
        super.update();
    }
}
