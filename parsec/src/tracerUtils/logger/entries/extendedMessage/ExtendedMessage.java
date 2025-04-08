package tracerUtils.logger.entries.extendedMessage;

import java.util.ArrayList;

public class ExtendedMessage {

    private ArrayList<ExtendedMessageElement> elements;

    public ExtendedMessage() {
        elements = new ArrayList<>();
    }

    public ExtendedMessage(ArrayList<ExtendedMessageElement> elements) {
        this.elements = elements;
    }

    public void add(ExtendedMessageElement element) {
        elements.add(element);
    }

    public ArrayList<ExtendedMessageElement> getElements() {
        return elements;
    }
}
