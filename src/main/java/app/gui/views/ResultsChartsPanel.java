package app.gui.views;

import app.gui.components.StyledLineChart;
import app.dto.AppState;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.Arrays;
import java.util.Date;

/**
 * A container panel specialized in loading and displaying historical linear time series
 * of financial asset prices (Opens, Closures, Highs, Lows, Volumes).
 */
public class ResultsChartsPanel extends AbstractChartsPanel {

    /**
     * Constructs the panel by inheriting the structural and display behaviors defined in {@link AbstractChartsPanel}.
     */
    public ResultsChartsPanel() {
        super();
        this.setVisible(true);
    }

    /**
     * Extracts historical market data from the application state, builds the respective
     * stylized line charts, and injects them into the graphical container for visual rendering.
     */
    public void createCharts() {
        setCharts(Arrays.asList(
                createOpensClosuresChart(),
                createHighsLowsChart(),
                createVolumesChart()
        ));
    }

    /**
     * Creates a composite time-series chart mapping the historical open and close prices.
     *
     * @return a configured {@link JFreeChart} instance containing the Opens and Closures datasets
     */
    private JFreeChart createOpensClosuresChart() {
        TimeSeries opensSerie = new TimeSeries("Opens");
        TimeSeries closuresSerie = new TimeSeries("Closures");

        for (int i = 0; i < AppState.getInstance().getDataRecordsInstants().size(); i++) {
            opensSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getOpens().get(i));
            closuresSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getClosures().get(i));
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(opensSerie);
        dataset.addSeries(closuresSerie);

        return new StyledLineChart("Opens and Closures", "Time", "Value", dataset);
    }

    /**
     * Creates a composite time-series chart mapping the historical high and low peak prices.
     *
     * @return a configured {@link JFreeChart} instance containing the Highs and Lows datasets
     */
    private JFreeChart createHighsLowsChart() {
        TimeSeries highsSerie = new TimeSeries("Highs");
        TimeSeries lowsSerie = new TimeSeries("Lows");

        for (int i = 0; i < AppState.getInstance().getDataRecordsInstants().size(); i++) {
            highsSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getHighs().get(i));
            lowsSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getLows().get(i));
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(highsSerie);
        dataset.addSeries(lowsSerie);

        return new StyledLineChart("Highs and Lows", "Time", "Value", dataset);
    }

    /**
     * Creates a time-series chart mapping the historical traded asset volumes.
     *
     * @return a configured {@link JFreeChart} instance containing the Volumes dataset
     */
    private JFreeChart createVolumesChart() {
        TimeSeries volumesSerie = new TimeSeries("Volumes");

        for (int i = 0; i < AppState.getInstance().getDataRecordsInstants().size(); i++)
            volumesSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getVolumes().get(i));

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(volumesSerie);

        return new StyledLineChart("Volumes", "Time", "Value", dataset);
    }
}