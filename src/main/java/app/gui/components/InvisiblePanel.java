package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;
import java.awt.*;

public class InvisiblePanel extends JPanel {

    public InvisiblePanel(LayoutManager layout) {

        super(layout);
        setBackground(AppColors.BACKGROUND_WHITE);
        setOpaque(false);

        putClientProperty("FlatLaf.style", "arc: 16");
    }

    public InvisiblePanel() {

        this(new GridBagLayout());
    }
}
