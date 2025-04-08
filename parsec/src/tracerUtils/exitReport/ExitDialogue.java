package tracerUtils.exitReport;

import tracerUtils.GeneralDialogue;
import tracerUtils.logger.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ExitDialogue extends GeneralDialogue {

    private final ExitReport exitReport;
    private boolean shouldRestart = false;

    public ExitDialogue(ExitReport exitReport, Logger logger) {
        super("PARSEC | Successfully exited", "Successfully exited engine!", logger);
        this.exitReport = exitReport;
        getDialogueBanner().setBannerBackground(Color.GREEN.darker());
        setMainContent(new ExitPanel(this));
        setFooterContent(new FooterPanel(this));
    }

    @Override
    public void open() {
        super.open();
    }

    ExitReport getExitReport() {
        return exitReport;
    }

    Logger getLogger() {
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

    private final Label sucessLabel;
    private final Label exitCodeLabel;
    private final Label borderLabel;
    private final Label reportLabel;

    public OverviewPanel(ExitDialogue exitDialogue) {
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
        sucessLabel = new Label("Successfully exited engine: " + exitDialogue.getExitReport().quitReason.name());
        exitCodeLabel = new Label("Exit code: " + exitDialogue.getExitReport().quitReason.getExitCode());

        add(sucessLabel, gridBagConstraints);
        add(exitCodeLabel, gridBagConstraints);

        borderLabel = new Label("------------------------------------------------------------ ");
        add(borderLabel, gridBagConstraints);

        reportLabel = new Label("Below is the exit report:");

        // remove the inset for the final label to prevent log panel being pushed upwards
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        add(reportLabel, gridBagConstraints);
    }
}

class ExitPanel extends JPanel {

    public ExitPanel(ExitDialogue exitDialogue) {

        //set the layout to grid bag so that the content can scale dynamically
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1; // set the x weight to 1 so that content fills the entire width of the panel
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL; // ensure content fills horizontal width
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST; // anchor the content to the top left so that it scales properly
        gridBagConstraints.insets = new Insets(0, 8, 0, 8);

        // add the overview panel, passing in the exit report
        add(new OverviewPanel(exitDialogue), gridBagConstraints);

        // now set the fill to BOTH so that the log panel will grow in both directions, as opposed to only horizontally
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        // now also set the y weight to 1 so that the log panel will be the only element that grows to fit the height
        gridBagConstraints.weighty = 1;

        // create the log panel
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new GridBagLayout());

        // turn the exit report into a string and add padding spaces
        String report = "";
        for (String line : exitDialogue.getExitReport().getInfo()) {
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
        logPanel.setBorder(new TitledBorder("Exit report: "));

        // reset the insets and add the log panel
        gridBagConstraints.insets = new Insets(0, 8, 0, 8);
        add(logPanel, gridBagConstraints);
    }
}