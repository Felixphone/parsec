package launcher.tasks;

public class RepeatingTask extends Task {

    private static final long NANOS_IN_SECOND = 1000000000;
    private final double interval;
    private final double delay;
    long lastUpdateTime;
    long lastEPSCheck;
    double deltaTime = 0;
    int executions = 0;
    int EPS = 0;
    private double EPSUpdateFrequencyNano = NANOS_IN_SECOND;

    public RepeatingTask(Runnable runnable, long nanoInterval, long nanoDelay) {
        super(runnable);
        this.interval = nanoInterval;
        this.delay = nanoDelay;
    }

    public static long nanoIntervalFromTargetEPS(int targetEPS) {
        if (targetEPS == 0) return 0;
        return (NANOS_IN_SECOND / targetEPS);
    }

    public static long nanoDelayFromSeconds(int seconds) {
        return seconds * NANOS_IN_SECOND;
    }

    @Override
    public void start() {
        super.start();
        lastUpdateTime = System.nanoTime();
        lastEPSCheck = System.nanoTime();
    }

    @Override
    public void update() {
        long time = System.nanoTime();

        if (taskState == TaskState.RUNNING) {

            deltaTime += (time - lastUpdateTime) / interval;
            lastUpdateTime = time;

            if (deltaTime >= 1) {
                execute();

                executions++;
                deltaTime--;
            }

            if (time >= lastEPSCheck + EPSUpdateFrequencyNano) {
                lastEPSCheck = time;
                EPS = (int) (executions / (EPSUpdateFrequencyNano / NANOS_IN_SECOND));
                executions = 0;
            }
        } else if (taskState == TaskState.DELAYED) {
            if (time >= startTime + delay) {
                taskState = TaskState.RUNNING;
                execute();
            }
        } else if (taskState == TaskState.RESUMING) {
            deltaTime = 0;
            executions = 0;
            lastUpdateTime = time;
            lastEPSCheck = time;
            taskState = TaskState.RUNNING;
        }
        super.update();
    }

    public void setEPSUpdateFrequencyNano(double EPSUpdateFrequencyNano) {
        this.EPSUpdateFrequencyNano = EPSUpdateFrequencyNano;
    }

    public int getEPS() {
        return EPS;
    }
}
