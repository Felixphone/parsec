package tracerUtils.logger.entries.extendedMessage;

import java.util.ArrayList;

public class ExtendedMessageTextElement extends ExtendedMessageElement {

    private ArrayList<String> contents;

    public ExtendedMessageTextElement() {
        contents = new ArrayList<>();
    }

    public ExtendedMessageTextElement(ArrayList<String> contents) {
        this.contents = contents;
    }

    public void add(String content) {
        contents.add(content);
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    @Override
    public ArrayList<String> asList() {
        return contents;
    }
}
