package launcher;

import launcher.tasks.RepeatingTask;

public class Application extends GenericApplication {

    private RepeatingTask updateTask;
    private RepeatingTask renderTask;

    public Application() {
        super("My first Phoenix app");
    }

    // ==== Init and launch methods ====

    @Override
    protected void onLaunch() {
        System.out.println("Hello world from Init!");
    }

    @Override
    protected void onOpen(String[] applicationArguments) {

        System.out.println("Hello world from Launch!");

        // set up repeating tasks for game loop (update and render)
        updateTask = new RepeatingTask(this::update, RepeatingTask.nanoIntervalFromTargetEPS(10), 0);
        getTaskScheduler().scheduleTask(updateTask);
        updateTask.start();

        renderTask = new RepeatingTask(this::render, RepeatingTask.nanoIntervalFromTargetEPS(10), 0);
        getTaskScheduler().scheduleTask(renderTask);
        renderTask.start();
    }

    // ==== Closure methods ====

    @Override
    protected void onClose() {
        System.out.println("Goodbye world from Close!");
    }

    @Override
    protected void onShutdown() {
        System.out.println("Goodbye world from Shutdown!");
    }

    // ==== User-defined methods ====

    private void update() {
        System.out.println("Updating....| UPS: " + updateTask.getEPS());
        try {
            Thread.sleep(10000);
            restart();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void render() {
        System.out.println("Rendering...| FPS: " + renderTask.getEPS());
    }
}
