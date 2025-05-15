package tracerUtils;

import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;
import tracerUtils.logger.entries.LogEntry;
import tracerUtils.logger.entries.LogLevel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Day {
    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private final int value;

    Day(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum Month {

    JAN(1),
    FEB(2),
    MAR(3),
    APR(4),
    MAY(5),
    JUN(6),
    JUL(7),
    AUG(8),
    SEP(9),
    OCT(10),
    NOV(11),
    DEC(12);

    private final int value;

    Month(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

/*
enum LogLevel {
    INFO(Color.GREEN, new Color(230, 255, 231)),
    WARNING(Color.ORANGE, new Color(255, 239, 210)),
    ERROR(Color.RED, new Color(255, 200, 200, 255));

    private final Color primaryColor;
    private final Color secondaryColor;

    LogLevel(Color primaryColor, Color secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }
}
*/
/*
class LogEntry {
    private final String timestamp;
    private final LogLevel level;
    private final String message;
    private final String extraDetails;

    public LogEntry(String timestamp, LogLevel level, String message, String extraDetails) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.extraDetails = extraDetails;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getExtraDetails() {
        return extraDetails;
    }
}
*/
class LogEntryPanel extends JPanel {
    private boolean expanded = false;
    private LogEntry entry;
    private JTable infoTable;
    private DefaultTableModel tableModel;
    private JButton expandButton, actionButton;
    private JPanel extraDetailsPanel;

    public LogEntryPanel(LogEntry logEntry) {
        this.entry = logEntry;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, entry.getLogLevel().getPrimaryDisplayColour()));
        setFocusable(true);

        String[] columnNames = {"Timestamp", "Level", "Message"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        infoTable = new JTable(tableModel);
        infoTable.setTableHeader(null);
        infoTable.setBackground(entry.getLogLevel().getSecondaryDisplayColour());

        tableModel.addRow(new Object[]{
                " [" + entry.getTimestamp() + "]",
                " " + entry.getLogLevel().name(),
                " " + entry.getMessage()
        });

        int rowHeight = infoTable.getRowHeight();

        TableColumnModel columnModel = infoTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(190);
        columnModel.getColumn(0).setMinWidth(190);
        columnModel.getColumn(1).setMaxWidth(130);
        columnModel.getColumn(1).setMinWidth(130);

        infoTable.setColumnModel(columnModel);

        expandButton = new JButton(">");
        expandButton.setPreferredSize(new Dimension(30, rowHeight));
        expandButton.createToolTip();
        expandButton.setToolTipText("More information");
        expandButton.setMargin(new Insets(0, 0, 0, 0));
        expandButton.addActionListener(e -> toggleExpanded());

        actionButton = new JButton("\uD83D\uDDD7");
        actionButton.setPreferredSize(new Dimension(30, rowHeight));
        actionButton.createToolTip();
        actionButton.setToolTipText("Open in new window");
        actionButton.setMargin(new Insets(0, 0, 0, 0));
        actionButton.addActionListener(e -> openLogDetailsWindow(entry));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(expandButton);
        buttonPanel.add(actionButton);

        extraDetailsPanel = new JPanel(new BorderLayout());

        JTextArea detailsArea = new JTextArea(entry.getMessage() + " " + entry.getDetailedMessage());
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setMargin(new Insets(0, 3, 3, 0));
        detailsArea.setEditable(false);
        extraDetailsPanel.setVisible(expanded);
        extraDetailsPanel.setBorder(BorderFactory.createMatteBorder(1, 4, 0, 0, Color.GRAY));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panel.add(detailsArea, gridBagConstraints);

        extraDetailsPanel.add(panel);

        add(infoTable, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        add(extraDetailsPanel, BorderLayout.SOUTH);
    }

    public void toggleExpanded() {
        expanded = !expanded;
        extraDetailsPanel.setVisible(expanded);

        expandButton.setText(expanded ? "v" : ">");

        Container parent = SwingUtilities.getAncestorOfClass(JTable.class, this);
        if (parent instanceof JTable) {
            JTable parentTable = (JTable) parent;
            int row = parentTable.convertRowIndexToModel(parentTable.getEditingRow());
            parentTable.setRowHeight(row, expanded ? getPreferredSize().height : parentTable.getRowHeight());
            parentTable.revalidate();
            parentTable.repaint();
        }
    }

    private void openLogDetailsWindow(LogEntry entry) {

        LogEntryDetailsDialogue detailsDialogue = new LogEntryDetailsDialogue(entry, EngineWatchdog.getLogger());

        new Thread(() -> detailsDialogue.open()).start();
    }
}

class LogEntryRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof LogEntryPanel) {
            return (LogEntryPanel) value;
        }
        return new JLabel(value != null ? " " + value.toString() : "");
    }
}

class LogEntryEditor extends AbstractCellEditor implements TableCellEditor {
    private LogEntryPanel panel;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof LogEntryPanel) {
            panel = (LogEntryPanel) value;
            return panel;
        }
        return new JLabel("# Invalid");
    }

    @Override
    public Object getCellEditorValue() {
        return panel;
    }
}

