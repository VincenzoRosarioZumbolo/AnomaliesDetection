package app.gui.components;

import app.gui.style.AppColors;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class LabeledComponent extends InvisiblePanel {

    @Getter
    private JLabel label;
    @Getter
    private JComponent component;

    public LabeledComponent(String label, JComponent component) {

        super();

        this.label = new BoldLabel(label);
        this.component = component;

        this.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0, 0));
        this.add(this.component, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
    }
}
