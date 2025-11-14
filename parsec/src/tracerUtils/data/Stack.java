package tracerUtils.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
            info.addAll(getFrameInfo(element));
            //info.add(" => " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() +")");
        }

        return info;
    }

    private ArrayList<String> getFrameInfo(StackTraceElement element) {
        ArrayList<String> info = new ArrayList<>();

        try {
            Class<?> cls = Class.forName(element.getClassName());
            Method method = findMethodByName(cls, element.getMethodName());

            String methodSignature = formatMethodSignature(cls, method);
            String lineNumber = String.valueOf(element.getLineNumber());
            String className = cls.getSimpleName();
            String fileName = element.getFileName();
            String packageName = "/" + cls.getPackageName();

            info.add("=> " + className + "." + method.getName() + "(" + fileName + ":" + lineNumber + "):");
            info.add("| - Method: " + methodSignature);
            info.add("| - Class: " + className);
            info.add("| - Package: " + packageName);
            info.add("| - File: " + fileName);
            info.add("| - Line: " + lineNumber);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return info;
    }

    private Method findMethodByName(Class<?> cls, String methodName) {
        return Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst()
                .orElse(null);
    }

    private String formatMethodSignature(Class<?> cls, Method method) {
        if (method == null)
            return cls.getName() + ".<unknown>()";

        String params = Arrays.stream(method.getParameters())
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", "));

        String returnType = method.getReturnType().getSimpleName();

        return "%s -> %s(%s)".formatted(
                returnType,
                method.getName(),
                params
        );
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
