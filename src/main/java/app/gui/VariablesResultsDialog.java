package app.gui;

import app.controller.Controller;
import app.model.AppState;

import javax.swing.*;
import java.awt.*;

public class VariablesResultsDialog extends JDialog {

    public VariablesResultsDialog(JFrame parent) {

        super(parent, "Variables Results", true);

        this.setLayout(new FlowLayout());
        this.setSize(600, 200);
        this.setLocationRelativeTo(parent);
        this.setAlwaysOnTop(false);

        addResultsLabels();

        this.setVisible(true);
    }

    private void addResultsLabels() {

        add(new JLabel("RSI: " + AppState.getInstance().getFinancialIndicators().getRSI()));
        add(new JLabel("MACD: " + AppState.getInstance().getFinancialIndicators().getMACD()));
        add(new JLabel("ATR: " + AppState.getInstance().getFinancialIndicators().getATR()));
        add(new JLabel("CMF: " + AppState.getInstance().getFinancialIndicators().getCMF()));
    }
}
