package app.gui;

import app.controller.Controller;
import app.exception.*;
import app.gui.components.*;
import app.gui.style.AppColors;
import app.gui.style.PaddingConstants;
import app.model.AnomalyResult;
import app.model.AppState;
import app.util.LoggerUtil;
import com.github.lgooddatepicker.components.DateTimePicker;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

public class AnomaliesDetectionPanel extends InvisiblePanel {

    private enum ViewMode { VERTICAL, HORIZONTAL, SINGLE_FOCUS }
    private ViewMode currentMode = ViewMode.VERTICAL;
    private int focusedChartIndex = -1;
    private List<AnomalyResult> lastResults;
    private String lastThreshold;

    private JPanel displayContainer;
    private JPanel chartsPanel;

    private JTextField thresholdTextField;
    private DateTimePicker startDatePicker;
    private JComboBox<String> implementationComboBox;
    private static final String[] implementations = {"Base implementation", "Quantum implementation"};
    private JButton searchButton;

    public AnomaliesDetectionPanel() {
        addComponents();
        this.setVisible(true);
    }

    private void addComponents() {

        searchButton = new PrimaryButton("SEARCH FOR ANOMALY");
        thresholdTextField = new UnderlinedTextField("0.6");
        startDatePicker = new UnderlinedDateTimePicker();
        implementationComboBox = new UnderlinedComboBox<>(implementations);

        searchButton.addActionListener(e -> searchForAnomaly());

        JPanel componentsPanel = new InvisiblePanel(new GridBagLayout());
        componentsPanel.add(new LabeledComponent("Start training date:", startDatePicker), new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
        componentsPanel.add(new LabeledComponent("Anomaly threshold:", thresholdTextField), new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
        componentsPanel.add(new LabeledComponent("Implementation:", implementationComboBox), new GridBagConstraints(2, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
        componentsPanel.add(searchButton, new GridBagConstraints(0, 1, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        add(componentsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_LARGE, 0, 0));

        displayContainer = new InvisiblePanel(new GridBagLayout());
        add(displayContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));
    }

    private void searchForAnomaly() {

        try {
            Controller.getInstance().searchForAnomaly((String) implementationComboBox.getSelectedItem(), startDatePicker.getDateTimePermissive(), thresholdTextField.getText());

            this.lastResults = AppState.getInstance().getAnomalyResults();
            this.lastThreshold = thresholdTextField.getText();

            updateChartsView();

        } catch (ValidationException | AnomalyDetectionException e) {
            LoggerUtil.logInfo("Anomaly detection blocked: " + e.getMessage());
            new FloatingMessage(e.getMessage(), searchButton, FloatingMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            LoggerUtil.logError("AnomalyPanel", "Unexpected error: " + e.getMessage());
            new FloatingMessage("An unexpected system error occurred.", searchButton, FloatingMessage.ERROR_MESSAGE);
        }
    }

    private void updateChartsView() {

        displayContainer.removeAll();

        if (currentMode != ViewMode.SINGLE_FOCUS)
            addControlPanel();

        chartsPanel = new InvisiblePanel(new GridBagLayout());
        chartsPanel.setBackground(Color.white);

        displayContainer.add(chartsPanel, new GridBagConstraints(0, (currentMode == ViewMode.SINGLE_FOCUS) ? 0 : 1, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));

        if (currentMode == ViewMode.SINGLE_FOCUS)
            addSingleFocusedChart();
        else
            addAllCharts();

        displayContainer.revalidate();
        displayContainer.repaint();
    }

    private void addControlPanel() {

        JPanel controlPanel = new InvisiblePanel(new BorderLayout());
        JButton changeViewButton = new PrimaryButton(currentMode == ViewMode.VERTICAL ? "Horizontal view" : "Vertical view");

        changeViewButton.addActionListener(e -> {
            currentMode = (currentMode == ViewMode.VERTICAL) ? ViewMode.HORIZONTAL : ViewMode.VERTICAL;
            focusedChartIndex = -1;
            updateChartsView();
        });

        controlPanel.add(changeViewButton, BorderLayout.CENTER);
        displayContainer.add(controlPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addAllCharts() {

        int gridX = 0;
        int gridY = 0;

        addChartToGrid(createScoreChart(lastResults, Double.parseDouble(lastThreshold)), 0, gridX, gridY);

        if (currentMode == ViewMode.VERTICAL) gridY++; else gridX++;

        String[] features = {"Open", "Close", "High", "Low", "Volume"};
        for (int i = 0; i < features.length; i++) {
            addChartToGrid(createContributionChart(lastResults, features[i]), i + 1, gridX, gridY);
            if (currentMode == ViewMode.VERTICAL) gridY++; else gridX++;
        }
    }

    private void addSingleFocusedChart() {

        JFreeChart chart;

        if (focusedChartIndex == 0) {
            chart = createScoreChart(lastResults, Double.parseDouble(lastThreshold));
        } else {
            String[] features = {"Open", "Close", "High", "Low", "Volume"};
            chart = createContributionChart(lastResults, features[focusedChartIndex - 1]);
        }

        addChartToGrid(chart, focusedChartIndex, 0, 0);
    }

    private void addChartToGrid(JFreeChart chart, int index, int x, int y) {

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

                updateChartsView();
            }
        });

        chartsPanel.add(chartPanel, new GridBagConstraints(x, y, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private JFreeChart createScoreChart(List<AnomalyResult> results, double threshold) {

        TimeSeries scoreSerie = new TimeSeries("Anomaly Score");

        for (AnomalyResult anomalyResult : results)
            scoreSerie.add(new Millisecond(Date.from(anomalyResult.getDataRecord().getTimestamp())), anomalyResult.getScore());

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(scoreSerie);

        JFreeChart chart = new StyledBarChart("Anomaly Scores per Record", "Time", "Score", dataset);
        org.jfree.chart.plot.XYPlot plot = chart.getXYPlot();
        org.jfree.chart.renderer.xy.XYBarRenderer renderer = (org.jfree.chart.renderer.xy.XYBarRenderer) plot.getRenderer();

        renderer.setBarPainter(new org.jfree.chart.renderer.xy.StandardXYBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, AppColors.CHART_SCORE);

        org.jfree.chart.plot.ValueMarker marker = new org.jfree.chart.plot.ValueMarker(threshold);
        marker.setPaint(AppColors.CHART_ANOMALY);
        marker.setStroke(new BasicStroke(2.0f));
        marker.setLabel("Threshold (" + threshold + ")");
        marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addRangeMarker(marker);

        return chart;
    }

    private JFreeChart createContributionChart(List<AnomalyResult> results, String featureName) {

        TimeSeries contributionSerie = new TimeSeries(featureName + " Contribution");

        for (AnomalyResult anomalyResult : results) {
            Double contribution = anomalyResult.getContributions().get(featureName);
            contributionSerie.add(new Millisecond(Date.from(anomalyResult.getDataRecord().getTimestamp())),
                    contribution != null ? contribution : 0.0);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(contributionSerie);

        JFreeChart chart = new StyledBarChart(featureName + " Contribution(%)", "Time", "Deviation Weight", dataset);
        org.jfree.chart.renderer.xy.XYBarRenderer renderer = (org.jfree.chart.renderer.xy.XYBarRenderer) chart.getXYPlot().getRenderer();
        renderer.setBarPainter(new org.jfree.chart.renderer.xy.StandardXYBarPainter());
        renderer.setShadowVisible(false);
        renderer.setSeriesPaint(0, AppColors.CHART_SCORE);
        chart.getXYPlot().getRangeAxis().setRange(0.0, 100.0);

        return chart;
    }
}