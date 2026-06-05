package app.gui;

import app.gui.components.CardPanel;
import app.gui.components.MenuTabButton;
import app.gui.style.PaddingConstants;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends CardPanel {

    private ButtonPanelPair[] components;

    public MenuPanel() {

        this.setVisible(false);

        addComponents();
    }
    
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
                    GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, PaddingConstants.PADDING_NONE, 0, 0));

            components[i].button.setVisible(false);
            components[i].panel.setVisible(false);
        }

        revalidate();
        repaint();
    }
    
    public void createCharts() {

        this.setVisible(true);

        ((ResultsChartsPanel)components[0].panel).createCharts();

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
    
    private void showPair(int index) {
        
        for (int j = 0; j < components.length; j++)
            if (j == index) {
                components[j].button.setSelected(true);
                components[j].panel.setVisible(true);
            } else {
                components[j].button.setSelected(false);
                components[j].panel.setVisible(false);
            }

        revalidate();
        repaint();
    }

    private static class ButtonPanelPair {

        MenuTabButton button;
        JPanel panel;

        public ButtonPanelPair(MenuTabButton button, JPanel panel) {
            this.button = button;
            this.panel = panel;
        }
    }
}