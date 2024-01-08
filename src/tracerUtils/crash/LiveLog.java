package tracerUtils.crash;

import engine.engine.EngineCore;
import launcher.watchdog.EngineWatchdog;
import tracerUtils.GeneralDialogue;
import tracerUtils.exitReport.ExitReport;
import tracerUtils.logger.Logger;
import tracerUtils.logger.entries.LogEntry;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class LiveLog extends GeneralDialogue {

    private boolean quit = false;
    private boolean shouldRestart = false;

    private LiveLogPanel liveLogPanel;
    private Thread liveLogThread;

    public LiveLog() {
        super("PARSEC | Live log", "Live Log:", EngineWatchdog.getLogger());
        this.logger = logger;
        //liveLogPanel = new LiveLogPanel(this);
        //setMainContent(liveLogPanel);
        //getDialogueBanner().setBannerBackground(Color.blue);
    }

    @Override
    public void open() {
        liveLogThread = new Thread(super :: open);
        liveLogThread.setName("LIVE_LOG_THREAD");
        //liveLogThread.start();
    }

    @Override
    public void close() {
        quit = true;
        super.close();
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
    }

    public void addEntry(LogEntry logEntry) {
        liveLogPanel.addEntry(logEntry); // Optimized UI rendering

    }

    protected Logger getLogger() {
        return logger;
    }

}

class OverviewPanellog extends JPanel {

    public OverviewPanellog(LiveLog liveLog) {
        // set the layout to grid bag so that content can be aligned appropriately
        setLayout(new GridBagLayout());

        // create the grid bag constraints
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        // ensure content grows to fill the entire width
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1;
        // anchor content to the left side so that text is not in the center
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;

        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
    }
}

class LiveLogPanel extends JPanel {
    JScrollPane scrollPane;
    JPanel log;

    private final ArrayList<LogEntry> allLogEntries = new ArrayList<>();
    private final Timer scrollTimer;

    private static final int LOG_ENTRY_HEIGHT = 10; // Approximate height of a log entry


    public LiveLogPanel(LiveLog liveLog) {

        //set the layout to grid bag so that the content can scale dynamically
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1; // set the x weight to 1 so that content fills the entire width of the panel
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL; // ensure content fills horizontal width
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST; // anchor the content to the top left so that it scales properly
        gridBagConstraints.weighty = 0; // to start with, set the y weight to 0 so that banner and overview panels do not grow vertically
        gridBagConstraints.insets = new Insets(0, 10, 0, 10); // set the insets so that the main content is not squashed against the sides

        // add the overview panel, passing in the exit report
        add(new OverviewPanellog(liveLog), gridBagConstraints);

        // now set the fill to BOTH so that the log panel will grow in both directions, as opposed to only horizontally
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        // now also set the y weight to 1 so that the log panel will be the only element that grows to fit the height
        gridBagConstraints.weighty = 1;

        // create the log panel
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new GridBagLayout());

        // create the text area to go inside the log panel
        log = new JPanel();
        log.setLayout(new BoxLayout(log, BoxLayout.PAGE_AXIS));
        log.setBackground(Color.GRAY);

        // create and wrap the log in a scroll pane so that the content is not cut off, and can be scrolled
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(log);

        // remove the insets so that the scroll pane fits exactly into the log panel
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        logPanel.add(scrollPane, gridBagConstraints);

        // create the border around the log
        logPanel.setBorder(new TitledBorder("Log: "));

        // reset the insets and add the log panel
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        add(logPanel, gridBagConstraints);

        scrollTimer = new Timer(100, e -> updateVisibleEntries());
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> scrollTimer.restart());
    }

    public void addEntry(LogEntry logEntry) {
        allLogEntries.add(logEntry);
        SwingUtilities.invokeLater(this::updateVisibleEntries);
    }

    private void updateVisibleEntries() {
        int visibleHeight = scrollPane.getViewport().getHeight();
        int scrollPosition = scrollPane.getVerticalScrollBar().getValue();
        int startIndex = Math.max(0, scrollPosition / LOG_ENTRY_HEIGHT);
        int endIndex = Math.min(allLogEntries.size(), (scrollPosition + visibleHeight) / LOG_ENTRY_HEIGHT);

        log.removeAll(); // Remove all currently displayed log entries
        for (int i = startIndex; i < endIndex; i++) {
            log.add(allLogEntries.get(i).asJPanel()); // Add only visible entries
        }
        log.revalidate();
        log.repaint();
    }
}