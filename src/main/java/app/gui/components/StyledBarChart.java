package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * A customized bar chart implementation that inherits core styling from {@link StyledChart}.
 * <p>
 * This class uses a time-based X-axis ({@link DateAxis}) and a numerical Y-axis ({@link NumberAxis})
 * to display interval data. It configures an {@link XYBarRenderer} with a flat
 * design, disabled shadows, and a dedicated theme color.
 * </p>
 *
 * @see StyledChart
 */
public class StyledBarChart extends StyledChart {

    /**
     * Constructs a styled bar chart with time-series data intervals.
     *
     * @param title       the main title of the chart
     * @param xAxisLabel  the descriptive label for the time (X) axis
     * @param yAxisLabel  the descriptive label for the value (Y) axis
     * @param dataset     the interval dataset used to populate the bar chart
     */
    public StyledBarChart(String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset) {
        super(title, new XYPlot(dataset, new DateAxis(xAxisLabel), new NumberAxis(yAxisLabel), null));

        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, AppColors.CHART_SCORE);

        ((XYPlot)getPlot()).setRenderer(renderer);
    }
}