package debugger.graph;

import java.awt.*;

public class GraphLineFormat {

    private Stroke lineStroke;
    private Color lineColour;
    private Stroke pointStroke;
    private Color pointColour;
    private int pointSize;

    public GraphLineFormat() {
        lineStroke = new BasicStroke(1);
        lineColour = Color.BLACK;
        pointStroke = new BasicStroke(1);
        pointColour = Color.BLACK;
        pointSize = 4;
    }

    public GraphLineFormat(Stroke lineStroke, Color lineColour, Stroke pointStroke, Color pointColour, int pointSize) {
        this.lineStroke = lineStroke;
        this.lineColour = lineColour;
        this.pointStroke = pointStroke;
        this.pointColour = pointColour;
        this.pointSize = pointSize;
    }

    public Stroke getLineStroke() {
        return lineStroke;
    }

    public void setLineStroke(Stroke lineStroke) {
        this.lineStroke = lineStroke;
    }

    public Color getLineColour() {
        return lineColour;
    }

    public void setLineColour(Color lineColour) {
        this.lineColour = lineColour;
    }

    public Stroke getPointStroke() {
        return pointStroke;
    }

    public void setPointStroke(Stroke pointStroke) {
        this.pointStroke = pointStroke;
    }

    public Color getPointColour() {
        return pointColour;
    }

    public void setPointColour(Color pointColour) {
        this.pointColour = pointColour;
    }

    public int getPointSize() {
        return pointSize;
    }

    public void setPointSize(int pointSize) {
        this.pointSize = pointSize;
    }
}
