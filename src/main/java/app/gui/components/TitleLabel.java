package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;

public class TitleLabel extends JLabel {

    /**
     * @param text Title text
     * @param headingLevel Level: "h1", "h2", "h3", "h4"
     */
    public TitleLabel(String text, String headingLevel) {

        super(text);

        putClientProperty("FlatLaf.styleClass", headingLevel);
        setForeground(AppColors.PRIMARY_HOVER);
    }
}