public class LogViewer extends GeneralDialogue {
    private final ConcurrentLinkedQueue<LogEntry> logQueue = new ConcurrentLinkedQueue<>();
    private final LinkedList<LogEntry> logs = new LinkedList<>();
    private final DefaultTableModel tableModel;
    private final JTable outerTable;
    private JTextField searchField;
    private String searchExpression = "level >= INFO";
    private boolean doAutoScroll = true;
    private LogEntryRenderer cellRenderer;
    private LogEntryEditor cellEditor;
    private int showingCount = 0;
    private JLabel headerLabel;
    private LogViewerFooterPanel footerPanel;

    public LogViewer() {
        super("PARSEC | Live log", "Live log:", EngineWatchdog.getLogger());

        JPanel frame = new JPanel(new BorderLayout());

        JPanel mainBody = new JPanel(new BorderLayout());

        headerLabel = new JLabel("Log Entries:", SwingConstants.CENTER);
        headerLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        headerLabel.setPreferredSize(new Dimension(-1, 20));
        mainBody.add(headerLabel, BorderLayout.NORTH);

        String[] columnNames = {"Log Entries:"};
        tableModel = new DefaultTableModel(columnNames, 0) {
        };

        outerTable = new JTable(tableModel);

        cellRenderer = new LogEntryRenderer();
        cellEditor = new LogEntryEditor();
        outerTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        outerTable.getColumnModel().getColumn(0).setCellEditor(cellEditor);
        outerTable.setTableHeader(null);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EtchedBorder());

        JLabel searchLabel = new JLabel(" Search: ");

        JPanel searchFieldPanel = new JPanel(new BorderLayout());

