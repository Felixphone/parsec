package tracerUtils;

import javax.swing.*;
import java.awt.*;

public class DialogueBanner extends JPanel {

    private String text;
    private Color foreground = Color.WHITE;
    private Color background = Color.BLUE;
    private Font font = new Font("Calibri", Font.BOLD, 14);

    private Label label;

    protected DialogueBanner(String text) {
        super(new GridBagLayout());
        this.text = "☐ " + text + " ☐";

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1; // set the x weight to 1 so that content fills the entire width of the panel
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL; // ensure content fills horizontal width
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST; // anchor the content to the top left so that it scales properly
        gridBagConstraints.weighty = 0; // to start with, set the y weight to 0 so that banner and overview panels do not grow vertically

        label = new Label(this.text, Label.CENTER);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(font);

        add(label, gridBagConstraints);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = "☐ " + text + " ☐";
        label.setText(this.text);
        repaint();
    }

    public Color getBannerForeground() {
        return foreground;
    }

    public void setBannerForeground(Color foreground) {
        label.setForeground(foreground);
        this.foreground = foreground;
        repaint();
    }

    public Color getBannerBackground() {
        return background;
    }

    public void setBannerBackground(Color background) {
        label.setBackground(background);
        this.background = background;
        repaint();
    }

    public Font getBannerFont() {
        return font;
    }

    public void setBannerFont(Font font) {
        label.setFont(font);
        this.font = font;
        repaint();
    }
}