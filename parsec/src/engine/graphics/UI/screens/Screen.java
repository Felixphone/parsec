package engine.graphics.UI.screens;

import engine.graphics.UI.UIComponents.UIComponent;
import tracerUtils.data.ExceptionContext;
import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;


import java.util.ArrayList;

public abstract class Screen {
    
    private ArrayList<UIComponent> components = new ArrayList<>();

    protected boolean interactive = true;

    public void update() {
        try {
            for (UIComponent component : components) {
                    if (component.isScreenInteractionEnabled() != interactive) {
                        component.setScreenInteractionEnabled(interactive);
                    }

                    component.update();
            }
        } catch (TraceableException e) {
            e.contextualiseAndRethrow(new ExceptionContext("Attempted to update UIComponents in screen", new ThreadState(Thread.currentThread())));
        }
    };

    public void render() {
        for (UIComponent component : components) {
           component.render();
        }
    }

    public final void add(UIComponent component) {
        component.setScreenInteractionEnabled(interactive);
        components.add(component);
    }
}
