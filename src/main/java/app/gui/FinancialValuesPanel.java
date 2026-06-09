package app.gui;

import app.controller.Controller;
import app.gui.components.*;
import app.gui.style.PaddingConstants;
import app.model.FinancialIndicatorsPeriods;

import javax.swing.*;
import java.awt.*;
import java.security.InvalidParameterException;

/**
 * A specialized user configuration matrix interface dedicated to mathematical trend tracking intervals.
 * <p>
 * Collects custom epoch periods for financial calculations, including Relative Strength Index (RSI),
 * Moving Average Convergence Divergence (MACD), Average True Range (ATR), and Chaikin Money Flow (CMF).
 * </p>
 *
 * @see InvisiblePanel
 * @see Controller
 * @see VariablesResultsDialog
 */
public class FinancialValuesPanel extends InvisiblePanel {

    /** Input tracking period string definitions matching RSI evaluation steps. */
    private JTextField RSIPeriodTextField;

    /** Input tracking period string definitions matching the Fast Exponential Moving Average window. */
    private JTextField fastEMAPeriodTextField;

    /** Input tracking period string definitions matching the Slow Exponential Moving Average window. */
    private JTextField slowEMAPeriodTextField;

    /** Input tracking period string definitions matching the MACD Signal Line evaluation phase. */
    private JTextField signalLinePeriodTextField;

    /** Input tracking period string definitions matching the Average True Range validation window. */
    private JTextField ATRPeriodTextField;

    /** Input tracking period string definitions matching Chaikin Money Flow epoch blocks. */
    private JTextField CMFPeriodTextField;

    /** The operational processing trigger launching metric calculation algorithms. */
    private JButton calculateButton;

    /**
     * Constructs a FinancialValuesPanel, initializing form sections for indicators
     * and setting layout constraints.
     */
    public FinancialValuesPanel() {
        super(new GridBagLayout());

        addRSIComponents();
        addMACDComponents();
        addATRComponents();
        addCMFComponents();
        addCalculateButton();

        this.setVisible(true);
    }

    /**
     * Initializes structural layout widgets related to Relative Strength Index periods.
     */
    private void addRSIComponents() {
        this.add(new TitleLabel("RELATIVE STRENGTH INDEX", "h2"), new GridBagConstraints(0, 0, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        RSIPeriodTextField = new UnderlinedTextField("14");
        this.add(new LabeledComponent("RSI period:", RSIPeriodTextField), new GridBagConstraints(0, 1, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes structural layout widgets related to Moving Average Convergence Divergence configurations.
     */
    private void addMACDComponents() {
        this.add(new TitleLabel("MOVING AVERAGE CONVERGENCE DIVERGENCE", "h2"), new GridBagConstraints(0, 2, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        fastEMAPeriodTextField = new UnderlinedTextField("12");
        this.add(new LabeledComponent("Fast EMA period:", fastEMAPeriodTextField), new GridBagConstraints(0, 3, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        slowEMAPeriodTextField = new UnderlinedTextField("26");
        this.add(new LabeledComponent("Slow EMA period:", slowEMAPeriodTextField), new GridBagConstraints(0, 4, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        signalLinePeriodTextField = new UnderlinedTextField("9");
        this.add(new LabeledComponent("Signal line period:", signalLinePeriodTextField), new GridBagConstraints(0, 5, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes structural layout widgets related to Average True Range metrics.
     */
    private void addATRComponents() {
        this.add(new TitleLabel("AVERAGE TRUE RANGE", "h2"), new GridBagConstraints(0, 6, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        ATRPeriodTextField = new UnderlinedTextField("14");
        this.add(new LabeledComponent("ATR period:", ATRPeriodTextField), new GridBagConstraints(0, 7, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes structural layout widgets related to Chaikin Money Flow strategies.
     */
    private void addCMFComponents() {
        CMFPeriodTextField = new UnderlinedTextField("20");
        this.add(new LabeledComponent("CMF period:", CMFPeriodTextField), new GridBagConstraints(0, 7, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));
    }

    /**
     * Binds action routines to the execution button used to process inputs.
     */
    private void addCalculateButton() {
        calculateButton = new PrimaryButton("CALCULATE", e -> calculate());

        this.add(calculateButton, new GridBagConstraints(0, 8, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    /**
     * Converts form strings into structured data objects, initiates processing routines
     * via the Controller, and opens a tracking display dialog.
     * <p>
     * Displays a contextual error pop-up if values violate field criteria.
     * </p>
     */
    private void calculate() {
        try {
            FinancialIndicatorsPeriods periods = new FinancialIndicatorsPeriods(RSIPeriodTextField.getText(),
                    new String[] {fastEMAPeriodTextField.getText(), slowEMAPeriodTextField.getText(), signalLinePeriodTextField.getText()},
                    ATRPeriodTextField.getText(), CMFPeriodTextField.getText());

            Controller.getInstance().calculateRSInMACDnATRnCMF(periods);

            new VariablesResultsDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        } catch (InvalidParameterException e) {
            new FloatingMessage(e.getMessage(), calculateButton, FloatingMessage.ERROR_MESSAGE);
        }
    }
}