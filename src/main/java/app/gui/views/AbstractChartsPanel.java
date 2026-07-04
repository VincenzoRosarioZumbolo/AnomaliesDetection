package app.gui.views;

import app.gui.components.DynamicChartContainer;
import app.gui.components.InvisiblePanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.util.List;

public abstract class AbstractChartsPanel extends InvisiblePanel {

    protected final DynamicChartContainer chartContainer;

    public AbstractChartsPanel() {
        super(new BorderLayout());
        this.chartContainer = new DynamicChartContainer();
        this.add(chartContainer, BorderLayout.CENTER);
    }

    protected void setCharts(List<JFreeChart> charts) {
        chartContainer.setCharts(charts);
    }
}