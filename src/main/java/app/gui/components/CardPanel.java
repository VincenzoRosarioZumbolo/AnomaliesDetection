package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {

    public CardPanel(LayoutManager layout, boolean shadow) {

        super(layout);
        setBackground(AppColors.BACKGROUND_WHITE);

        putClientProperty("FlatLaf.style", "arc: 16");
        if (shadow)
            this.setBorder(new FlatDropShadowBorder(
                    AppColors.SHADOW, new Insets(0, 0, 8, 8), 1));
    }

    public CardPanel(LayoutManager layout) {

        this(layout, true);
    }

    public CardPanel() {

        this(new GridBagLayout());
    }
}