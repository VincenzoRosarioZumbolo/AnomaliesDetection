package app.gui.views;

import app.gui.components.DynamicChartContainer;
import app.gui.components.InvisiblePanel;
import org.jfree.chart.JFreeChart;

import java.awt.*;
import java.util.List;

/**
 * An abstract base panel responsible for managing and displaying a collection of graphical charts.
 * <p>
 * Extends {@link InvisiblePanel} and sets up a {@link BorderLayout}, encapsulating a dynamic
 * grid container to host and layout {@link JFreeChart} instances.
 * </p>
 */
public abstract class AbstractChartsPanel extends InvisiblePanel {

    /** The dynamic graphical container responsible for arranging and rendering the chart plots. */
    protected final DynamicChartContainer chartContainer;

    /**
     * Constructs an {@code AbstractChartsPanel} instance, initializing the dynamic
     * chart container and anchoring it to the center of the layout region.
     */
    public AbstractChartsPanel() {
        super(new BorderLayout());
        this.chartContainer = new DynamicChartContainer();
        this.add(chartContainer, BorderLayout.CENTER);
    }

    /**
     * Updates the list of charts currently hosted within the dynamic container,
     * triggering a UI repaint to display the new graphical elements.
     *
     * @param charts the {@link List} of {@link JFreeChart} instances to display on screen
     */
    protected void setCharts(List<JFreeChart> charts) {
        chartContainer.setCharts(charts);
    }
}