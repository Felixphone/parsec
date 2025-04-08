package engine.engineFlags;

public class Flag {

    private final FlagType flagType;
    private boolean isUp;

    public Flag(FlagType flagType, boolean isUp) {
        this.flagType = flagType;
        this.isUp = isUp;
    }

    public FlagType getFlagType() {
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
