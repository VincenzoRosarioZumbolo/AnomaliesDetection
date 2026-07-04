package app.gui.components;

import app.gui.style.PaddingConstants;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A dynamic GUI container that manages and renders a collection of {@link JFreeChart} objects
 * within a flexible layout.
 * <p>
 * This panel supports an interactive "focus" feature. Clicking on any chart inside
 * the container maximizes it to fill the entire panel view. Clicking the maximized chart
 * a second time restores the container to its original multi-chart grid layout.
 * </p>
 *
 * @see InvisiblePanel
 * @see StyledChartPanel
 */
public class DynamicChartContainer extends InvisiblePanel {

    /** The index of the currently maximized chart, or -1 if all charts are displayed. */
    private int focusedChartIndex = -1;

    /** The list of charts managed by this container. */
    private List<JFreeChart> charts;

    /**
     * Constructs a new, empty DynamicChartContainer using a {@link GridBagLayout}.
     */
    public DynamicChartContainer() {
        super(new GridBagLayout());
        this.charts = new ArrayList<>();
    }

    /**
     * Updates the collection of charts managed by this container.
     * <p>
     * This resets any active chart focus and triggers a fresh layout rendering.
     * </p>
     *
     * @param charts the new list of {@link JFreeChart} objects to display
     */
    public void setCharts(List<JFreeChart> charts) {
        this.charts = charts;
        this.focusedChartIndex = -1;
        renderCharts();
    }

    /**
     * Clears and reconstructs the visual layout based on the current focus state.
     * <p>
     * If a chart is focused, only that chart is rendered. Otherwise, all charts in
     * the collection are displayed side-by-side in a single row.
     * </p>
     */
    private void renderCharts() {
        this.removeAll();

        if (charts != null && !charts.isEmpty()) {
            if (focusedChartIndex != -1) {
                addChartToGrid(charts.get(focusedChartIndex), focusedChartIndex, 0);
            } else {
                for (int i = 0; i < charts.size(); i++) {
                    addChartToGrid(charts.get(i), i, i);
                }
            }
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Wraps a chart in a {@link StyledChartPanel}, attaches interactivity, and injects
     * it into the layout grid.
     * <p>
     * Left-clicking the panel toggles between the focused (maximized) view and the
     * standard grid view. Right-clicks are explicitly ignored to allow context menus.
     * </p>
     *
     * @param chart  the {@link JFreeChart} instance to display
     * @param index  the original index of this chart within the managed list
     * @param gridX  the X-coordinate column position inside the {@link GridBagLayout}
     */
    private void addChartToGrid(JFreeChart chart, int index, int gridX) {
        StyledChartPanel chartPanel = new StyledChartPanel(chart);

        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) return;

                focusedChartIndex = (focusedChartIndex == -1) ? index : -1;

                renderCharts();
            }
        });

        this.add(chartPanel, new GridBagConstraints(gridX, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_STANDARD, 0, 0));
    }
}