package launcher;

import launcher.tasks.Task;
import launcher.tasks.TaskState;

import java.util.concurrent.LinkedBlockingQueue;

public class TaskScheduler {

    private final LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    private volatile boolean processTasks = true;

    public void run() {
        while (processTasks) {
            tasks.forEach(this::processTask);
        }
    }

    private void processTask(Task task) {
        task.update();
        if (task.getTaskState() == TaskState.STOPPED) {
            tasks.remove(task);
        }
    }

    public void scheduleTask(Task task) {
        tasks.offer(task);
    }

    public void close() {
        processTasks = false;
    }

    public void shutdown() {
        processTasks = false;
    }
}
