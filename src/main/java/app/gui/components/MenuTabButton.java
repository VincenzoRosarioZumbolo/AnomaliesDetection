package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * A highly tailored header navigation tab navigation component that extends {@link JButton}.
 * <p>
 * Employs FlatLaf style client definitions to force flat, borderless behaviors. It registers
 * custom layout dimensions, enables cursor adjustments, and features custom tracking states
 * that draw thick bottom border segments matching active state color updates.
 * </p>
 *
 * @see JButton
 * @see FlatClientProperties
 */
public class MenuTabButton extends JButton {

    /** Tracks whether this tab component is actively selected within the navigation view layout. */
    private boolean selected = false;

    /**
     * Constructs a MenuTabButton initialized with navigation text parameters and initial styling metrics.
     *
     * @param text the descriptive navigation label assigned to this menu tab
     */
    public MenuTabButton(String text) {
        super(text);

        putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);

        setFocusable(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setMargin(new Insets(50, 20, 50, 20));

        updateAppearance();
    }

    /**
     * Alters the structural validation selection state, updating look-and-feel constraints.
     *
     * @param selected {@code true} to highlight the component as an active option; {@code false} otherwise
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateAppearance();
        repaint();
    }

    /**
     * Interactively updates operational foreground text metrics and highlights lower matte borders
     * based on active selection variables.
     */
    private void updateAppearance() {
        if (selected) {
            setForeground(AppColors.PRIMARY);
            setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, AppColors.PRIMARY));
        } else {
            setForeground(AppColors.TEXT);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        }
    }
}