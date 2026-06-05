package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class UnderlinedTextField extends JTextField {

    public UnderlinedTextField(String text) {
        super(text, 5);
        initStyle();
    }

    public UnderlinedTextField(int columns) {
        super(columns);
        initStyle();
    }

    private void initStyle() {

        setOpaque(false);
        setBackground(AppColors.EMPTY);
        setHorizontalAlignment(JTextField.CENTER);

        putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, false);
        putClientProperty(FlatClientProperties.STYLE, "borderWidth: 0; focusWidth: 0; arc: 0");

        updateBorder(AppColors.TEXT);
        setMargin(new Insets(2, 2, 5, 2));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBorder(AppColors.PRIMARY);
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateBorder(AppColors.TEXT);
            }
        });
    }

    private void updateBorder(Color color) {

        setBorder(new MatteBorder(0, 0, 1, 0, color));
        repaint();
    }
}