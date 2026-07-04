package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import java.awt.*;

/**
 * A customized line chart implementation that inherits core styling from {@link StyledChart}.
 * <p>
 * This chart features a time-based X-axis ({@link DateAxis}) and supports up to two distinct
 * data series. It styles the lines with custom weights (strokes) and colors, hiding individual
 * data point shapes for a cleaner presentation.
 * </p>
 *
 * @see StyledChart
 */
public class StyledLineChart extends StyledChart {

    /**
     * Constructs a styled line chart for time-series data.
     *
     * @param title       the main title of the chart
     * @param xAxisLabel  the descriptive label for the time (X) axis
     * @param yAxisLabel  the descriptive label for the value (Y) axis
     * @param dataset     the dataset containing the series data for the line chart
     */
    public StyledLineChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset) {
        super(title, new XYPlot(dataset, new DateAxis(xAxisLabel), new NumberAxis(yAxisLabel), null));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

        renderer.setSeriesPaint(0, AppColors.CHART_LINE1);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));

        renderer.setSeriesPaint(1, AppColors.CHART_LINE2);
        renderer.setSeriesStroke(1, new BasicStroke(2.5f));

        ((XYPlot)getPlot()).setRenderer(renderer);
    }
}