package debugger.tabs;

import debugger.graph.GraphLine;
import debugger.graph.GraphPanel;
import debugger.graph.GraphRegion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebuggerGeneralTab {
    private static GraphPanel graphPanel1;
    private static GraphPanel graphPanel2;

    private static Random random = new Random();
    private static float num = 0;
    private static int maxScore = 1;

    private static GraphLine line;
    private static GraphLine line2;
    private static GraphLine line3;
    private static GraphLine line4;

    private static double prevTime = 0;

    public static JPanel get() {
        JPanel panel = new JPanel();
        ArrayList<Double> scores = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            scores.add(0.0);
        }
        scores.add(random.nextDouble() * maxScore);

        line = new GraphLine(scores);
        line.getFormat().setLineColour(Color.BLUE);
        line.getFormat().setLineStroke(new BasicStroke(2));
        line.getFormat().setPointColour(Color.BLACK);

        scores = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            scores.add(0.0);
        }
        line2 = new GraphLine(scores);
        line2.getFormat().setLineColour(Color.LIGHT_GRAY);
        line2.getFormat().setLineStroke(new BasicStroke(2));
        line2.getFormat().setPointColour(Color.GRAY);

        scores = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            scores.add(0.0);
        }
        line3 = new GraphLine(scores);
        line3.getFormat().setLineColour(Color.BLUE);
        line3.getFormat().setLineStroke(new BasicStroke(2));
        line3.getFormat().setPointColour(Color.BLACK);

        scores = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            scores.add(0.0);
        }
        line4 = new GraphLine(scores);
        line4.getFormat().setLineColour(Color.RED);
        line4.getFormat().setLineStroke(new BasicStroke(2));
        line4.getFormat().setPointColour(Color.RED);

        List<GraphLine> lines = new ArrayList<>();
        //lines.add(line);
        //lines.add(line2);
        lines.add(line3);
        lines.add(line4);

        GraphRegion region1 = new GraphRegion(-0.5, 0.5, new Color(0, 255, 0, 100));
        GraphRegion region2;
        GraphRegion region3;
        GraphRegion region4;
        GraphRegion region5;

        List<GraphRegion> regions = new ArrayList<>();
        regions.add(region1);

        graphPanel1 = new GraphPanel(lines, regions);
        graphPanel1.setWidth(1000);
        graphPanel1.setHeight(300);
        graphPanel1.setRows(50);
        graphPanel1.setGraphBackgroundColour(Color.BLACK);
        graphPanel1.setBackground(Color.BLACK);
        graphPanel1.setGridColor(Color.BLACK);
        graphPanel1.setAxisColour(Color.GRAY);
        graphPanel1.setScaleLabelColour(Color.CYAN);
        panel.add(graphPanel1);

        lines = new ArrayList<>();
        //lines.add(line);
        lines.add(line2);

        scores = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            scores.add(0.0);
        }

        region1 = new GraphRegion(11, 100, new Color(0, 42, 255, 100));
        region2 = new GraphRegion(9, 11, new Color(0, 150, 255, 100));
        region3 = new GraphRegion(6, 9, new Color(0, 255, 0, 100));
        region4 = new GraphRegion(3, 6, new Color(255, 255, 0, 100));
        region5 = new GraphRegion(0, 3, new Color(255, 60, 0, 100));

        regions = new ArrayList<>();
        regions.add(region1);
        regions.add(region2);
        regions.add(region3);
        regions.add(region4);
        regions.add(region5);

        scores.add(random.nextDouble() * maxScore);
        graphPanel2 = new GraphPanel(lines, regions);
        graphPanel2.setWidth(1000);
        graphPanel2.setHeight(200);
        graphPanel2.setRows(10);
        graphPanel2.setGraphBackgroundColour(Color.BLACK);
        graphPanel2.setBackground(Color.BLACK);
        graphPanel2.setGridColor(Color.BLACK);
        graphPanel2.setAxisColour(Color.GRAY);
        graphPanel2.setScaleLabelColour(Color.CYAN);
        graphPanel2.setYScalingTopMargin(1);
        graphPanel2.setYScalingBottomMargin(1);
        graphPanel2.setRoundMinMaxToInt(true);
        panel.add(graphPanel2);

        prevTime = System.currentTimeMillis();

        JButton logButton = new JButton("PAUSE");
        logButton.setBounds(365, 330, 100, 30);
        logButton.addActionListener(e -> {
        });

        panel.add(logButton);

        panel.setBackground(Color.BLACK);
        return panel;
    }

    public static void tick() {

        if (line.getValues().size() > 50) {
            //line.getValues().removeFirst();
        }
        line.getValues().add((double) Runtime.getRuntime().freeMemory() / 1000000);

        if (line2.getValues().size() > 50) {
            //line2.getValues().removeFirst();
        }

        double currentTime = System.currentTimeMillis();
        double delta = currentTime - prevTime;
        prevTime = currentTime;
        line2.getValues().add(1 / (delta / 1000));

        if (line3.getValues().size() > 180) {
            //line3.getValues().removeFirst();
        }

        line3.getValues().add(Math.sin(num));

        if (line4.getValues().size() > 180) {
            //line4.getValues().removeFirst();
        }

        line4.getValues().add(Math.sin(num / 2));
        num += 0.1f;
    }
}
