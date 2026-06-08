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

public class ResultsChartsPanel extends InvisiblePanel {

    private final DynamicChartContainer chartContainer;

    public ResultsChartsPanel() {

        super(new BorderLayout());

        this.chartContainer = new DynamicChartContainer();
        this.add(chartContainer, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void createCharts() {

        chartContainer.setCharts(Arrays.asList(
                createOpensClosuresChart(),
                createHighsLowsChart(),
                createVolumesChart()
        ));
    }

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

    private JFreeChart createVolumesChart() {
        TimeSeries volumesSerie = new TimeSeries("Volumes");

        for (int i = 0; i < AppState.getInstance().getInstants().size(); i++)
            volumesSerie.add(new Millisecond(Date.from(AppState.getInstance().getInstants().get(i))), AppState.getInstance().getVolumes().get(i));

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(volumesSerie);

        return new StyledLineChart("Volumes Chart", "Time", "Volumes", dataset);
    }
}