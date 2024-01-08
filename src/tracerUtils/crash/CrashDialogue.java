package tracerUtils.crash;

import tracerUtils.GeneralDialogue;
import tracerUtils.data.ThreadState;
import tracerUtils.exitReport.CrashReport;
import tracerUtils.exitReport.ExitDialogue;
import tracerUtils.exitReport.FooterPanel;
import tracerUtils.logger.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

public final class CrashDialogue extends ExitDialogue {

    private CrashReport crashReport;
    private boolean shouldRestart = false;

    public CrashDialogue(CrashReport crashReport, Logger logger) {
        super(crashReport, logger);
        setTitle("PARSEC | Fatal error | " + crashReport.getPhrase());
        getDialogueBanner().setText("An unhandleable exception was encountered!");
        this.crashReport = crashReport;
        this.logger = logger;
        getDialogueBanner().setBannerBackground(Color.RED);
        setMainContent(new CrashPanel(this));
        setFooterContent(new FooterPanel(this));
    }

    @Override
    public void open() {
        try {
            // play exclamation sound
            final Runnable runnable =
                    (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
            if (runnable != null) runnable.run();
        } catch (Throwable e) {
            logger.error("<!> Error displaying dialog <!>:", e.getMessage(), new ThreadState(Thread.currentThread()));
        }

        super.open();
    }

    protected CrashReport getCrashReport() {
        return crashReport;
    }

    protected Logger getLogger() {
        return logger;
    }

    public boolean shouldRelaunch() {
        return shouldRestart;
    }

    public void setShouldRestart(boolean shouldRestart) {
        this.shouldRestart = shouldRestart;
    }
}

class OverviewPanel extends JPanel {

    private final Label errorLabel;
    private final Label sourceLabel;
    private final Label borderLabel;
    private final Label walkThroughLabel;

    public OverviewPanel(CrashDialogue crashDialogue) {
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
        // add negative insets on the bottom of text to reduce line gap
        gridBagConstraints.insets = new Insets(0, 0, -5, 0);

        //create and add the labels
        errorLabel = new Label(" Fatal Error: " + crashDialogue.getCrashReport().getException().getExceptionName());
        add(errorLabel, gridBagConstraints);

        sourceLabel = new Label(" Called by: " + crashDialogue.getCrashReport().getException().getSource());
        add(sourceLabel, gridBagConstraints);

        borderLabel = new Label(" ------------------------------------------------------------ ");
        add(borderLabel, gridBagConstraints);

        walkThroughLabel = new Label(" Below is a walk-through of what happened:");

        // remove the inset for the final label to prevent log panel being pushed upwards
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        add(walkThroughLabel, gridBagConstraints);
    }
}

class CrashPanel extends JPanel {

    public CrashPanel(CrashDialogue crashDialogue) {

        //set the layout to grid bag so that the content can scale dynamically
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1; // set the x weight to 1 so that content fills the entire width of the panel
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL; // ensure content fills horizontal width
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST; // anchor the content to the top left so that it scales properly

        gridBagConstraints.weighty = 0; // to start with, set the y weight to 0 so that banner and overview panels do not grow vertically

        // set the insets so that the main content is not squashed against the sides
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);

        // add the overview panel, passing in the crash report
        add(new OverviewPanel(crashDialogue), gridBagConstraints);

        // now set the fill to BOTH so that the log panel will grow in both directions, as opposed to only horizontally
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        // now also set the y weight to 1 so that the log panel will be the only element that grows to fit the height
        gridBagConstraints.weighty = 1;

        // create the log panel
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new GridBagLayout());

        // turn the crash report into a string and add padding spaces
        String report = "";
        for (String line : crashDialogue.getCrashReport().getInfo()) {
            report += line + "\n";
        }

        // create the text area to go inside the log panel
        JTextArea log = new JTextArea(report);
        log.setMargin(new Insets(0, 2, 0 ,0));
        log.setBackground(Color.GRAY);
        log.setEditable(false);

        // create and wrap the log in a scroll pane so that the content is not cut off, and can be scrolled
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(600, 400));
        scrollPane.setViewportView(log);

        // remove the insets so that the scroll pane fits exactly into the log panel
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        logPanel.add(scrollPane, gridBagConstraints);

        // create the border around the log
        logPanel.setBorder(new TitledBorder("Crash report: "));

        // reset the insets and add the log panel
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        add(logPanel, gridBagConstraints);
    }
}