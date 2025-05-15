package tracerUtils;

import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class LogViewerFooterPanel extends JPanel {

    private final LogViewer logViewer;
    private final JLabel showingCountLabel;
    private final JButton logButton;
    private final JButton quitButton;

    public LogViewerFooterPanel(LogViewer logViewer) {
        this.logViewer = logViewer;

        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets.set(0, 10, 2, 8);

        // set the layout to border so that we can push the labels to one side, and the buttons to the other
        JPanel insetPanel = new JPanel(new BorderLayout());

        // create the count label
        showingCountLabel = new JLabel("Showing 0 of 0 results");

        // create a panel to wrap both buttons in to that it is also one element in the border layout
        JPanel buttonPanel = new JPanel();

        // create the log button and add action and mouse listeners
        logButton = new JButton("Open Log");
        logButton.addActionListener(e -> openLog());
        logButton.addMouseListener(new ButtonMouseListener());


        // create the quit button and add action and mouse listeners
        quitButton = new JButton("Close");
        quitButton.addActionListener(e -> logViewer.exit());
        quitButton.addMouseListener(new ButtonMouseListener());

        // add the buttons to the button panel
        buttonPanel.add(logButton);
        buttonPanel.add(quitButton);

        // add both panels to the main footer panel
        insetPanel.add(showingCountLabel, BorderLayout.LINE_START);
        insetPanel.add(buttonPanel, BorderLayout.LINE_END);

        add(insetPanel, gridBagConstraints);
    }

    private void openLog() {
        // create a process to launch notepad to open the log
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", EngineWatchdog.getLogger().getLog().getLogFilePath());
        try {
            pb.start();
        } catch (IOException e) {
            EngineWatchdog.getLogger().error("Error opening log file:", "" + e, new ThreadState(Thread.currentThread()));
        }
    }

    public JLabel getShowingCountLabel() {
        return showingCountLabel;
    }

    // subclass, implements MouseListener for the log and quit buttons to change the cursor on hover
    class ButtonMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
