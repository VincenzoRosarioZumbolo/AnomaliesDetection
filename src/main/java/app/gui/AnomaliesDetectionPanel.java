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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnomaliesDetectionPanel extends InvisiblePanel {

    private DynamicChartContainer chartContainer;

    private JTextField thresholdTextField;
    private DateTimePicker startDatePicker;
    private JComboBox<String> implementationComboBox;
    private static final String[] implementations = {"Base implementation", "Quantum implementation"};
    private JButton searchButton;

    public AnomaliesDetectionPanel() {

        super(new GridBagLayout());

        addComponents();
        this.setVisible(true);
    }

    private void addComponents() {

        addControlPanel();

        chartContainer = new DynamicChartContainer();
        add(chartContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));
    }

    private void addControlPanel() {

        JPanel componentsPanel = new InvisiblePanel(new GridBagLayout());

        startDatePicker = new UnderlinedDateTimePicker();
        componentsPanel.add(new LabeledComponent("Start training date:", startDatePicker), new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        thresholdTextField = new UnderlinedTextField("0.6");
        componentsPanel.add(new LabeledComponent("Anomaly threshold:", thresholdTextField), new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        implementationComboBox = new UnderlinedComboBox<>(implementations);
        componentsPanel.add(new LabeledComponent("Implementation:", implementationComboBox), new GridBagConstraints(2, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        searchButton = new PrimaryButton("SEARCH FOR ANOMALY", e -> searchForAnomaly());
        componentsPanel.add(searchButton, new GridBagConstraints(0, 1, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        add(componentsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    private void searchForAnomaly() {
        try {
            Controller.getInstance().searchForAnomaly((String) implementationComboBox.getSelectedItem(), startDatePicker.getDateTimePermissive(), thresholdTextField.getText());
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

        List<AnomalyResult> results = AppState.getInstance().getAnomalyResults();
        double threshold = Double.parseDouble(thresholdTextField.getText());

        List<JFreeChart> chartsToDisplay = new ArrayList<>();

        chartsToDisplay.add(createScoreChart(results, threshold));

        String[] features = {"Open", "Close", "High", "Low", "Volume"};
        for (String feature : features)
            chartsToDisplay.add(createContributionChart(results, feature));

        chartContainer.setCharts(chartsToDisplay);
    }

    private JFreeChart createScoreChart(List<AnomalyResult> results, double threshold) {

        TimeSeries scoreSerie = new TimeSeries("Anomaly Score");

        for (AnomalyResult anomalyResult : results)
            scoreSerie.add(new Millisecond(Date.from(anomalyResult.getDataRecord().getTimestamp())), anomalyResult.getScore());

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(scoreSerie);

        JFreeChart chart = new StyledBarChart("Anomaly Scores per Record", "Time", "Score", dataset);

        org.jfree.chart.plot.ValueMarker marker = new org.jfree.chart.plot.ValueMarker(threshold);
        marker.setPaint(AppColors.CHART_ANOMALY);
        marker.setStroke(new BasicStroke(2.0f));
        marker.setLabel("Threshold (" + threshold + ")");
        marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        chart.getXYPlot().addRangeMarker(marker);

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
        chart.getXYPlot().getRangeAxis().setRange(0.0, 100.0);

        return chart;
    }
}