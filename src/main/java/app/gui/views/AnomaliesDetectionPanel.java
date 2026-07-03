package app.gui.views;

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

/**
 * A GUI panel dedicated to configuring, triggering, and visualizing anomaly detection operations.
 * <p>
 * This panel provides user input controls for setting a training start date, anomaly threshold, number of trees
 * and selecting the underlying detection algorithm implementation (e.g., Base or Quantum).
 * Upon executing a search, it renders the resulting anomaly scores and individual feature contributions
 * using dynamic JFreeChart visualizations.
 * </p>
 * * @see InvisiblePanel
 * @see JFreeChart
 */
public class AnomaliesDetectionPanel extends InvisiblePanel {

    /** Container responsible for dynamically holding and rendering the JFreeChart instances. */
    private DynamicChartContainer chartContainer;

    /** Input field for entering the numerical threshold above which a record is flagged as an anomaly. */
    private JTextField thresholdTextField;

    /** Input field for entering the number of trees to build to find the anomalies. */
    private JTextField treesNumberTextField;

    /** Date and time picker to specify the start boundary for training data. */
    private DateTimePicker startDatePicker;

    /** ComboBox allowing selection between different anomaly detection implementations. */
    private JComboBox<String> implementationComboBox;

    /** Available algorithm implementations for anomaly detection. */
    private static final String[] implementations = {"Base implementation", "Quantum implementation"};

    /** Button that triggers the anomaly detection routine. */
    private JButton searchButton;

    /**
     * Constructs a new {@code AnomaliesDetectionPanel} with a GridBagLayout.
     * Initializes the user interface components and sets the panel visibility to true.
     */
    public AnomaliesDetectionPanel() {
        super(new GridBagLayout());
        addComponents();
        this.setVisible(true);
    }

    /**
     * Initializes and arranges the main structural components of the panel,
     * dividing it into a control configuration section and a chart visualization section.
     */
    private void addComponents() {
        addControlPanel();

        chartContainer = new DynamicChartContainer();
        add(chartContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));
    }

    /**
     * Constructs the control panel containing the configuration inputs
     * (date picker, threshold field, treesNumber field, implementation selector) and the execution button.
     */
    private void addControlPanel() {
        JPanel componentsPanel = new InvisiblePanel(new GridBagLayout());

        startDatePicker = new UnderlinedDateTimePicker();
        componentsPanel.add(new LabeledComponent("Start training date:", startDatePicker), new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        thresholdTextField = new UnderlinedTextField("0.6");
        componentsPanel.add(new LabeledComponent("Anomaly threshold:", thresholdTextField), new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        treesNumberTextField = new UnderlinedTextField("500");
        componentsPanel.add(new LabeledComponent("Number of trees:", treesNumberTextField), new GridBagConstraints(2, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        implementationComboBox = new UnderlinedComboBox<>(implementations);
        componentsPanel.add(new LabeledComponent("Implementation:", implementationComboBox), new GridBagConstraints(3, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        searchButton = new PrimaryButton("SEARCH FOR ANOMALY", e -> searchForAnomaly());
        componentsPanel.add(searchButton, new GridBagConstraints(0, 1, 4, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        add(componentsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    /**
     * Gathers user inputs from the UI, delegates the anomaly detection processing to
     * the system {@link Controller}, and triggers a view refresh upon success.
     * <p>
     * Catches expected business/validation exceptions to display user-friendly contextual
     * notifications, and catches generic exceptions to prevent application crashes.
     * </p>
     */
    private void searchForAnomaly() {
        try {
            Controller.getInstance().searchForAnomaly((String) implementationComboBox.getSelectedItem(),
                    startDatePicker.getDateTimePermissive(), thresholdTextField.getText(), treesNumberTextField.getText());
            updateChartsView();

        } catch (ValidationException | AnomalyDetectionException e) {
            LoggerUtil.logInfo("Anomaly detection blocked: " + e.getMessage());
            new FloatingMessage(e.getMessage(), searchButton, FloatingMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            LoggerUtil.logError("AnomalyPanel", "Unexpected error: " + e.getMessage());
            new FloatingMessage("An unexpected system error occurred.", searchButton, FloatingMessage.ERROR_MESSAGE);
        }
    }

    /**
     * Fetches the latest processing results from the {@link AppState}, generates
     * both the overall score chart and individual feature contribution charts,
     * and pushes them to the active chart container component.
     */
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

    /**
     * Creates a bar chart representing the anomaly scores over time.
     * Includes a visual range marker reflecting the designated anomaly threshold.
     *
     * @param results   the list of {@link AnomalyResult} items to plot
     * @param threshold the numerical value marking the boundary of anomalous behavior
     * @return a configured {@link JFreeChart} displaying anomaly scores
     */
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

    /**
     * Creates a chart representing the relative deviation impact percentage of a specific
     * data feature (e.g., Open, Close, Volume) over time.
     *
     * @param results     the list of {@link AnomalyResult} items containing metrics
     * @param featureName the name of the specific feature to chart
     * @return a configured {@link JFreeChart} displaying feature contribution trends
     */
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