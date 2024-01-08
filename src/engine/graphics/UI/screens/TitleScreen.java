package engine.graphics.UI.screens;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.engine.EngineCore;
import engine.graphics.UI.UIComponents.interactables.AbstractButton;
import engine.maths.Vector3f;
import game.dataItems.constants.TestConstants;
import game.dataItems.exeptions.ConcurrentWorldLoadException;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;

public class TitleScreen extends Screen {

    private static AbstractButton button;

    static {
        try {
            button = new AbstractButton(new Vector3f(10, 10, 0), TestConstants.ZERO_VEC3F, new Vector3f(100,100,100)) {
                @Override
                public void onClick() {
                    super.onClick();
                    try {
                        EngineCore.getGameCore().loadWorld("test");
                    } catch (ConcurrentWorldLoadException e) {
                        EngineWatchdog.getLogger().log(e.toLogEntry(new ThreadState(Thread.currentThread())));
                    }
                }

                @Override
                public void whilstBeingHovered() {
                }
            };
        } catch (RequiredFileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TitleScreen() {
        interactive = true;
        add(button);
    }
}