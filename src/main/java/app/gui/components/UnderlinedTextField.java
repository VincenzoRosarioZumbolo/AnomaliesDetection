package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A customized text input component styled with a minimalist bottom underline instead of a boxed frame.
 * <p>
 * This field features centered horizontal alignment and flat styling configurations.
 * It registers focus listeners to dynamically change the underline border color between standard text colors
 * and primary brand colors when interacting with the UI.
 * </p>
 *
 * @see JTextField
 * @see MatteBorder
 */
public class UnderlinedTextField extends JTextField {

    /**
     * Constructs an UnderlinedTextField populated with an initial text string and a default column allocation.
     *
     * @param text the initial text displayed within the component input
     */
    public UnderlinedTextField(String text) {
        super(text, 5);
        initStyle();
    }

    /**
     * Constructs an empty UnderlinedTextField using a fixed capacity column constraint.
     *
     * @param columns the number of columns to use to calculate preferred width
     */
    public UnderlinedTextField(int columns) {
        super(columns);
        initStyle();
    }

    /**
     * Initializes structural and aesthetic component settings.
     * <p>
     * Sets transparency, centers internal alignments, strips FlatLaf button controls/borders,
     * applies margins, and registers dynamic focus handling behavior.
     * </p>
     */
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

    /**
     * Regenerates a single-pixel matte bottom border using the designated color constraint.
     *
     * @param color the {@link Color} assigned to paint the bottom underline segment
     */
    private void updateBorder(Color color) {
        setBorder(new MatteBorder(0, 0, 1, 0, color));
        repaint();
    }
}