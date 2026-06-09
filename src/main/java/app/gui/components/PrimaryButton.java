package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A customized standard action execution button utilizing rounded configurations.
 * <p>
 * This component overrides default button paint parameters to apply high-visibility brand styling,
 * bold typography text scaling, pointer-hand interaction indicators, custom margin layouts,
 * and native FlatLaf structural modifications.
 * </p>
 *
 * @see JButton
 * @see ActionListener
 */
public class PrimaryButton extends JButton {

    /**
     * Constructs a PrimaryButton initialized with designated label text and brand parameters.
     *
     * @param text the title string shown on the execution surface area
     */
    public PrimaryButton(String text) {
        super(text);

        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 30, 10, 30));

        setFont(getFont().deriveFont(Font.BOLD));
        setForeground(AppColors.BACKGROUND_WHITE);

        putClientProperty("JButton.buttonType", "roundRect");
        setBackground(AppColors.PRIMARY_HOVER);
        setBorderPainted(false);
    }

    /**
     * Constructs a PrimaryButton initialized with label text and maps an immediate action context callback.
     *
     * @param text           the title string shown on the execution surface area
     * @param actionListener the operational handler callback invoked upon left-clicking this component
     */
    public PrimaryButton(String text, ActionListener actionListener) {
        this(text);
        this.addActionListener(actionListener);
    }
}