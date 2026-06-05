package app.gui;

import app.controller.Controller;
import app.gui.components.*;
import app.gui.style.PaddingConstants;
import app.model.FinancialIndicatorsPeriods;

import javax.swing.*;
import java.awt.*;
import java.security.InvalidParameterException;

public class FinancialValuesPanel extends InvisiblePanel {

    private JTextField RSIPeriodTextField;
    private JTextField fastEMAPeriodTextField;
    private JTextField slowEMAPeriodTextField;
    private JTextField signalLinePeriodTextField;
    private JTextField ATRPeriodTextField;
    private JTextField CMFPeriodTextField;
    private JButton calculateButton;

    public FinancialValuesPanel() {

        addRSIComponents();
        addMACDComponents();
        addATRComponents();
        addCMFComponents();
        addCalculateButton();

        this.setVisible(true);
    }

    private void addRSIComponents() {

        this.add(new TitleLabel("RELATIVE STRENGTH INDEX", "h2"), new GridBagConstraints(0, 0, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_TOP, 0, 0));

        RSIPeriodTextField = new UnderlinedTextField("14");

        this.add(new LabeledComponent("RSI period:", RSIPeriodTextField), new GridBagConstraints(0, 1, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));
    }

    private void addMACDComponents() {

        this.add(new TitleLabel("MOVING AVERAGE DIVERGENCE CONVERGENCE", "h2"), new GridBagConstraints(0, 2, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        fastEMAPeriodTextField = new UnderlinedTextField("12");
        this.add(new LabeledComponent("Fast EMA period:", fastEMAPeriodTextField), new GridBagConstraints(0, 3, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));

        slowEMAPeriodTextField = new UnderlinedTextField("26");
        this.add(new LabeledComponent("Slow EMA period:", slowEMAPeriodTextField), new GridBagConstraints(1, 3, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));

        signalLinePeriodTextField = new UnderlinedTextField("9");
        this.add(new LabeledComponent("Signal Line period:", signalLinePeriodTextField), new GridBagConstraints(2, 3, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));
    }

    private void addATRComponents() {

        this.add(new TitleLabel("AVERAGE TRUE RANGE", "h2"), new GridBagConstraints(0, 4, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        ATRPeriodTextField = new UnderlinedTextField("14");
        this.add(new LabeledComponent("ATR period:", ATRPeriodTextField), new GridBagConstraints(0, 5, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));
    }

    private void addCMFComponents() {

        this.add(new TitleLabel("CHAIKIN MONEY FLOW", "h2"), new GridBagConstraints(0, 6, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));

        CMFPeriodTextField = new UnderlinedTextField("20");
        this.add(new LabeledComponent("CMF period:", CMFPeriodTextField), new GridBagConstraints(0, 7, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_BOTTOM, 0, 0));
    }

    private void addCalculateButton() {

        calculateButton = new PrimaryButton("CALCULATE");

        calculateButton.addActionListener(e -> calculate());

        this.add(calculateButton, new GridBagConstraints(0, 8, 3, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    private void calculate() {

        try {
            FinancialIndicatorsPeriods periods = new FinancialIndicatorsPeriods(RSIPeriodTextField.getText(),
                    new String[] {fastEMAPeriodTextField.getText(), slowEMAPeriodTextField.getText(), signalLinePeriodTextField.getText()},
                    ATRPeriodTextField.getText(), CMFPeriodTextField.getText());

            Controller.getInstance().calculateRSInMACDnATRnCMF(periods);

            new VariablesResultsDialog((JFrame)SwingUtilities.getWindowAncestor(this));
        } catch (InvalidParameterException e) {
            new FloatingMessage(e.getMessage(), calculateButton, FloatingMessage.ERROR_MESSAGE);
        }
    }
}
