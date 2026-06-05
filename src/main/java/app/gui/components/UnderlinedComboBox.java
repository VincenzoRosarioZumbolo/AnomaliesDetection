package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class UnderlinedComboBox<E> extends JComboBox<E> {

    public UnderlinedComboBox() {
        super();
        initStyle();
    }

    public UnderlinedComboBox(E[] items) {
        super(items);
        initStyle();
    }

    private void initStyle() {

        setOpaque(false);
        setBackground(AppColors.EMPTY);

        putClientProperty(FlatClientProperties.STYLE,
                "borderWidth: 0; " +
                        "focusWidth: 0; " +
                        "arc: 0; " +
                        "buttonStyle: borderless; " +
                        "arrowType: chevron");

        updateBorder(AppColors.TEXT);

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