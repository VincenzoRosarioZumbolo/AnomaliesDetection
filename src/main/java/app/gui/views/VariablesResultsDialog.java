package app.gui.views;

import app.model.AppState;

import javax.swing.*;
import java.awt.*;

/**
 * A modal tracking dialogue display pop-up displaying financial metric results.
 * <p>
 * Pulls current statistical indicators from the system state engine registry, presenting
 * summarized scores for RSI, MACD, ATR, and CMF models in a clean overview.
 * </p>
 *
 * @see JDialog
 * @see AppState
 */
public class VariablesResultsDialog extends JDialog {

    /**
     * Constructs a modal tracking window frame focused over the calling application container.
     *
     * @param parent the structural ancestor window context framing anchoring location guidelines
     */
    public VariablesResultsDialog(JFrame parent) {
        super(parent, "Variables Results", true);

        this.setLayout(new FlowLayout());
        this.setSize(600, 200);
        this.setLocationRelativeTo(parent);
        this.setAlwaysOnTop(false);

        addResultsLabels();

        this.setVisible(true);
    }

    /**
     * Inspects global variable score parameters to build output text strings within formatting labels.
     */
    private void addResultsLabels() {
        add(new JLabel("RSI: " + AppState.getInstance().getFinancialIndicators().getRSI()));
        add(new JLabel("MACD: " + AppState.getInstance().getFinancialIndicators().getMACD()));
        add(new JLabel("ATR: " + AppState.getInstance().getFinancialIndicators().getATR()));
        add(new JLabel("CMF: " + AppState.getInstance().getFinancialIndicators().getCMF()));
    }
}