package launcher;

public class DefaultApplication extends GenericApplication {

    public DefaultApplication() {
        super("Default Phoenix app");
    }

    // ==== Init and launch methods ====

    @Override
    protected void onLaunch() {
        System.out.println("""
                Congratulations! You just ran the Phoenix-AEL default application!
                
                ============================= Welcome to Phoenix-AEL =============================
                 What is Phoenix-AEL?
                  - Phoenix-ALE (Application Engine Library) is part of the PhoenixEngine family
                    of libraries. It can be used to create simple, light-weight applications,
                    complete with task-scheduling, logging, and crash handling, all in one easy
                    to use bundle.
                
                 What is an App?
                  - Apps typically consist of 3 main parts:
                    1. The Application - A user-defined subclass of the GenericApplication
                       class, holding the main application logic.
                    2. The Watchdog - Supervises the Applications execution and handles logging
                       and crashes.
                    3. The Launcher - Configures and instantiates the watchdog and launches the
                       provided application.
                
                 For more information, please visit: https://github.com/Felixphone
                ==================================================================================
                
                ========================== Hello world from onLaunch()! ==========================
                 This is the onLaunch() method. It is called when the application is launched,
                 but before any application initialisation takes place. It is called from the
                 same thread the launcher's launchApplication() method is called from.
                ==================================================================================
                """);
    }

    @Override
    protected void onOpen(String[] applicationArguments) {

        System.out.println("""
                =========================== Hello world from onOpen()! ===========================
                 This is the onOpen() method. It is called when the application is launched,
                 after the application has been initialised. It is called from the
                 MAIN_APPLICATION_THREAD. This should be where you put your main application
                 logic, and if you want, start any task loops using the TaskScheduler.
                ==================================================================================
                """);

        close();

    }

    // ==== Closure methods ====

    @Override
    protected void onClose() {
        System.out.println("""
                ========================== Goodbye world from onClose()! =========================
                 This is the onClose() method. It is called when the application is requested to
                 close. It is called before the internal application cleanup takes place, and
                 from the same thread close() is called from. This should be where you put any
                 cleanup logic for your application, ready for the program to end.
                ==================================================================================
                """);
    }

    @Override
    protected void onShutdown() {
        System.out.println("""
                ======================== Goodbye world from onShutdown()! ========================
                 This is the onShutdown() method. It is called when the JVM is about to be
                 terminated. It is called from the APPLICATION_SHUTDOWN_THREAD, and should be
                 where you put any crucial cleanup logic for your application, i.e. any cleanup
                 logic which must run even if the application closes unexpectedly or crashes.
                ==================================================================================
                """);
    }
}
