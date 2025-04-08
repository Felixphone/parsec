package tracerUtils.exitReport;

import launcher.watchdog.EngineWatchdog;
import tracerUtils.data.ThreadState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class FooterPanel extends JPanel {

    private final ExitDialogue exitDialogue;
    private final JLabel warningsLabel;
    private final JLabel infoLabel;
    private final JButton logButton;
    private final JButton relaunchButton;
    private final JButton quitButton;

    public FooterPanel(ExitDialogue exitDialogue) {
        this.exitDialogue = exitDialogue;

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

        // create a panel to wrap both labels in so that it is one element in the border layout
        JPanel labelPanel = new JPanel();
        // set the layout of the label panel to box so that the two labels will be stacked on top of one another
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));

        // create the warnings label
        warningsLabel = new JLabel("There are " + exitDialogue.getExitReport().getAdditionalWarnings() + " additional warnings. See the log ->");
        warningsLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
        warningsLabel.setForeground(Color.RED);

        // create the info label
        infoLabel = new JLabel("For more info, see our website...");
        infoLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
        infoLabel.setForeground(Color.BLUE);
        // add a mouse listener to info label to enable changes on hover, and link on click
        infoLabel.addMouseListener(new LinkMouseListener());

        // add the labels to the label panel
        labelPanel.add(warningsLabel);
        labelPanel.add(infoLabel);

        // create a panel to wrap both buttons in to that it is also one element in the border layout
        JPanel buttonPanel = new JPanel();

        // create the log button and add action and mouse listeners
        logButton = new JButton("Open Log");
        logButton.addActionListener(e -> openLog());
        logButton.addMouseListener(new ButtonMouseListener());

        // create the log button and add action and mouse listeners
        relaunchButton = new JButton("Relaunch");
        relaunchButton.addActionListener(new AbstractAction() {
                                             @Override
                                             public void actionPerformed(ActionEvent e) {
                                                 exitDialogue.setShouldRestart(true);
                                                 exitDialogue.exit();
                                             }
                                         }
        );

        relaunchButton.addMouseListener(new ButtonMouseListener());

        // create the quit button and add action and mouse listeners
        quitButton = new JButton("Quit (" + exitDialogue.getExitReport().getQuitReason().getExitCode() + ")");
        quitButton.addActionListener(e -> exitDialogue.exit());
        quitButton.addMouseListener(new ButtonMouseListener());

        // add the buttons to the button panel
        buttonPanel.add(logButton);
        buttonPanel.add(relaunchButton);
        buttonPanel.add(quitButton);

        // add both panels to the main footer panel
        insetPanel.add(labelPanel, BorderLayout.LINE_START);
        insetPanel.add(buttonPanel, BorderLayout.LINE_END);

        add(insetPanel, gridBagConstraints);
    }

    private void openLog() {
        // create a process to launch notepad to open the log
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", EngineWatchdog.getLogger().getLog().getLogFilePath());
        try {
            pb.start();
        } catch (IOException e) {
            exitDialogue.getLogger().error("Error opening log file:", "" + e, new ThreadState(Thread.currentThread()));
       }
    }

    // subclass, implements MouseListener for the log and quit buttons to change the cursor on hover
    class ButtonMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    // subclass, implements MouseListener for the info label to change colour and cursor on hover, and open browser on click
    class LinkMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop.getDesktop().browse(URI.create("www.parsecGame.com/info"));
            } catch (IOException i) {
                exitDialogue.getLogger().error("Error opening link to web page:", "" + e, new ThreadState(Thread.currentThread()));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) {
            Font font = infoLabel.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            infoLabel.setFont(font.deriveFont(attributes));
            infoLabel.setForeground(new Color(0, 150, 255));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            infoLabel.setForeground(Color.BLUE);
            infoLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
