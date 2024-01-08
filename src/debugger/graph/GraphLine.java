package debugger.graph;
import java.util.ArrayList;

public class GraphLine {

    private GraphLineFormat format = new GraphLineFormat();
    private boolean visible = true;

    private ArrayList<Double> values;

    public GraphLine(ArrayList<Double> values, boolean visible) {
        this.values = values;
        this.visible = visible;
    }

    public GraphLine(ArrayList<Double> values) {
        this.values = values;
    }

    public GraphLineFormat getFormat() {
        return format;
    }

    public void setFormat(GraphLineFormat format) {
        this.format = format;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
