package launcher;

import launcher.launchConfig.LaunchConfig;

public abstract class GenericApplication {

    private final String applicationName;

    private LaunchConfig launchConfig;
    private ApplicationWatchdog watchdog;
    private TaskScheduler taskScheduler;
    private String[] applicationArguments;

    private Thread mainApplicationThread;
    private Thread taskSchedulerThread;

    private Thread watchdogThread; //required (separate application?)

    private Thread shutdownThread; //required

    public GenericApplication(String applicationName) {
        this.applicationName = applicationName.toUpperCase().replace(' ', '_');
    }

    public final void launch(LaunchConfig launchConfig, String[] applicationArguments) {
        this.launchConfig = launchConfig;
        this.applicationArguments = applicationArguments;

        // call user-defined initialisation methods
        onLaunch();

        // set up application threads

        // watchdog
        if (launchConfig.useWatchdog) {
            watchdogThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    watchdog.run();
                }
            }, applicationName + "_WATCHDOG_THREAD");

            watchdogThread.start();
        }

        // main
        mainApplicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                open(applicationArguments);
            }
        }, applicationName + "_MAIN_THREAD");

        // task scheduler
        taskScheduler = new TaskScheduler();
        taskSchedulerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                taskScheduler.run();
            }
        }, applicationName + "_TASK_SCHEDULER_THREAD");

        // shutdown
        shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }, applicationName + "_SHUTDOWN_THREAD");
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        // configure daemon
        mainApplicationThread.setDaemon(launchConfig.daemon);
        taskSchedulerThread.setDaemon(launchConfig.daemon);

        // start required threads
        mainApplicationThread.start();
        taskSchedulerThread.start();
    }

    protected abstract void onLaunch();

    private void open(String[] applicationArguments) {
        onOpen(applicationArguments);
    }

    protected abstract void onOpen(String[] applicationArguments);

    public void restart() {
        close();
        shutdown();
        launch(launchConfig, applicationArguments);
    }

    public void restart(String[] applicationArguments) {
        close();
        shutdown();
        launch(launchConfig, applicationArguments);
    }

    public void restart(LaunchConfig launchConfig, String[] applicationArguments) {
        close();
        shutdown();
        launch(launchConfig, applicationArguments);
    }

    public void close() {
        onClose();
        taskScheduler.close();
        //TODO: Join threads
        watchdog.close();
    }

    protected abstract void onClose();

    private void shutdown() {
        onShutdown();
        taskScheduler.shutdown();

        if (watchdog != null) {
            watchdog.shutdown();
        }
    }

    protected abstract void onShutdown();


    // ==== Getters ====

    public String getApplicationName() {
        return applicationName;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public ApplicationWatchdog getWatchdog() {
        return watchdog;
    }
}