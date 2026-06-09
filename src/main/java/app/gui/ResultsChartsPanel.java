package app.gui;

import app.gui.components.DynamicChartContainer;
import app.gui.components.InvisiblePanel;
import app.gui.components.StyledLineChart;
import app.model.AppState;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;

/**
 * A panel container that handles standard historical asset value timelines.
 * <p>
 * Extracts contextual state sequences from the application model registry, generating standard
 * timeline representations for valuation boundaries, candle extrema, and raw transactional volume counts.
 * </p>
 *
 * @see InvisiblePanel
 * @see DynamicChartContainer
 * @see AppState
 */
public class ResultsChartsPanel extends InvisiblePanel {

    /** The internal canvas managing zoom focus adjustments across temporal chart models. */
    private final DynamicChartContainer chartContainer;

    /**
     * Constructs a ResultsChartsPanel, initializing layout configurations
     * and nesting the dynamic display grid.
     */
    public ResultsChartsPanel() {
        super(new BorderLayout());

        this.chartContainer = new DynamicChartContainer();
        this.add(chartContainer, BorderLayout.CENTER);
        this.setVisible(true);
    }

    /**
     * Aggregates line plot assets from current application data configurations
     * and maps them onto the interactive display grid.
     */
    public void createCharts() {
        chartContainer.setCharts(Arrays.asList(
                createOpensClosuresChart(),
                createHighsLowsChart(),
                createVolumesChart()
        ));
    }

    /**
     * Compiles opening and closing asset price metrics over time into a configured line chart.
     *
     * @return a structured line chart visualizing transactional asset variations
     */
    private JFreeChart createOpensClosuresChart() {
        TimeSeries opensSerie = new TimeSeries("Opens");
        TimeSeries closuresSerie = new TimeSeries("Closures");

        for (int i = 0; i < AppState.getInstance().getInstants().size(); i++) {
            opensSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getOpens().get(i));
            closuresSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getClosures().get(i));
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(opensSerie);
        dataset.addSeries(closuresSerie);

        return new StyledLineChart("Opens and Closures", "Time", "Value", dataset);
    }

    /**
     * Compiles maximum high and minimum low asset price bounds over time into a configured line chart.
     *
     * @return a structured line chart tracking valuation extrema boundaries
     */
    private JFreeChart createHighsLowsChart() {
        TimeSeries highsSerie = new TimeSeries("Highs");
        TimeSeries lowsSerie = new TimeSeries("Lows");

        for (int i = 0; i < AppState.getInstance().getInstants().size(); i++) {
            highsSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getHighs().get(i));
            lowsSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getLows().get(i));
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(highsSerie);
        dataset.addSeries(lowsSerie);

        return new StyledLineChart("Highs and Lows", "Time", "Value", dataset);
    }

    /**
     * Compiles raw market transaction volumes over time into a configured line chart.
     *
     * @return a structured line chart showing transactional volume density tracks
     */
    private JFreeChart createVolumesChart() {
        TimeSeries volumesSerie = new TimeSeries("Volumes");

        for (int i = 0; i < AppState.getInstance().getInstants().size(); i++)
            volumesSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getVolumes().get(i));

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(volumesSerie);

        return new StyledLineChart("Volumes", "Time", "Value", dataset);
    }
}