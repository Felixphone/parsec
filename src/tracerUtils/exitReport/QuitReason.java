package tracerUtils.exitReport;

public enum QuitReason {
    //Clean exit codes ( +ve )
    PRESSED_0(0),
    WINDOW_CLOSURE(1),

    //Unclean exit codes ( -ve )
    UNKNOWN_EXCEPTION(-1),
    UNCAUGHT_EXCEPTION(-2),
    UNSUPPORTED_PLATFORM_EXCEPTION(-3),
    SIMULATED_EXCEPTION(-4),
    OUT_OF_MEMORY(-5),
    FAILED_TO_INIT_SHADER_EXCEPTION(-6),
    EXEDED_LIMIT_EXCEPTION(-7),
    FAILED_TO_QUIT_OR_CRASH_NORMALLY(-999999);

    private final int exitCode;
    QuitReason(int exitCode) {
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}
