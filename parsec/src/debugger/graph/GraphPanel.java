package debugger.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {

    private int width = 800;
    private int height = 400;
    private int rows = 10;
    private int columns = 10;

    private double paddingLeft = 80;
    private double paddingRight = 20;
    private double paddingTop = 10;
    private double paddingBottom = 10;
    private double scalePadding = 2;

    private Color graphBackgroundColour = Color.LIGHT_GRAY;
    private Color gridColor = Color.BLACK;
    private Stroke gridStroke = new BasicStroke(1);
    private Color axisColour = Color.BLUE;
    private Stroke axisStroke = new BasicStroke(4);
    private Color zeroLineColour = Color.BLACK;
    private Stroke zeroLineStroke = new BasicStroke(2);

    private boolean showScaleLabels = true;
    private Color scaleLabelColour = Color.BLACK;
    private Font scaleLabelFont = null;
    private int rowsPerScaleLabel = 2;
    DecimalFormat scaleLabelFormat = new DecimalFormat();

    private GraphYScalingFormat yScalingFormatTop = GraphYScalingFormat.SCALE_MARGINED_FIT;
    private double yScalingTopFixedValue = 20;
    private double yScalingTopMin = 4;
    private double yScalingTopMax = 2;
    private double yScalingTopMargin = 0.5;
    private GraphYScalingFormat yScalingFormatBottom = GraphYScalingFormat.SCALE_MARGINED_FIT;
    private double yScalingBottomFixedValue = 0;
    private double yScalingBottomMin = -1;
    private double yScalingBottomMax = 0;
    private double yScalingBottomMargin = 0.5;

    private boolean roundMinMaxToInt = true;


    private double maxYBaseline = 0;
    private double minYTopline = 1;

    private List<GraphLine> lines = new ArrayList<>();
    private List<GraphRegion> regions = new ArrayList<>();

    public GraphPanel(List<GraphLine> lines, List<GraphRegion> regions) {
        this.lines = lines;
        this.regions = regions;
        this.setPreferredSize(new Dimension(width, height));
        scaleLabelFormat.setMaximumFractionDigits(3);
        scaleLabelFormat.setMinimumFractionDigits(1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double graphWidth = width - (paddingLeft + paddingRight);
        double graphHeight = height - (paddingTop + paddingBottom);
        double scaleY = graphHeight / (getMaxValue() - getMinValue());

        drawBackground(paddingLeft, paddingTop, graphWidth, graphHeight, g2);
        drawRegions(paddingLeft, paddingTop, graphWidth, graphHeight, scaleY, g2);
        drawGrid(paddingLeft, paddingTop, graphWidth, graphHeight, rows, columns, scaleY, g2);

        for (GraphLine line : lines) {
            if (line.isVisible()) {
                double scaleX = graphWidth / (line.getValues().size() - 1);

                List<Vec2d> graphPoints = new ArrayList<>();
                for (int i = 0; i < line.getValues().size(); i++) {
                    double x = i * scaleX + paddingLeft;
                    double y = (getMaxValue() - line.getValues().get(i)) * scaleY + paddingTop;
                    graphPoints.add(new Vec2d(x, y));
                }
                drawGraphLines(graphPoints, line.getFormat(), paddingTop, g2);
                drawGraphPoints(graphPoints, line.getFormat(), paddingTop, g2);
            }
        }

        drawAxis(paddingLeft, paddingTop, graphWidth, graphHeight, g2);
    }

    private void drawBackground(double originX, double originY, double width, double height, Graphics2D g2) {
        g2.setColor(graphBackgroundColour);
        g2.fill(new Rectangle2D.Double(originX, originY, width, height));
    }

    private void drawGrid(double originX, double originY, double width, double height, double rows, double columns, double scaleY, Graphics2D g2) {
        g2.setStroke(gridStroke);
        g2.setColor(gridColor);

        // horizontal
        for (int i = 0; i < rows + 1; i++) {
            double rowValue = getMinValue() + i * ((getMaxValue() - getMinValue()) / rows);

            double x1 = originX;
            double x2 = originX + width;
            double y = (originY + height) - (rowValue - getMinValue()) * scaleY;

            g2.draw(new Line2D.Double(x1, y, x2, y));

            if (showScaleLabels) {
                if (i % (rowsPerScaleLabel) == 0) {
                    String label = scaleLabelFormat.format(rowValue) + "-";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(label);
                    g2.setColor(scaleLabelColour);
                    g2.drawString(label, (int) (x1 - labelWidth - scalePadding), (int) (y + 4));
                    g2.setColor(gridColor);
                }
            }
        }

        //zero line
        double y = (originY + height) - (-getMinValue()) * scaleY;
        g2.setStroke(zeroLineStroke);
        g2.setColor(zeroLineColour);
        g2.draw(new Line2D.Double(originX, y, originX + width, y));
        g2.setStroke(gridStroke);
        g2.setColor(gridColor);

        // vertical
        for (int i = 1; i < columns + 1; i++) {
            double x = originX + i * (width / columns);
            double y1 = originY;
            double y2 = originY + height;

            g2.draw(new Line2D.Double(x, y1, x, y2));
        }
    }

    private void drawGraphLines(List<Vec2d> points, GraphLineFormat format, double originY, Graphics2D g2) {
        g2.setStroke(format.getLineStroke());
        g2.setColor(format.getLineColour());
        for (int i = 0; i < points.size() - 1; i++) {
            double x1 = points.get(i).getX();
            double y1 = points.get(i).getY();
            double x2 = points.get(i + 1).getX();
            double y2 = points.get(i + 1).getY();

            if (y1 < originY) {
                y1 = originY;
            }

            if (y2 < originY) {
                y2 = originY;
            }

            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }

    private void drawGraphPoints(List<Vec2d> points, GraphLineFormat format, double originY, Graphics2D g2) {
        g2.setStroke(format.getPointStroke());
        g2.setColor(format.getPointColour());
        for (int i = 0; i < points.size(); i++) {
            double x = points.get(i).getX() - (format.getPointSize() / 2);
            double y = points.get(i).getY() - (format.getPointSize() / 2);

            if (y < originY) {
                y = originY;
            }
            g2.fill(new RoundRectangle2D.Double(x, y, format.getPointSize(), format.getPointSize(), 90, 90));
        }
    }

    private void drawAxis(double originX, double originY, double width, double height, Graphics2D g2) {
        g2.setStroke(axisStroke);
        g2.setColor(axisColour);
        //X
        g2.draw(new Line2D.Double(originX, originY + height, originX + width, originY + height));
        //Y
        g2.draw(new Line2D.Double(originX, originY + height, originX, originY));
    }

    private void drawRegions(double originX, double originY, double width, double height, double scaleY, Graphics2D g2) {
        for (GraphRegion region : regions) {
            if (region.isVisible()) {
                g2.setColor(region.getColor());
                double y = originY + (getMaxValue() - region.getUpperBound()) * scaleY;
                double rectHeight = (region.getUpperBound() - region.getLowerBound()) * scaleY;

                if (y < originY) {
                    rectHeight = rectHeight - (originY - y);
                    y = originY;
                }

                if (y + rectHeight > originY + height) {
                    rectHeight = originY + height - y;
                }

                g2.fill(new Rectangle2D.Double(originX, y, width, rectHeight));
            }
        }
    }

    private double getMinValue() {

        if (yScalingFormatBottom == GraphYScalingFormat.SCALE_FIXED) {
            if (roundMinMaxToInt) {
                return Math.floor(yScalingBottomFixedValue);
            }
            return yScalingBottomFixedValue;
        }

        double minScore = Double.MAX_VALUE;
        for (GraphLine line : lines) {
            if (line.isVisible()) {
                for (Double score : line.getValues()) {
                    minScore = Math.min(minScore, score);
                }
            }
        }

        switch (yScalingFormatTop) {
            case SCALE_FIT:
                minScore = minScore;
                break;
            case SCALE_MARGINED_FIT:
                minScore = minScore - yScalingBottomMargin;
                break;
            case SCALE_MAX:
                minScore = Math.min(minScore, yScalingBottomMax);
                break;
            case SCALE_MARGINED_MAX:
                minScore += yScalingBottomMargin;
                minScore = Math.min(minScore, yScalingBottomMax);
                break;
            case SCALE_MIN:
                minScore = Math.max(minScore, yScalingBottomMin);
                break;
            case SCALE_MARGINED_MIN:
                minScore += yScalingBottomMargin;
                minScore = Math.min(minScore, yScalingBottomMin);
        }

        if (roundMinMaxToInt) {
            return Math.floor(minScore);
        }

        return minScore;
    }

    private double getMaxValue() {

        if (yScalingFormatTop == GraphYScalingFormat.SCALE_FIXED) {
            if (roundMinMaxToInt) {
                return Math.ceil(yScalingTopFixedValue);
            }
            return yScalingTopFixedValue;
        }

        double maxScore = Double.MIN_VALUE;
        for (GraphLine line : lines) {
            if (line.isVisible()) {
                for (Double score : line.getValues()) {
                    maxScore = Math.max(maxScore, score);
                }
            }
        }

        switch (yScalingFormatTop) {
            case SCALE_FIT:
                maxScore = maxScore;
                break;
            case SCALE_MARGINED_FIT:
                maxScore = maxScore + yScalingTopMargin;
                break;
            case SCALE_MAX:
                maxScore = Math.min(maxScore, yScalingTopMax);
                break;
            case SCALE_MARGINED_MAX:
                maxScore += yScalingTopMargin;
                maxScore = Math.min(maxScore, yScalingTopMax);
                break;
            case SCALE_MIN:
                maxScore = Math.max(maxScore, yScalingTopMin);
                break;
            case SCALE_MARGINED_MIN:
                maxScore += yScalingTopMargin;
                maxScore = Math.min(maxScore, yScalingTopMin);
                break;
        }

        if (roundMinMaxToInt) {
            return Math.ceil(maxScore);
        }

        return maxScore;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.setPreferredSize(new Dimension(width, height));
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public double getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public double getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public double getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public double getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public Color getGraphBackgroundColour() {
        return graphBackgroundColour;
    }

    public void setGraphBackgroundColour(Color graphBackgroundColour) {
        this.graphBackgroundColour = graphBackgroundColour;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public Stroke getGridStroke() {
        return gridStroke;
    }

    public void setGridStroke(Stroke gridStroke) {
        this.gridStroke = gridStroke;
    }

    public Color getAxisColour() {
        return axisColour;
    }

    public void setAxisColour(Color axisColour) {
        this.axisColour = axisColour;
    }

    public Stroke getAxisStroke() {
        return axisStroke;
    }

    public void setAxisStroke(Stroke axisStroke) {
        this.axisStroke = axisStroke;
    }

    public Color getZeroLineColour() {
        return zeroLineColour;
    }

    public void setZeroLineColour(Color zeroLineColour) {
        this.zeroLineColour = zeroLineColour;
    }

    public Stroke getZeroLineStroke() {
        return zeroLineStroke;
    }

    public void setZeroLineStroke(Stroke zeroLineStroke) {
        this.zeroLineStroke = zeroLineStroke;
    }

    public double getMaxYBaseline() {
        return maxYBaseline;
    }

    public void setMaxYBaseline(double maxYBaseline) {
        this.maxYBaseline = maxYBaseline;
    }

    public double getMinYTopline() {
        return minYTopline;
    }

    public void setMinYTopline(double minYTopline) {
        this.minYTopline = minYTopline;
    }

    public double getScalePadding() {
        return scalePadding;
    }

    public void setScalePadding(int scalePadding) {
        this.scalePadding = scalePadding;
    }

    public boolean isShowScaleLabels() {
        return showScaleLabels;
    }

    public void setShowScaleLabels(boolean showScaleLabels) {
        this.showScaleLabels = showScaleLabels;
    }

    public int getScaleMaxPrecision() {
        return scaleLabelFormat.getMaximumFractionDigits();
    }

    public void setScaleMaxPrecision(int scalePrecision) {
        scaleLabelFormat.setMaximumFractionDigits(scalePrecision);
    }

    public void setScaleMinPrecision(int scalePrecision) {
        scaleLabelFormat.setMinimumFractionDigits(scalePrecision);
    }

    public int getRowsPerScaleLabel() {
        return rowsPerScaleLabel;
    }

    public void setRowsPerScaleLabel(int rowsPerScaleLabel) {
        this.rowsPerScaleLabel = rowsPerScaleLabel;
    }

    public Color getScaleLabelColour() {
        return scaleLabelColour;
    }

    public void setScaleLabelColour(Color scaleLabelColour) {
        this.scaleLabelColour = scaleLabelColour;
    }

    public Font getScaleLabelFont() {
        return scaleLabelFont;
    }

    public void setScaleLabelFont(Font scaleLabelFont) {
        this.scaleLabelFont = scaleLabelFont;
    }

    public void setPaddingLeft(double paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingRight(double paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(double paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(double paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setScalePadding(double scalePadding) {
        this.scalePadding = scalePadding;
    }

    public DecimalFormat getScaleLabelFormat() {
        return scaleLabelFormat;
    }

    public void setScaleLabelFormat(DecimalFormat scaleLabelFormat) {
        this.scaleLabelFormat = scaleLabelFormat;
    }

    public GraphYScalingFormat getYScalingFormatTop() {
        return yScalingFormatTop;
    }

    public void setYScalingFormatTop(GraphYScalingFormat yScalingFormatTop) {
        this.yScalingFormatTop = yScalingFormatTop;
    }

    public double getYScalingTopFixedValue() {
        return yScalingTopFixedValue;
    }

    public void setYScalingTopFixedValue(double yScalingTopFixedValue) {
        this.yScalingTopFixedValue = yScalingTopFixedValue;
    }

    public double getYScalingTopMin() {
        return yScalingTopMin;
    }

    public void setYScalingTopMin(double yScalingTopMin) {
        this.yScalingTopMin = yScalingTopMin;
    }

    public double getYScalingTopMax() {
        return yScalingTopMax;
    }

    public void setYScalingTopMax(double yScalingTopMax) {
        this.yScalingTopMax = yScalingTopMax;
    }

    public double getYScalingTopMargin() {
        return yScalingTopMargin;
    }

    public void setYScalingTopMargin(double yScalingTopMargin) {
        this.yScalingTopMargin = yScalingTopMargin;
    }

    public GraphYScalingFormat getYScalingFormatBottom() {
        return yScalingFormatBottom;
    }

    public void setYScalingFormatBottom(GraphYScalingFormat yScalingFormatBottom) {
        this.yScalingFormatBottom = yScalingFormatBottom;
    }

    public double getYScalingBottomFixedValue() {
        return yScalingBottomFixedValue;
    }

    public void setYScalingBottomFixedValue(double yScalingBottomFixedValue) {
        this.yScalingBottomFixedValue = yScalingBottomFixedValue;
    }

    public double getYScalingBottomMin() {
        return yScalingBottomMin;
    }

    public void setYScalingBottomMin(double yScalingBottomMin) {
        this.yScalingBottomMin = yScalingBottomMin;
    }

    public double getYScalingBottomMax() {
        return yScalingBottomMax;
    }

    public void setYScalingBottomMax(double yScalingBottomMax) {
        this.yScalingBottomMax = yScalingBottomMax;
    }

    public double getYScalingBottomMargin() {
        return yScalingBottomMargin;
    }

    public void setYScalingBottomMargin(double yScalingBottomMargin) {
        this.yScalingBottomMargin = yScalingBottomMargin;
    }

    public boolean isRoundMinMaxToInt() {
        return roundMinMaxToInt;
    }

    public void setRoundMinMaxToInt(boolean roundMinMaxToInt) {
        this.roundMinMaxToInt = roundMinMaxToInt;
    }

    public List<GraphLine> getLines() {
        return lines;
    }

    public void setLines(List<GraphLine> lines) {
        this.lines = lines;
    }

    public List<GraphRegion> getRegions() {
        return regions;
    }

    public void setRegions(List<GraphRegion> regions) {
        this.regions = regions;
    }
}