package app.gui.views;

import app.controller.Controller;
import app.gui.components.*;
import app.gui.style.PaddingConstants;
import app.model.AppState;
import app.model.FinancialIndicatorsPeriods;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Date;

/**
 * An interactive graphical panel tasked with configuring time periods and visualizing
 * the trends of main technical analysis indicators (RSI, MACD, ATR, CMF).
 * <p>
 * It includes an input area in the upper section for calculating moving averages and flow channels,
 * and a dynamic container in the lower section for displaying the related linear charts.
 * </p>
 */
public class FinancialValuesPanel extends AbstractChartsPanel {

    private JTextField RSIPeriodTextField;
    private JTextField fastEMAPeriodTextField;
    private JTextField slowEMAPeriodTextField;
    private JTextField signalLinePeriodTextField;
    private JTextField ATRPeriodTextField;
    private JTextField CMFPeriodTextField;
    private JButton calculateButton;

    /**
     * Constructs the panel by arranging the top control bar containing fields for financial
     * indicators and hooking the event listener to the calculate button.
     */
    public FinancialValuesPanel() {
        super();

        InvisiblePanel inputsPanel = new InvisiblePanel(new GridBagLayout());

        addRSIComponents(inputsPanel);
        addMACDComponents(inputsPanel);
        addATRComponents(inputsPanel);
        addCMFComponents(inputsPanel);
        addCalculateButton(inputsPanel);

        this.add(inputsPanel, BorderLayout.NORTH);
        this.setVisible(true);
    }

