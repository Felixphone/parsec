package launcher.watchdog;

import launcher.ApplicationLauncher;
import launcher.DefaultApplication;
import launcher.launchConfig.LaunchConfig;

public class Main {

    public static void main(String[] args) {

        DefaultApplication application = new DefaultApplication();
        ApplicationLauncher launcher = new ApplicationLauncher(application);
        LaunchConfig launchConfig = LaunchConfig.defaultConfig();
        launcher.launchApplication(launchConfig, args);
    }
}
