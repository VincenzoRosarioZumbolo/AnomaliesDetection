package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * A Swing GUI component container for holding and displaying {@link JFreeChart} objects.
 * <p>
 * This wrapper extends {@link ChartPanel} to ensure that the panel background matches
 * the application's clean design system, allowing transparent or custom backgrounds
 * behind the rendered chart.
 * </p>
 *
 * @see ChartPanel
 */
public class StyledChartPanel extends ChartPanel {

    /**
     * Constructs a new StyledChartPanel containing the specified chart.
     *
     * @param chart the {@link JFreeChart} instance to display within the panel container
     */
    public StyledChartPanel(JFreeChart chart) {
        super(chart);

        // Sets the container background to match application styling
        setBackground(AppColors.EMPTY);
    }
}