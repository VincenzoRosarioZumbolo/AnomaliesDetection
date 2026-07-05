package app.gui.views;

import app.gui.components.CardPanel;
import app.gui.components.MenuTabButton;
import app.gui.style.PaddingConstants;

import javax.swing.*;
import java.awt.*;

/**
 * A composite dashboard selector container that switches view visibility between modules.
 * <p>
 * Implements a Card Layout logic based on a paired array of control structures, associating
 * {@link MenuTabButton} selectors with operational panels (Results Charts, Anomalies Detection,
 * Financial Values, and Indicators Anomalies).
 * </p>
 *
 * @see CardPanel
 * @see MenuTabButton
 */
public class MenuPanel extends CardPanel {

    /** Internal structural array tracking the button-panel pairs managed by the menu. */
    private ButtonPanelPair[] components;

    /**
     * Constructs a {@code MenuPanel} instance. By default, the menu remains invisible
     * until successful data import requires its exposure.
     */
    public MenuPanel() {
        this.setVisible(false);
        addComponents();
    }

    /**
     * Initializes the available tabs array (set to 4 elements), instantiates the corresponding
     * display panels, and aligns the spatial constraints via {@link GridBagConstraints}.
     */
    private void addComponents() {
        components = new ButtonPanelPair[4];

        components[0] = new ButtonPanelPair(new MenuTabButton("Results Charts"), new ResultsChartsPanel());
        components[1] = new ButtonPanelPair(new MenuTabButton("Anomalies Detection"), new DataRecordAnomalyDetectionPanel());
        components[2] = new ButtonPanelPair(new MenuTabButton("Financial Values"), new FinancialValuesPanel());
        components[3] = new ButtonPanelPair(new MenuTabButton("Indicators Anomalies"), new FinancialIndicatorsAnomalyDetectionPanel());

        for (int i = 0; i < components.length; i++) {
            int finalI = i;
            components[i].button.addActionListener(e -> showPair(finalI));
            add(components[i].button, new GridBagConstraints(finalI, 0, 1, 1, 0.5, 0.1,
                    GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_NONE, 0, 0));
            add(components[i].panel, new GridBagConstraints(0, 1, 4, 1, 0.5, 0.9,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));

            components[i].button.setVisible(false);
            components[i].panel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    /**
     * Reveals the menu structure, orders the initial generation of charts on the historical
     * results panel, and sets the visual focus on the first tab.
     */
    public void createCharts() {
        this.setVisible(true);

        ((ResultsChartsPanel) components[0].panel).createCharts();

        components[0].button.setSelected(true);
        components[0].button.setVisible(true);
        components[0].panel.setVisible(true);

        for (int i = 1; i < components.length; i++) {
            components[i].button.setSelected(false);
            components[i].button.setVisible(true);
            components[i].panel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    /**
     * Modifies the selection state of the buttons and toggles the visibility of the panel
     * corresponding to the specified index, hiding all other alternative elements.
     *
     * @param index the numerical position of the tab within the components array
     */
    private void showPair(int index) {
        for (int j = 0; j < components.length; j++) {
            if (j == index) {
                components[j].button.setSelected(true);
                components[j].panel.setVisible(true);
            } else {
                components[j].button.setSelected(false);
                components[j].panel.setVisible(false);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Logical support record aimed at pairing a menu header button with its respective content panel.
     */
    private static class ButtonPanelPair {

        /** The toggle button designated for selecting this specific tab. */
        MenuTabButton button;

        /** The linked container panel displayed upon button activation. */
        JPanel panel;

        /**
         * Standard direct assignment constructor for the button-panel pair.
         *
         * @param button the navigation button component
         * @param panel  the destination canvas
         */
        public ButtonPanelPair(MenuTabButton button, JPanel panel) {
            this.button = button;
            this.panel = panel;
        }
    }
}