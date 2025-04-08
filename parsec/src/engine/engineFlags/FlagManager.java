package engine.engineFlags;

import java.util.HashSet;
import java.util.Set;

public class FlagManager {

    private final Set<Flag> flags = new HashSet<>();
    private final Set<DebugFlag> debuggerFlags = new HashSet<>();

    public FlagManager() {
        for (FlagType flagType : FlagType.values()) {
            flags.add(new Flag(flagType, false));
        }

        for (DebugFlagType flagType : DebugFlagType.values()) {
            debuggerFlags.add(new DebugFlag(flagType, false));
        }
    }

    public Flag getFlag(FlagType flagType) {
        for (Flag flag : flags) {
            if (flag.getFlagType() == flagType) {
                return flag;
            }
        }

        return null;
    }

    public DebugFlag getDebugFlag(DebugFlagType flagType) {
        for (DebugFlag flag : debuggerFlags) {
            if (flag.getFlagType() == flagType) {
                return flag;
            }
        }

        return null;
    }
}
