package debugger;

import debugger.tabs.DebuggerGeneralTab;
import engine.engine.EngineCore;
import engine.engineFlags.DebugFlagType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Debugger {

    public Debugger() {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame window = new JFrame();
            window.setLayout(new BorderLayout());

            Label l1 = new Label("             ☐ PARSEC Engine: DEBUG MODE! ☐");
            l1.setBackground(Color.BLUE);
            l1.setForeground(Color.WHITE);
            l1.setFont(new Font("Calibri", Font.BOLD, 14));
            window.add(l1, BorderLayout.PAGE_START);

            JTabbedPane tabPanel = new JTabbedPane();
            // Create the first tab (page1) and add a JLabel to it
            JPanel page1 = DebuggerGeneralTab.get();
            // Create the second tab (page2) and add a JLabel to it
            JPanel page2 = new JPanel();
            // Create the third tab (page3) and add a JLabel to it
            JPanel page3 = new JPanel();
            // Add the three tabs to the JTabbedPane
            tabPanel.addTab("General", page1);
            tabPanel.addTab("Graphics", page2);
            tabPanel.addTab("Physics", page3);

            //JDesktopPane desktopPane = new JDesktopPane();
            //window.add(desktopPane, BorderLayout.CENTER);
            //JInternalFrame internalFrame = createInternalFrame(desktopPane, "Child Frame 1");
            //desktopPane.add(internalFrame);
            //internalFrame.setVisible(true);

            window.add(tabPanel, BorderLayout.CENTER);
            ////////////////////

            window.setJMenuBar(new JMenuBar());
            // Make the JFrame visible

            window.setSize(600, 500);
            window.setTitle("PARSEC | Fatal error | ");
            window.setLocationRelativeTo(null);
            window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            window.setVisible(true);

            while (true) {
                if (!EngineCore.getFlagManager().getDebugFlag(DebugFlagType.DEBUG_PAUSE_ALL).isUp()) {
                    DebuggerGeneralTab.tick();
                    Thread.sleep(100);
                    window.repaint();
                }
            }

        } catch (Throwable e) {

        }
    }

    private static JInternalFrame createInternalFrame(JDesktopPane desktopPane, String title) {

        // Create a new internal frame
        JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
        internalFrame.setBounds(50, 50, 300, 200);

        // Add a text area with the frame's title as content
        JTextArea textArea = new JTextArea(title);
        internalFrame.add(textArea);

        // Create a "Close" button to dispose of the internal frame
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Dispose of the internal frame when the "Close" button is clicked
                internalFrame.dispose();
            }
        });

        // Create a panel for the "Close" button and add it to the internal frame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        internalFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Add the internal frame to the JDesktopPane and make it visible

        return internalFrame;
    }
}
