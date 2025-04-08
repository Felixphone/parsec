package tracerUtils.logger.entries.extendedMessage;

import java.util.ArrayList;

public class ExtendedMessageGroupElement extends ExtendedMessageElement {

    private String header;
    private ArrayList<ExtendedMessageElement> contents;

    public ExtendedMessageGroupElement(String header) {
        this.header = header;
        contents = new ArrayList<>();
    }

    public ExtendedMessageGroupElement(String header, ArrayList<ExtendedMessageElement> contents) {
        this.header = header;
        this.contents = contents;
    }

    public void add(ExtendedMessageElement element) {
        contents.add(element);
    }

    public ArrayList<ExtendedMessageElement> getContents() {
        return contents;
    }

    @Override
    public ArrayList<String> asList() {
        ArrayList<String> list = new ArrayList<>();
        list.add(header);
        for (int i = 0; i < contents.size(); i ++) {
            for (String line : contents.get(i).asList()) {
                list.add(" | " + line);
            }
            if (i < contents.size()) {
                list.add("---------------------------------------------------- #GROUP");
            }
        }
        list.add("==================================================== #GROUP");
        return list;
    }
}
