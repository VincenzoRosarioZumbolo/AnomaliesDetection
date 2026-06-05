package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class MenuTabButton extends JButton {

    private boolean selected = false;

    public MenuTabButton(String text) {

        super(text);

        putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

        setFocusable(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setMargin(new Insets(50, 20, 50, 20));

        updateAppearance();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateAppearance();
        repaint();
    }

    private void updateAppearance() {
        if (selected) {
            setForeground(AppColors.PRIMARY);
            setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, AppColors.PRIMARY));
        } else {
            setForeground(AppColors.TEXT);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        }
    }

    private String toHex() {
        return String.format("#%02x%02x%02x", AppColors.PRIMARY.getRed(), AppColors.PRIMARY.getGreen(), AppColors.PRIMARY.getBlue());
    }
}