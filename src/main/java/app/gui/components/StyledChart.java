package app.gui.components;

import app.gui.style.AppColors;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import java.awt.*;

/**
 * An abstract base class for customized JFreeChart charts within the application.
 * <p>
 * This class extends {@link JFreeChart} to provide a unified, clean, and modern
 * visual theme across different chart types. It configures anti-aliasing,
 * background colors, borders, typography, and axes styling.
 * </p>
 */
public abstract class StyledChart extends JFreeChart {

    /**
     * Constructs a new StyledChart with a specific title and plot configuration,
     * and automatically applies the corporate styling rules.
     *
     * @param title the title of the chart, displayed at the top
     * @param plot  the {@link XYPlot} containing the dataset and axes configuration
     */
    public StyledChart(String title, XYPlot plot) {
        super(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        formatChart();
    }

    /**
     * Applies uniform visual formatting to the chart components.
     * <p>
     * This includes enabling anti-aliasing, setting fonts and colors for the
     * title, removing legend borders, hiding domain gridlines, and formatting
     * the plot axes and range gridlines.
     * </p>
     */
    private void formatChart() {
        this.setAntiAlias(true);
        this.setTextAntiAlias(true);

        this.setBackgroundPaint(AppColors.BACKGROUND_WHITE);
        this.setBorderVisible(false);

        TextTitle chartTitle = this.getTitle();
        chartTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        chartTitle.setPaint(AppColors.PRIMARY);
        chartTitle.setMargin(15, 0, 15, 0);

        if (this.getLegend() != null) {
            this.getLegend().setFrame(BlockBorder.NONE);
            this.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 12));
            this.getLegend().setBackgroundPaint(AppColors.EMPTY);
        }

        XYPlot plot = (XYPlot) this.getPlot();
        plot.setBackgroundPaint(AppColors.EMPTY);
        plot.getDomainAxis().setLabelPaint(AppColors.PRIMARY_HOVER);
        plot.getRangeAxis().setLabelPaint(AppColors.PRIMARY_HOVER);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(235, 235, 235));
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(false);

        plot.getDomainAxis().setAxisLinePaint(AppColors.TEXT);
        plot.getRangeAxis().setAxisLinePaint(AppColors.TEXT);
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
    }
}