package app.gui.views;

import app.exception.*;
import app.gui.components.*;
import app.gui.style.AppColors;
import app.gui.style.PaddingConstants;
import app.model.AnomalyResult;
import app.model.TimeSeriesRow;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An abstract generic panel tasked with configuring parameters, triggering execution,
 * and visually representing anomaly detection processes.
 * <p>
 * It provides a standardized dashboard including time pickers, text inputs for contamination
 * thresholds and tree density, and a dropdown menu to determine the computational engine (Classical or Quantum).
 * Results are projected into bar charts indicating the global anomaly score and the specific weight of each feature.
 * </p>
 *
 * @param <T> the type of the time-series row under analysis, extending {@link TimeSeriesRow}
 */
public abstract class BaseAnomalyDetectionPanel<T extends TimeSeriesRow> extends InvisiblePanel {

    private DynamicChartContainer chartContainer;
    private JTextField thresholdTextField;
    private JTextField treesNumberTextField;
    private DateTimePicker startDatePicker;
    private JComboBox<String> implementationComboBox;
    private static final String[] implementations = {"Base implementation", "Quantum implementation"};
    private JButton searchButton;

    /**
     * Initializes the panel by configuring the flexible {@link GridBagLayout}, arranging
     * the top control widgets, and preparing the bottom area dedicated to the chart plots.
     */
    public BaseAnomalyDetectionPanel() {
        super(new GridBagLayout());
        addComponents();
        this.setVisible(true);
    }

    /**
     * Orchestrates the spatial insertion of the control module and the dynamic charts canvas.
     */
    private void addComponents() {
        addControlPanel();

        chartContainer = new DynamicChartContainer();
        add(chartContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));
    }

    /**
     * Generates and positions the input fields for the training start date, the contamination threshold,
     * the density of the isolation forest, and the type of analysis engine.
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

        searchButton = new PrimaryButton("SEARCH FOR ANOMALY", e -> executeSearch());
        componentsPanel.add(searchButton, new GridBagConstraints(0, 1, 4, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        add(componentsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    /**
     * Collects user inputs, delegates processing to the abstract search method catching any
     * validation exceptions, and updates the charts view upon successful completion.
     */
    private void executeSearch() {
        try {
            performSearch((String) implementationComboBox.getSelectedItem(),
                    startDatePicker.getDateTimePermissive(), thresholdTextField.getText(), treesNumberTextField.getText());
            updateChartsView();

        } catch (ValidationException | AnomalyDetectionException e) {
            LoggerUtil.logInfo("Anomaly detection blocked: " + e.getMessage());
            new FloatingMessage(e.getMessage(), searchButton, FloatingMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            LoggerUtil.logError("BaseAnomaliesDetectionPanel", "Unexpected error: " + e.getMessage());
            new FloatingMessage("An unexpected system error occurred.", searchButton, FloatingMessage.ERROR_MESSAGE);
        }
    }

    /**
     * Extracts the result vectors and sequentially builds the global anomaly score chart
     * (enriched by a horizontal threshold marker line) combined with the individual feature breakdown charts.
     */
    private void updateChartsView() {
        List<AnomalyResult<T>> results = getAnomalyResults();

        if (results == null || results.isEmpty())
            return;

        double threshold = Double.parseDouble(thresholdTextField.getText());
        List<JFreeChart> chartsToDisplay = new ArrayList<>();

        chartsToDisplay.add(createScoreChart(results, threshold));

        for (String feature : getFeatureNames()) {
            chartsToDisplay.add(createContributionChart(results, feature));
        }

        chartContainer.setCharts(chartsToDisplay);
    }

    /**
     * Generates a time-series bar chart illustrating the temporal trend of the anomaly score calculated for each individual record.
     *
     * @param results   the analysis results to map
     * @param threshold the limit value beyond which a record is considered anomalous
     * @return a configured {@link JFreeChart} instance provided with a visual alert marker
     */
    private JFreeChart createScoreChart(List<AnomalyResult<T>> results, double threshold) {
        TimeSeries scoreSerie = new TimeSeries("Anomaly Score");

        for (AnomalyResult<T> anomalyResult : results) {
            scoreSerie.add(new Millisecond(Date.from(anomalyResult.getDataRecord().getTimestamp())), anomalyResult.getScore());
        }

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
     * Generates a bar chart focusing on the percentage deviation impact exerted by a single specific coordinate or indicator within the series.
     *
     * @param results     the result set containing the contribution maps
     * @param featureName the textual identifier of the variable to isolate in the chart
     * @return a {@link JFreeChart} instance with the vertical axis constrained within the standard [0, 100] range
     */
    private JFreeChart createContributionChart(List<AnomalyResult<T>> results, String featureName) {
        TimeSeries contributionSerie = new TimeSeries(featureName + " Contribution");

        for (AnomalyResult<T> anomalyResult : results) {
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

    /**
     * Abstract method tasked with interfacing with the application's controller layer to actually start the anomaly detection calculations.
     *
     * @param implementation the descriptive string of the selected algorithmic model
     * @param startDate      the start date and time of the training subset
     * @param threshold      the string containing the sensitivity coefficient
     * @param treesNumber    the string indicating the density of the estimators (trees)
     * @throws Exception in case of invalid inputs or internal calculation module failures
     */
    protected abstract void performSearch(String implementation, LocalDateTime startDate, String threshold, String treesNumber) throws Exception;

    /**
     * Retrieves the processed data stored in the global application state.
     *
     * @return a {@link List} of {@link AnomalyResult} structures associated with the managed generic type
     */
    protected abstract List<AnomalyResult<T>> getAnomalyResults();

    /**
     * Returns the list of dimension names examined by the subclass, used to label the contribution charts.
     *
     * @return an array of strings representing the feature names
     */
    protected abstract String[] getFeatureNames();
}