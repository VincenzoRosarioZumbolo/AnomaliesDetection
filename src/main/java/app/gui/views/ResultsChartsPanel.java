package app.gui.views;

import app.gui.components.StyledLineChart;
import app.model.AppState;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.util.Arrays;
import java.util.Date;

/**
 * A panel container that handles standard historical asset value timelines.
 */
public class ResultsChartsPanel extends AbstractChartsPanel {

    /**
     * Constructs a ResultsChartsPanel, inheriting layout configurations
     * and the dynamic display grid.
     */
    public ResultsChartsPanel() {
        super();
        this.setVisible(true);
    }

    /**
     * Aggregates line plot assets from current application data configurations
     * and maps them onto the interactive display grid.
     */
    public void createCharts() {
        setCharts(Arrays.asList(
                createOpensClosuresChart(),
                createHighsLowsChart(),
                createVolumesChart()
        ));
    }

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

    private JFreeChart createVolumesChart() {
        TimeSeries volumesSerie = new TimeSeries("Volumes");

        for (int i = 0; i < AppState.getInstance().getDataRecordsInstants().size(); i++)
            volumesSerie.add(new Millisecond(Date.from(AppState.getInstance().getDataRecordsInstants().get(i))), AppState.getInstance().getVolumes().get(i));

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(volumesSerie);

        return new StyledLineChart("Volumes", "Time", "Value", dataset);
    }
}