package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import java.awt.*;

public class StyledLineChart extends StyledChart {

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