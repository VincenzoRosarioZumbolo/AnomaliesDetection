package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;

public class StyledBarChart extends StyledChart {

    public StyledBarChart(String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset) {

        super(title, new XYPlot(dataset, new DateAxis(xAxisLabel), new NumberAxis(yAxisLabel), null));

        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, AppColors.CHART_SCORE);

        ((XYPlot)getPlot()).setRenderer(renderer);
    }
}