    /**
     * Appends the Relative Strength Index (RSI) specific title and input field to the control panel.
     *
     * @param panel the target container panel
     */
    private void addRSIComponents(JPanel panel) {
        panel.add(new TitleLabel("RELATIVE STRENGTH INDEX", "h2"), new GridBagConstraints(0, 0, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        RSIPeriodTextField = new UnderlinedTextField("14");
        panel.add(new LabeledComponent("RSI period:", RSIPeriodTextField), new GridBagConstraints(0, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Appends the Moving Average Convergence Divergence (MACD) specific titles and input fields to the control panel.
     *
     * @param panel the target container panel
     */
    private void addMACDComponents(JPanel panel) {
        panel.add(new TitleLabel("MOVING AVERAGE CONVERGENCE DIVERGENCE", "h2"), new GridBagConstraints(1, 0, 3, 1, 0.5, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        fastEMAPeriodTextField = new UnderlinedTextField("12");
        panel.add(new LabeledComponent("Fast EMA period:", fastEMAPeriodTextField), new GridBagConstraints(1, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        slowEMAPeriodTextField = new UnderlinedTextField("26");
        panel.add(new LabeledComponent("Slow EMA period:", slowEMAPeriodTextField), new GridBagConstraints(2, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        signalLinePeriodTextField = new UnderlinedTextField("9");
        panel.add(new LabeledComponent("Signal line period:", signalLinePeriodTextField), new GridBagConstraints(3, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Appends the Average True Range (ATR) specific title and input field to the control panel.
     *
     * @param panel the target container panel
     */
    private void addATRComponents(JPanel panel) {
        panel.add(new TitleLabel("AVERAGE TRUE RANGE", "h2"), new GridBagConstraints(4, 0, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        ATRPeriodTextField = new UnderlinedTextField("14");
        panel.add(new LabeledComponent("ATR period:", ATRPeriodTextField), new GridBagConstraints(4, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Appends the Chaikin Money Flow (CMF) specific title and input field to the control panel.
     *
     * @param panel the target container panel
     */
    private void addCMFComponents(JPanel panel) {
        panel.add(new TitleLabel("CHAIKIN MONEY FLOW", "h2"), new GridBagConstraints(5, 0, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        CMFPeriodTextField = new UnderlinedTextField("20");
        panel.add(new LabeledComponent("CMF period:", CMFPeriodTextField), new GridBagConstraints(5, 1, 1, 1, 0.16, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Appends the primary submission button used to initiate the indicator calculations.
     *
     * @param panel the target container panel
     */
    private void addCalculateButton(JPanel panel) {
        calculateButton = new PrimaryButton("CALCULATE", e -> calculate());
        panel.add(calculateButton, new GridBagConstraints(0, 2, 6, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    /**
     * Validates the numeric strings entered, generates the configuration object
     * {@link FinancialIndicatorsPeriods}, and triggers the calculation of the oscillators on the controller.
     * In case of errors in numeric formats, it displays a contextual floating message.
     */
    private void calculate() {
        try {
            FinancialIndicatorsPeriods periods = new FinancialIndicatorsPeriods(RSIPeriodTextField.getText(),
                    new String[] {fastEMAPeriodTextField.getText(), slowEMAPeriodTextField.getText(), signalLinePeriodTextField.getText()},
                    ATRPeriodTextField.getText(), CMFPeriodTextField.getText());

            Controller.getInstance().calculateRSInMACDnATRnCMF(periods);
            createCharts();

        } catch (InvalidParameterException e) {
            new FloatingMessage(e.getMessage(), calculateButton, FloatingMessage.ERROR_MESSAGE);
        }
    }

    /**
     * Generates and merges the four independent analytical charts into an ordered list ready for display.
     */
    private void createCharts() {
        setCharts(Arrays.asList(
                createRSIChart(),
                createMACDChart(),
                createATRChart(),
                createCMFChart()
        ));
    }

    /**
     * Creates the line chart representation for the Relative Strength Index (RSI).
     *
     * @return a configured {@link JFreeChart} instance for the RSI dataset
     */
    private JFreeChart createRSIChart() {
        TimeSeries rsiSerie = new TimeSeries("RSI");
        for (int i = 0; i < AppState.getInstance().getFinancialIndicatorsInstants().size(); i++) {
            rsiSerie.add(new Millisecond(Date.from(AppState.getInstance().getFinancialIndicatorsInstants().get(i))),
                    AppState.getInstance().getRSIs().get(i));
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(rsiSerie);
        return new StyledLineChart("Relative Strength Index (RSI)", "Time", "Value", dataset);
    }

    /**
     * Creates the line chart representation for the Moving Average Convergence Divergence (MACD).
     *
     * @return a configured {@link JFreeChart} instance for the MACD dataset
     */
    private JFreeChart createMACDChart() {
        TimeSeries macdSerie = new TimeSeries("MACD");
        for (int i = 0; i < AppState.getInstance().getFinancialIndicatorsInstants().size(); i++) {
            macdSerie.add(new Millisecond(Date.from(AppState.getInstance().getFinancialIndicatorsInstants().get(i))),
                    AppState.getInstance().getMACDs().get(i));
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(macdSerie);
        return new StyledLineChart("Moving Average Convergence Divergence (MACD)", "Time", "Value", dataset);
    }

    /**
     * Creates the line chart representation for the Average True Range (ATR).
     *
     * @return a configured {@link JFreeChart} instance for the ATR dataset
     */
    private JFreeChart createATRChart() {
        TimeSeries atrSerie = new TimeSeries("ATR");
        for (int i = 0; i < AppState.getInstance().getFinancialIndicatorsInstants().size(); i++) {
            atrSerie.add(new Millisecond(Date.from(AppState.getInstance().getFinancialIndicatorsInstants().get(i))),
                    AppState.getInstance().getATRs().get(i));
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(atrSerie);
        return new StyledLineChart("Average True Range (ATR)", "Time", "Value", dataset);
    }

    /**
     * Creates the line chart representation for the Chaikin Money Flow (CMF).
     *
     * @return a configured {@link JFreeChart} instance for the CMF dataset
     */
    private JFreeChart createCMFChart() {
        TimeSeries cmfSerie = new TimeSeries("CMF");
        for (int i = 0; i < AppState.getInstance().getFinancialIndicatorsInstants().size(); i++) {
            cmfSerie.add(new Millisecond(Date.from(AppState.getInstance().getFinancialIndicatorsInstants().get(i))),
                    AppState.getInstance().getCMFs().get(i));
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(cmfSerie);
        return new StyledLineChart("Chaikin Money Flow (CMF)", "Time", "Value", dataset);
    }
}