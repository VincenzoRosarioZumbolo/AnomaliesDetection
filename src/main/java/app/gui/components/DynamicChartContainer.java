package app.gui.components;

import app.gui.style.PaddingConstants;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DynamicChartContainer extends InvisiblePanel {

    private int focusedChartIndex = -1;
    private List<JFreeChart> charts;

    public DynamicChartContainer() {

        super(new GridBagLayout());
        this.charts = new ArrayList<>();
    }

    public void setCharts(List<JFreeChart> charts) {

        this.charts = charts;
        this.focusedChartIndex = -1;
        renderCharts();
    }

    private void renderCharts() {

        this.removeAll();

        if (charts != null && !charts.isEmpty())
            if (focusedChartIndex != -1)
                addChartToGrid(charts.get(focusedChartIndex), focusedChartIndex, 0);
            else
                for (int i = 0; i < charts.size(); i++)
                    addChartToGrid(charts.get(i), i, i);

        this.revalidate();
        this.repaint();
    }

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