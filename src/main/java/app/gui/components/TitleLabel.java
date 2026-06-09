package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;

/**
 * A customized header title label that relies on FlatLaf semantics for consistent font scales.
 * <p>
 * This class applies designated typographic rules using FlatLaf style class declarations,
 * automatically formatting font sizes based on requested heading levels.
 * </p>
 *
 * @see JLabel
 */
public class TitleLabel extends JLabel {

    /**
     * Constructs a standardized title label component.
     *
     * @param text         the string contents to write into the heading label
     * @param headingLevel the structural scale style classification name (e.g., "h1", "h2", "h3", "h4")
     */
    public TitleLabel(String text, String headingLevel) {
        super(text);

        putClientProperty("FlatLaf.styleClass", headingLevel);
        setForeground(AppColors.PRIMARY_HOVER);
    }
}