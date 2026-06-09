package app.gui;

import app.gui.components.CardPanel;
import app.gui.components.MenuTabButton;
import app.gui.style.PaddingConstants;

import javax.swing.*;
import java.awt.*;

/**
 * A composite dashboard selector container that switches view visibility between tabs.
 * <p>
 * Implements a card layout behavior via a custom collection array, tracking paired active layout buttons
 * and hidden display panel references (Results, Anomalies, Financial indicators).
 * </p>
 *
 * @see CardPanel
 * @see MenuTabButton
 */
public class MenuPanel extends CardPanel {

    /** The internal lookup map tracking configuration relations linking headers to panels. */
    private ButtonPanelPair[] components;

    /**
     * Constructs a MenuPanel instance, hidden by default until data availability is verified.
     */
    public MenuPanel() {
        this.setVisible(false);
        addComponents();
    }

    /**
     * Populates structural components array maps, establishing click handler mappings and layout constraints.
     */
    private void addComponents() {
        components = new ButtonPanelPair[3];

        components[0] = new ButtonPanelPair(new MenuTabButton("Results Charts"), new ResultsChartsPanel());
        components[1] = new ButtonPanelPair(new MenuTabButton("Anomalies Detection"), new AnomaliesDetectionPanel());
        components[2] = new ButtonPanelPair(new MenuTabButton("Financial Values"), new FinancialValuesPanel());

        for (int i = 0; i < components.length; i++) {
            int finalI = i;
            components[i].button.addActionListener(e -> showPair(finalI));
            add(components[i].button, new GridBagConstraints(finalI, 0, 1, 1, 0.5, 0.1,
                    GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, PaddingConstants.PADDING_NONE, 0, 0));
            add(components[i].panel, new GridBagConstraints(0, 1, 3, 1, 0.5, 0.9,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));

            components[i].button.setVisible(false);
            components[i].panel.setVisible(false);
        }

        revalidate();
        repaint();
    }

    /**
     * Reveals the dashboard component container, forces chart plotting routines on the
     * results canvas, and focuses the primary navigation index.
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
     * Updates selection states across tab header tracks, setting target panel structures
     * visible while obscuring alternative variants.
     *
     * @param index the target integer positioning context identifier matching requested views
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
     * A structural configuration helper binding an explicit navigation button to its content view.
     */
    private static class ButtonPanelPair {

        /** The visual layout selection button assigned to this tab tracking node. */
        MenuTabButton button;

        /** The visual content presentation canvas block assigned to this tab tracking node. */
        JPanel panel;

        /**
         * Standard assignment constructor pairing design tracks.
         *
         * @param button the navigation toggle element
         * @param panel  the corresponding target view component
         */
        public ButtonPanelPair(MenuTabButton button, JPanel panel) {
            this.button = button;
            this.panel = panel;
        }
    }
}