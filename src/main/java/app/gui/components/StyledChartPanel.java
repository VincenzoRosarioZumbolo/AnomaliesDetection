package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class StyledChartPanel extends ChartPanel {

    public StyledChartPanel(JFreeChart chart) {
        super(chart);

        setBackground(AppColors.EMPTY);
    }
}
