package tracerUtils.exitReport;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CollapsiblePanel extends JPanel {
    private JPanel headerPanel;
    private final JPanel contentPanel;
    private JButton toggleButton;
    private boolean isCollapsed;
    private Color headerColour = Color.LIGHT_GRAY;
    private Color headerHoverColour = new Color(162, 162, 162);
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();


    public CollapsiblePanel(String headerTitle) {
        setLayout(new BorderLayout());
        JPanel headerPanel = createHeaderPanel(headerTitle);
        contentPanel = createContentPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setCollapsed(true);
    }

    private JPanel createHeaderPanel(String headerTitle) {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        toggleButton = new JButton("▶");
        toggleButton.setBackground(Color.GRAY);
        toggleButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(1, 2, 1, 2)));
        toggleButton.setFocusPainted(false);

        headerPanel.add(new JLabel(" " + headerTitle + " "), BorderLayout.WEST);
        headerPanel.add(toggleButton, BorderLayout.EAST);

        headerPanel.addMouseListener(new MouseToggleListener());

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        panel.setLayout(new GridBagLayout());

        gridBagConstraints.insets.set(0, 5, 0, 5);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;

        return panel;
    }

    public void addToContentPanel(Component component) {
        contentPanel.add(component, gridBagConstraints);
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        contentPanel.setVisible(!isCollapsed);
        toggleButton.setText(isCollapsed ? "▶" : "▼");
    }

    public void toggleCollapsed() {
        isCollapsed = !isCollapsed;
        contentPanel.setVisible(!isCollapsed);
        toggleButton.setText(isCollapsed ? "▶" : "▼");
    }

    public Color getHeaderHoverColour() {
        return headerHoverColour;
    }

    public void setHeaderHoverColour(Color headerHoverColour) {
        this.headerHoverColour = headerHoverColour;
    }

    public Color getHeaderColour() {
        return headerColour;
    }

    public void setHeaderColour(Color headerColour) {
        this.headerColour = headerColour;
        headerPanel.setBackground(headerColour);
    }

    private class MouseToggleListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            toggleCollapsed();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            headerPanel.setBackground(headerHoverColour);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            headerPanel.setBackground(headerColour);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}