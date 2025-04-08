package tracerUtils.logger.entries;

import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.CollapsiblePanel;
import tracerUtils.logger.entries.extendedMessage.ExtendedMessage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LogEntry {

    private LogLevel logLevel;
    private ThreadState threadState;

    private String timestamp;
    private String sourceThreadName;
    private String sourceClassName = "";
    private String sourceLocation = "";
    private String message;
    private String detailedMessage;
    private ExtendedMessage extendedMessage;

    private String signature;

    public LogEntry(LogLevel logLevel, String message, String detailedMessage, ThreadState threadState) {
        this.logLevel = logLevel;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.threadState = threadState;
        construct();
    }

    public LogEntry(LogLevel logLevel, String message, String detailedMessage, ExtendedMessage extendedMessage, ThreadState threadState) {
        this.logLevel = logLevel;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.extendedMessage = extendedMessage;
        this.threadState = threadState;
        construct();
    }

    private void construct() {
        // timestamp
        timestamp = threadState.getStateDateTime().toString();

        // source thread name
        sourceThreadName = threadState.getThreadName().toUpperCase();

        StackTraceElement sourceElement = threadState.getStack().getElements()[0];

        // source class name
        String[] sourceClassPath = sourceElement.getClassName().split("\\.");
        sourceClassName = sourceClassPath[sourceClassPath.length-1];

        sourceLocation = sourceClassName + "." + sourceElement.getMethodName() + "(" + sourceElement.getFileName() + ":" + sourceElement.getLineNumber() +")";

        signature = "[" + timestamp + "] [" + sourceThreadName + "] [" + sourceLocation + "] [" + logLevel.getNameLower() + "]: ";
    }

    public CollapsiblePanel asJPanel() {
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel(signature + message);

        switch (logLevel) {
            case LAUNCHER:
                collapsiblePanel.setHeaderColour(Color.MAGENTA);
                collapsiblePanel.setHeaderHoverColour(Color.MAGENTA.darker());
                break;
            case FATAL:
                collapsiblePanel.setHeaderColour(Color.RED);
                collapsiblePanel.setHeaderHoverColour(Color.RED.darker());
                break;
            case ERROR:
                collapsiblePanel.setHeaderColour(new Color(242, 81, 41));
                collapsiblePanel.setHeaderHoverColour(new Color(184, 44, 9));
                break;
            case WARNING:
                collapsiblePanel.setHeaderColour(Color.YELLOW);
                collapsiblePanel.setHeaderHoverColour(Color.YELLOW.darker());
                break;
            case CRITICAL_ATTEMPT:
                collapsiblePanel.setHeaderColour(Color.CYAN);
                collapsiblePanel.setHeaderHoverColour(Color.CYAN.darker());
                break;
            case ATTEMPT:
                collapsiblePanel.setHeaderColour(new Color(46, 123, 255));
                collapsiblePanel.setHeaderHoverColour(new Color(19, 89, 209));
                break;
            case CRITICAL_SUCCESS:
            case SUCCESS:
                collapsiblePanel.setHeaderColour(Color.GREEN);
                collapsiblePanel.setHeaderHoverColour(Color.GREEN.darker());
                break;
            case INFO:
                collapsiblePanel.setHeaderColour(Color.GRAY);
                collapsiblePanel.setHeaderHoverColour(Color.DARK_GRAY.brighter());
        }

        StringBuilder text = new StringBuilder();
        for (String string : getInfo()) {
            text.append(string).append("\n");
        }
        JTextArea area = new JTextArea();
        area.append(text.toString());
        area.append("");
        collapsiblePanel.addToContentPanel(area);
        return collapsiblePanel;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add(signature + message + " " + detailedMessage);
        if (extendedMessage != null) {
            for (int i = 0; i < extendedMessage.getElements().size(); i ++) {
                info.addAll(extendedMessage.getElements().get(i).asList());
                if (i < extendedMessage.getElements().size()-1) {
                    info.add("-------------------------------------------");
                }
            }
            info.add("===========================================");
        }
        return info;
    }

    public ArrayList<String> getColouredInfo() {
        ArrayList<String> info = getInfo();
        info.set(0, logLevel.getColour() + info.get(0));
        info.set(info.size()-1, info.get(info.size()-1) + ConsoleColours.RESET);
        return info;
    }

    @Override
    public String toString() {
        ArrayList<String> info = getInfo();
        StringBuilder infoString = new StringBuilder();

        for (int i = 0; i < info.size(); i++) {
            infoString.append(info.get(i));
            if (i < info.size()-1) {
                infoString.append("\n");
            }
        }

        return infoString.toString();
    }

    public String toColouredString() {
        ArrayList<String> info = getColouredInfo();
        StringBuilder infoString = new StringBuilder();

        for (int i = 0; i < info.size(); i++) {
            infoString.append(info.get(i));
            if (i < info.size()-1) {
                infoString.append("\n");
            }
        }

        return infoString.toString();
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSourceThreadName() {
        return sourceThreadName;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceClassName() {
        return sourceClassName;
    }

    public String getSignature() {
        return signature;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public ExtendedMessage getExtendedMessage() {
        return extendedMessage;
    }
}
