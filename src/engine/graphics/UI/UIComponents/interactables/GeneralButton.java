package engine.graphics.UI.UIComponents.interactables;

import engine.dataItems.exceptions.RequiredFileNotFoundException;
import engine.maths.Vector3f;

public class GeneralButton extends AbstractButton {

    public GeneralButton(Vector3f position, Vector3f rotation, Vector3f scale) throws RequiredFileNotFoundException {
        super(position, rotation, scale);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void whilstBeingClicked() {
        if (!enabled) {
        }
    }

    @Override
    public void onClick() {
        if (!enabled) {
        }
    }

    @Override
    public void onRelease() {
        if (!enabled) {
        }
    }

    @Override
    public void whilstBeingHovered() {
        if (!enabled) {
        }
    }

    @Override
    public void onMouseEnter() {
        if (!enabled) {
        }
    }

    @Override
    public void onMouseExit() {
        if (!enabled) {
        }
    }
}
