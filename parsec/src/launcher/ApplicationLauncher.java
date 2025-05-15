package launcher;

import launcher.launchConfig.LaunchConfig;

public class ApplicationLauncher {

    private final GenericApplication application;

    public ApplicationLauncher(GenericApplication application) {
        this.application = application;
    }

    public void launchApplication(LaunchConfig launchConfig, String[] applicationArguments) {
        application.launch(launchConfig, applicationArguments);
    }
}
