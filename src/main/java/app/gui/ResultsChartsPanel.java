package app.gui;

import app.gui.components.InvisiblePanel;
import app.gui.components.PrimaryButton;
import app.gui.components.StyledChartPanel;
import app.gui.components.StyledLineChart;
import app.gui.style.PaddingConstants;
import app.model.AppState;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

public class ResultsChartsPanel extends InvisiblePanel {

    private enum ViewMode { VERTICAL, HORIZONTAL, SINGLE_FOCUS }

    private JPanel chartsPanel;
    private ViewMode currentMode = ViewMode.HORIZONTAL;
    private int focusedChartIndex = -1;

    public ResultsChartsPanel() {

        super();

        this.setVisible(true);
    }

    public void createCharts() {
        
        this.removeAll();

        if (currentMode != ViewMode.SINGLE_FOCUS)
            addControlPanel();

        chartsPanel = new InvisiblePanel();
        this.add(chartsPanel, new GridBagConstraints(0, (currentMode == ViewMode.SINGLE_FOCUS) ? 0 : 1, 1, 1,
                1.0, (currentMode ==  ViewMode.VERTICAL) ? 0.3 : 0.9,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));

        if (currentMode.equals(ViewMode.SINGLE_FOCUS))
            addSingleFocusedChart();
        else
            addAllCharts();

        this.revalidate();
        this.repaint();
    }

    private void addControlPanel() {
        
        JPanel controlPanel = new InvisiblePanel(new BorderLayout());
        controlPanel.setOpaque(false);

        JButton changeViewButton = new PrimaryButton(currentMode.equals(ViewMode.VERTICAL) ? "Horizontal view" : "Vertical view");
        
        changeViewButton.addActionListener(e -> {
            currentMode = (currentMode.equals(ViewMode.VERTICAL)) ? ViewMode.HORIZONTAL : ViewMode.VERTICAL;
            focusedChartIndex = -1;
            createCharts();
        });

        controlPanel.add(changeViewButton, BorderLayout.CENTER);

        this.add(controlPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addAllCharts() {

        int gridX = 0;
        int gridY = 0;

        addChartToGrid(createOpensClosuresChart(), 0, gridX, gridY);
        
        if (currentMode == ViewMode.VERTICAL) gridY++; else gridX++;

        addChartToGrid(createHighsLowsChart(), 1, gridX, gridY);

        if (currentMode == ViewMode.VERTICAL) gridY++; else gridX++;

        addChartToGrid(createVolumesChart(), 2, gridX, gridY);
    }

    private void addSingleFocusedChart() {

        JFreeChart chart;

        switch (focusedChartIndex) {
            case 0 -> chart = createOpensClosuresChart();
            case 1 -> chart = createHighsLowsChart();
            default -> chart = createVolumesChart();
        }

        addChartToGrid(chart, focusedChartIndex, 0, 0);
    }

    private void addChartToGrid(JFreeChart chart, int index, int x, int y) {

        StyledChartPanel chartPanel = getStyledChartPanel(chart, index);

        chartsPanel.add(chartPanel, new GridBagConstraints(x, y, 1, 1, 1,1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private StyledChartPanel getStyledChartPanel(JFreeChart chart, int index) {

        StyledChartPanel chartPanel = new StyledChartPanel(chart);

        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentMode == ViewMode.SINGLE_FOCUS) {
                    currentMode = ViewMode.HORIZONTAL;
                    focusedChartIndex = -1;
                } else {
                    currentMode = ViewMode.SINGLE_FOCUS;
                    focusedChartIndex = index;
                }
                createCharts();
            }
        });

        return chartPanel;
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