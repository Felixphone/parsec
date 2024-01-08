package tracerUtils.data;

import java.util.ArrayList;

public class Stack {

    private StackTraceElement[] elements;

    public Stack(StackTraceElement[] elements) {
        this.elements = elements;
    }

    public StackTraceElement[] getElements() {
        return elements;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Stack: [most recent call first]");
        for (StackTraceElement element : elements) {
            info.add(" => " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() +")");
        }

        return info;
    }

    @Override
    public String toString() {
        String str = "";
        for (String line : getInfo()) {
            str += line + "\n";
        }

        return str;
    }
}
