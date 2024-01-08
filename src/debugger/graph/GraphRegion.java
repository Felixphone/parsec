package debugger.graph;

import java.awt.*;

public class GraphRegion {

    private double upperBound = 1;
    private double lowerBound = 0;
    private Color color = Color.LIGHT_GRAY;
    private boolean visible = true;

    public GraphRegion(double upperBound, double lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public GraphRegion(double lowerBound, double upperBound, Color color) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.color = color;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }
}