        searchField = new JTextField(searchExpression);
        searchField.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(193, 193, 193), new Color(126, 126, 126)));
        searchField.addActionListener(e -> {
            onSearch(searchField.getText());
        });

        JPanel buttonsPanel = new JPanel(new BorderLayout());

        JButton searchButton = new JButton("\uD83D\uDD0E");
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setPreferredSize(new Dimension(30, 20));
        searchButton.createToolTip();
        searchButton.setToolTipText("Search");
        searchButton.addActionListener(e -> {
            onSearch(searchField.getText());
        });

        JButton clearButton = new JButton("X");
        clearButton.setMargin(new Insets(0, 0, 0, 0));
        clearButton.setPreferredSize(new Dimension(30, 20));
        clearButton.createToolTip();
        clearButton.setToolTipText("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            onSearch("");
        });

        buttonsPanel.add(searchButton, BorderLayout.WEST);
        buttonsPanel.add(clearButton, BorderLayout.EAST);

        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(buttonsPanel, BorderLayout.EAST);

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchFieldPanel, BorderLayout.CENTER);

        footerPanel = new LogViewerFooterPanel(this);

        JScrollPane scrollPane = new JScrollPane(outerTable);
        mainBody.add(scrollPane, BorderLayout.CENTER);

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(mainBody, BorderLayout.CENTER);

        setMainContent(frame);
        setFooterContent(footerPanel);

        onSearch(searchExpression);

        updateLog();
    }

    public static void main(String[] args) {
        LogViewer viewer = new LogViewer();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    LogLevel level = LogLevel.values()[(int) (Math.random() * LogLevel.values().length)];
                    viewer.submitLogEntry(new LogEntry(level, "test", "test", new ThreadState(Thread.currentThread())));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });

        thread.start();

        viewer.open();
    }

    private void onSearch(String searchExpression) {
        this.searchExpression = searchExpression;
        tableModel.setRowCount(0);
        headerLabel.setText(Objects.equals(searchExpression, "") ? "Log Entries:" : "Log Entries (Showing results for: '" + searchExpression + "'):");
        outerTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);
        outerTable.getColumnModel().getColumn(0).setCellEditor(cellEditor);

        try {
            for (LogEntry entry : filterLogs(logs)) {
                tableModel.addRow(new Object[]{new LogEntryPanel(entry)});
            }

        } catch (InvalidExpressionException ex) {
            tableModel.addRow(new Object[]{ex.getMessage()});
        }

        outerTable.revalidate();
        outerTable.repaint();

        showingCount = outerTable.getRowCount();
        footerPanel.getShowingCountLabel().setText(" Showing " + showingCount + " of " + logs.size() + " entries ");

        if (showingCount == 0) {
            tableModel.addRow(new Object[]{"# No results found for '" + searchExpression + "'"});
        }

        if (showingCount == 1) {
            if (tableModel.getValueAt(0, 0).toString().startsWith(" # ")) {
                showingCount = 0;
            }
        }

        if (doAutoScroll) {
            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, outerTable);
            if (scrollPane != null) {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                int maxScrollPosition = verticalBar.getMaximum();

                verticalBar.setValue(maxScrollPosition);
            }
        }
    }

    private LinkedList<LogEntry> filterLogs(LinkedList<LogEntry> logs) throws InvalidExpressionException {
        LinkedList<LogEntry> filteredLogs = new LinkedList<>();

        if (Objects.equals(searchExpression, "")) {
            return logs;
        }

        for (LogEntry entry : logs) {
            String expression = searchExpression.trim();
            if (evaluateExpression(entry, expression)) {
                filteredLogs.add(entry);
            }
        }

        return filteredLogs;
    }

    private boolean evaluateExpression(LogEntry entry, String expression) throws InvalidExpressionException {
        expression = expression.trim();

        ArrayList<String> subExpressions = new ArrayList<>();
        int startIndex = 0;
        int endIndex = -1;
        int depth = 0;

        for (int i = 0; i < expression.length(); i++) {

            char character = expression.charAt(i);

            if (character == '(') {

                if (depth == 0) { // First outermost '('
                    startIndex = i + 1;
                }

                depth++;

            } else if (character == ')') {

                depth--;

                if (depth < 0) {
                    throw new InvalidExpressionException("# Invalid expression: Unexpected bracket ')' at index " + i + " in expression");
                }

                if (depth == 0) {
                    endIndex = i - 1;
                    String subExpression = expression.substring(startIndex, endIndex);
                    if (!subExpressions.contains(expression)) {
                        subExpressions.add(subExpression);
                    }
                }
            }
        }

        if (depth > 0) {
            throw new InvalidExpressionException("# Invalid expression: Missing closing bracket ')' for bracket '(' at index " + startIndex + " in expression");
        }

        for (String subExpression : subExpressions) {
            String subExpressionResult = String.valueOf(evaluateExpression(entry, subExpression));
            expression = expression.replace("(" + subExpression + ")", subExpressionResult);
        }

        return evaluateBracketlessExpression(entry, expression); // Evaluate after resolving parentheses
    }

    private boolean evaluateBracketlessExpression(LogEntry entry, String expression) throws InvalidExpressionException {
        boolean expressionResult = false;

        String[] orConditions = expression.split("\\s+OR\\s+"); // Correctly parse OR conditions
        for (String orCondition : orConditions) {
            boolean andResult = true;

            String[] andConditions = orCondition.split("\\s+AND\\s+"); // Correctly parse AND conditions
            for (String andCondition : andConditions) {
                andCondition = andCondition.trim();
                boolean andConditionResult = evaluateExpressionCondition(entry, andCondition);

                andResult &= andConditionResult; // Apply AND conditions inside each OR group
            }

            expressionResult |= andResult; // Apply OR conditions
        }

        return expressionResult;
    }

    private boolean evaluateExpressionCondition(LogEntry entry, String expressionCondition) throws InvalidExpressionException {
        boolean conditionResult = false;

        // Handle NOT operator
        boolean isNegated = expressionCondition.startsWith("NOT ");
        if (isNegated) {
            expressionCondition = expressionCondition.substring(4).trim();
        }

        if (expressionCondition.equals("true")) {
            conditionResult = true;
        } else if (expressionCondition.equals("false")) {
            conditionResult = false;
        } else if (expressionCondition.matches("day\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("day\s+([^\s+]+)\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String day = matcher.group(2).toUpperCase();
                Day dayEnum;
                try {
                    dayEnum = Day.valueOf(day);
                } catch (IllegalArgumentException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + day + "' is not valid for expression '" + expressionCondition + "'");
                }

                String entryDay = entry.getTimestamp().substring(1, 4);
                Day entryDayEnum = Day.valueOf(entryDay.toUpperCase());

                switch (operator) {
                    case ">":
                        conditionResult = entryDayEnum.getValue() > dayEnum.getValue();
                        break;
                    case "<":
                        conditionResult = entryDayEnum.getValue() < dayEnum.getValue();
                        break;
                    case ">=":
                        conditionResult = entryDayEnum.getValue() >= dayEnum.getValue();
                        break;
                    case "<=":
                        conditionResult = entryDayEnum.getValue() <= dayEnum.getValue();
                        break;
                    case "==":
                        conditionResult = entryDayEnum.getValue() == dayEnum.getValue();
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }
            }
        } else if (expressionCondition.matches("month\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("month\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String month = matcher.group(2).toUpperCase();
                Month monthEnum;
                try {
                    monthEnum = Month.valueOf(month);
                } catch (IllegalArgumentException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + month + "' is not valid for expression '" + expressionCondition + "'");
                }

                String entryMonth = entry.getTimestamp().substring(5, 8);
                Month entryMonthEnum = Month.valueOf(entryMonth.toUpperCase());

                switch (operator) {
                    case ">":
                        conditionResult = entryMonthEnum.getValue() > monthEnum.getValue();
                        break;
                    case "<":
                        conditionResult = entryMonthEnum.getValue() < monthEnum.getValue();
                        break;
                    case ">=":
                        conditionResult = entryMonthEnum.getValue() >= monthEnum.getValue();
                        break;
                    case "<=":
                        conditionResult = entryMonthEnum.getValue() <= monthEnum.getValue();
                        break;
                    case "==":
                        conditionResult = entryMonthEnum.getValue() == monthEnum.getValue();
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }
            }
        } else if (expressionCondition.matches("date\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("date\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String date = matcher.group(2);
                int dateInt;

                try {
                    dateInt = Integer.parseInt(date);
                } catch (NumberFormatException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + date + "' is not valid for expression '" + expressionCondition + "'");
                }

                int entryDate = Integer.parseInt(entry.getTimestamp().substring(10, 11));

                switch (operator) {
                    case ">":
                        conditionResult = entryDate > dateInt;
                        break;
                    case "<":
                        conditionResult = entryDate < dateInt;
                        break;
                    case ">=":
                        conditionResult = entryDate >= dateInt;
                        break;
                    case "<=":
                        conditionResult = entryDate <= dateInt;
                        break;
                    case "==":
                        conditionResult = entryDate == dateInt;
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }
            }
        }

        // Time conditions
        else if (expressionCondition.matches("time\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("time\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String time = matcher.group(2);
                LocalTime targetTime;
                try {
                    targetTime = LocalTime.parse(time);
                } catch (DateTimeParseException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + time + "' is not valid for expression '" + expressionCondition + "'");
                }

                String timeString = entry.getTimestamp().substring(11, 19);
                LocalTime logTime = LocalTime.parse(timeString);

                switch (operator) {
                    case ">":
                        conditionResult = logTime.isAfter(targetTime);
                        break;
                    case "<":
                        conditionResult = logTime.isBefore(targetTime);
                        break;
                    case ">=":
                        conditionResult = !logTime.isBefore(targetTime);
                        break;
                    case "<=":
                        conditionResult = !logTime.isAfter(targetTime);
                        break;
                    case "==":
                        conditionResult = !logTime.equals(targetTime);
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }
            }
        } else if (expressionCondition.matches("year\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("year\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String year = matcher.group(2);
                int yearInt;

                try {
                    yearInt = Integer.parseInt(year);
                } catch (NumberFormatException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + year + "' is not valid for expression '" + expressionCondition + "'");
                }

                int entryYear = Integer.parseInt(entry.getTimestamp().substring(25, 29));

                switch (operator) {
                    case ">":
                        conditionResult = entryYear > yearInt;
                        break;
                    case "<":
                        conditionResult = entryYear < yearInt;
                        break;
                    case ">=":
                        conditionResult = entryYear >= yearInt;
                        break;
                    case "<=":
                        conditionResult = entryYear <= yearInt;
                        break;
                    case "==":
                        conditionResult = entryYear == yearInt;
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }
            }
        }

        // Log level conditions
        else if (expressionCondition.matches("level\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("level\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String level = matcher.group(2);

                LogLevel logLevelEnum;

                try {
                    logLevelEnum = LogLevel.valueOf(level);
                } catch (IllegalArgumentException e) {
                    throw new InvalidExpressionException("# Invalid expression: Operand '" + level + "' is not valid for expression '" + expressionCondition + "'. Must be a valid log level");
                }

                switch (operator) {
                    case ">":
                        conditionResult = entry.getLogLevel().getLevel() > logLevelEnum.getLevel();
                        break;
                    case "<":
                        conditionResult = entry.getLogLevel().getLevel() < logLevelEnum.getLevel();
                        break;
                    case ">=":
                        conditionResult = entry.getLogLevel().getLevel() >= logLevelEnum.getLevel();
                        break;
                    case "<=":
                        conditionResult = entry.getLogLevel().getLevel() <= logLevelEnum.getLevel();
                        break;
                    case "==":
                        conditionResult = entry.getLogLevel().getLevel() == logLevelEnum.getLevel();
                        break;
                    default:
                        throw new InvalidExpressionException("Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");

                }
            }
        } else if (expressionCondition.matches("message\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("message\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String messageSearch = matcher.group(2);

                switch (operator) {
                    case "=*":
                    case "contains":
                        conditionResult = entry.getMessage().contains(messageSearch);
                        break;
                    case "==":
                        conditionResult = entry.getMessage().equalsIgnoreCase(messageSearch);
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }

            }
        } else if (expressionCondition.matches("details\\s+([^\\s+]+)\\s*(.*)")) {
            Matcher matcher = Pattern.compile("details\\s+([^\\s+]+)\\s*(.*)").matcher(expressionCondition);
            if (matcher.find()) {
                String operator = matcher.group(1);
                String messageSearch = matcher.group(2);

                switch (operator) {
                    case "=*":
                    case "contains":
                        conditionResult = entry.getDetailedMessage().toLowerCase().contains(messageSearch.toLowerCase());
                        break;
                    case "==":
                        conditionResult = entry.getDetailedMessage().equalsIgnoreCase(messageSearch);
                        break;
                    default:
                        throw new InvalidExpressionException("# Invalid expression: Operator '" + operator + "' is not valid for condition '" + expressionCondition + "'");
                }

            }
        } else {
            conditionResult = (entry.getMessage().toLowerCase().contains(expressionCondition.toLowerCase()) || entry.getDetailedMessage().toLowerCase().contains(expressionCondition.toLowerCase()) || entry.getTimestamp().toLowerCase().contains(expressionCondition.toLowerCase()) || entry.getLogLevel().name().toLowerCase().contains(expressionCondition));
        }

        // Apply NOT logic
        if (isNegated) {
            conditionResult = !conditionResult;
        }

        return conditionResult;
    }

    public void submitLogEntry(LogEntry entry) {
        logQueue.add(entry);
    }

    private void updateLog() {

        SwingWorker<Void, LogEntry> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (!isCancelled()) {
                    while (!logQueue.isEmpty()) {
                        publish(logQueue.poll());
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {

                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<LogEntry> chunks) {

                logs.addAll(chunks);

                JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, outerTable);
                if (scrollPane != null) {
                    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                    int scrollPosition = verticalBar.getValue() + verticalBar.getVisibleAmount();
                    int maxScrollPosition = verticalBar.getMaximum();

                    doAutoScroll = scrollPosition >= maxScrollPosition - 100;
                }

                try {
                    for (LogEntry entry : filterLogs(new LinkedList<>(chunks))) {
                        tableModel.addRow(new Object[]{new LogEntryPanel(entry)});
                        Object value = tableModel.getValueAt(0, 0);
                        if (value instanceof JLabel) {
                            JLabel label = (JLabel) tableModel.getValueAt(0, 0);
                            if (Objects.equals(label.getText(), "# No results found for '" + searchExpression + "'")) {
                                tableModel.removeRow(0);
                            }
                        }
                    }

                } catch (InvalidExpressionException ignored) {
                }

                showingCount = outerTable.getRowCount();

                if (showingCount == 1) {
                    if (tableModel.getValueAt(0, 0).toString().startsWith("# ")) {
                        showingCount = 0;
                    }
                }

                footerPanel.getShowingCountLabel().setText(" Showing " + showingCount + " of " + logs.size() + " entries ");

                outerTable.revalidate();
                outerTable.repaint();

                if (doAutoScroll) {
                    if (scrollPane != null) {
                        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                        int maxScrollPosition = verticalBar.getMaximum();

                        verticalBar.setValue(maxScrollPosition);
                    }
                }
            }
        };
        worker.execute();
    }
}