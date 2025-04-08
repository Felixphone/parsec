package engine.graphics.UI.UIComponents.interactables;

import engine.dataItems.exceptions.InvalidInputException;
import engine.engine.EngineCore;
import engine.graphics.UI.UIComponents.UIComponent;
import engine.graphics.objects.Mesh;
import engine.maths.Vector2f;
import engine.maths.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.newdawn.slick.opengl.Texture;
import tracerUtils.data.ExceptionContext;
import tracerUtils.data.ThreadState;
import tracerUtils.traceableException.TraceableException;

public abstract class InteractiveUIComponent extends UIComponent {

    protected boolean enabled = true;
    public boolean screenInteractionEnabled = true;

    private boolean isBeingHoveredOver = false;
    private boolean isBeingClickedOn = false;

    public InteractiveUIComponent(Vector3f position, Vector3f rotation, Vector3f scale, Mesh mesh) {
        super(position, rotation, scale, mesh);
    }

    @Override
    public void update() {
        if (enabled & screenInteractionEnabled) {
            try {
                if (isMouseHoveringOverComponent()) {
                    whilstBeingHovered();
                    if (!isBeingHoveredOver) {
                        onMouseEnter();
                        isBeingHoveredOver = true;
                    }
                } else {
                    if (isBeingHoveredOver) {
                        onMouseExit();
                        isBeingHoveredOver = false;
                    }
                }

                if (isMouseClickedOnComponent()) {
                    whilstBeingClicked();
                    if (!isBeingClickedOn) {
                        onClick();
                        isBeingClickedOn = true;
                    }
                } else {
                    if (isBeingClickedOn) {
                        onRelease();
                        isBeingClickedOn = false;
                    }
                }
            } catch (TraceableException e) {
                e.contextualiseAndRethrow(new ExceptionContext("Attempted to update component", new ThreadState(Thread.currentThread())));
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public final boolean inBounds(Vector2f queryPosition) {
        if (queryPosition.getX() >= position.getX() - (scale.getX()/2)) {
            if (queryPosition.getX() <= position.getX() + (scale.getX()/2)) {
                if (queryPosition.getY() >= position.getY() - (scale.getY()/2)) {
                    if (queryPosition.getY() >= position.getY() - (scale.getY()/2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public final boolean isMouseHoveringOverComponent() throws InvalidInputException {
        if (inBounds(EngineCore.getMouseManager().getMousePos())) {
            if (!EngineCore.getMouseManager().isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isMouseClickedOnComponent() throws InvalidInputException {
        if (inBounds(EngineCore.getMouseManager().getMousePos())) {
            if (EngineCore.getMouseManager().isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                return true;
            }
        }
        return false;
    }

    public abstract void whilstBeingClicked();

    public abstract void onClick();

    public abstract void onRelease();

    public abstract void whilstBeingHovered();

    public abstract void onMouseEnter();

    public abstract void onMouseExit();


    public void destroy() {

    }
}