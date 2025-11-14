package tracerUtils;

import tracerUtils.data.ThreadState;
import tracerUtils.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GeneralDialogue {

    protected Logger logger;
    private String title;
    private String bannerText;

    protected JFrame frame;
    private JPanel dialogueBody;
    private DialogueBanner dialogueBanner;
    private JPanel mainContentPanel;
    private JPanel footerPanel;

    private GridBagConstraints contentConstraints;

    protected boolean quit = false;

    public GeneralDialogue(String title, String bannerText, Logger logger) {
        this.title = title;
        this.bannerText = bannerText;
        this.logger = logger;
        create();
    }

    public void open() {
        try {

            // loop until quit = true to ensure frame stays open
            frame.setVisible(true);
            while (!quit) {
                Thread.sleep(10);
                onUpdate();
            }

            close();

        } catch (Throwable e) {
            logger.error("<!> Error displaying dialog <!>:", e.getMessage(), e, new ThreadState(Thread.currentThread()));
        }
    }

    public void close() {
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
    }

    private void create() {

        // initialise the JFrame
        frame = new JFrame();

        // create body
        dialogueBody = new JPanel();
        dialogueBody.setLayout(new GridBagLayout()); // set the layout to grid bag so that the content can scale dynamically

        // ###### Layout #######
        contentConstraints = new GridBagConstraints();
        contentConstraints.weightx = 1;
        contentConstraints.weighty = 0;
        contentConstraints.fill = GridBagConstraints.BOTH;
        contentConstraints.gridwidth = GridBagConstraints.REMAINDER;
        contentConstraints.anchor = GridBagConstraints.NORTHWEST;

        // ###### Banner ########
        // create the banner
        dialogueBanner = new DialogueBanner(bannerText);
        dialogueBanner.setVisible(true);
        dialogueBody.add(dialogueBanner, contentConstraints);

        // ######## Main content ########
        contentConstraints.fill = GridBagConstraints.BOTH; // now set the fill to BOTH so that the log panel will grow in both directions, as opposed to only horizontally
        contentConstraints.weighty = 1; // now also set the y weight to 1 so that the log panel will be the only element that grows to fit the height

        // create the content panel
        mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setVisible(true);
        dialogueBody.add(mainContentPanel, contentConstraints);

        // ##### Footer #####
        contentConstraints.weighty = 0; // reset y weight to 0

        footerPanel = new JPanel(new GridBagLayout());
        footerPanel.setBackground(new Color(200, 200, 200));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets.set(4, 0 ,4, 0);
        JButton okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setOpaque(false);
        okButton.addActionListener(e -> exit());
        footerPanel.add(okButton, gridBagConstraints);
        footerPanel.setVisible(true);
        dialogueBody.add(footerPanel, contentConstraints);


        // ######## Frame #######

        // add the dialogueBody to the frame
        frame.setContentPane(dialogueBody);
        // ensure that window will close if x is pressed
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                exit();
            }
        });
        // set frame properties
        frame.pack();
        frame.setTitle(title);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        //reset y for content setting
        contentConstraints.weighty = 1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        frame.setTitle(title);
    }

    protected DialogueBanner getDialogueBanner() {
        return dialogueBanner;
    }

    public void hideDialogueBanner(boolean hide) {
        dialogueBanner.setVisible(!hide);
    }

    public void setMainContent(JPanel jPanel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(jPanel, contentConstraints);
        frame.revalidate();
        mainContentPanel.repaint();
    }

    public void setFooterContent(JPanel jPanel) {
        footerPanel.removeAll();
        footerPanel.add(jPanel, contentConstraints);
        frame.revalidate();
        footerPanel.repaint();
    }

    protected void onUpdate() {

    }

    protected void onQuit() {

    };

    public void exit() {
        quit = true;
    }
}
