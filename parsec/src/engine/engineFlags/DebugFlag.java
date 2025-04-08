package engine.engineFlags;

public class DebugFlag {

    private final DebugFlagType flagType;
    private boolean isUp;

    public DebugFlag(DebugFlagType flagType, boolean isUp) {
        this.flagType = flagType;
        this.isUp = isUp;
    }

    public DebugFlagType getFlagType() {
        return flagType;
    }

    public boolean isUp() {
        return isUp;
    }

    public void raise() {
        isUp = true;
    }

    public void lower() {
        isUp = false;
    }

    public void toggle() {
        isUp = !isUp;
    }
}
