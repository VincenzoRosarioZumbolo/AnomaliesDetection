package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {

        super(text);

        //behaviour
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 30, 10, 30));

        //font
        setFont(getFont().deriveFont(Font.BOLD));
        setForeground(AppColors.BACKGROUND_WHITE);

        //shape and color
        putClientProperty("JButton.buttonType", "roundRect");
        setBackground(AppColors.PRIMARY_HOVER);
        setBorderPainted(false);
    }

    public PrimaryButton(String text, ActionListener actionListener) {

        this(text);

        this.addActionListener(actionListener);
    }
}

