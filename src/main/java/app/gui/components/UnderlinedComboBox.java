package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A customized generic drop-down selection list box styled with a minimalist bottom underline instead of standard full borders.
 * <p>
 * This list box implements FlatLaf custom properties to hide standard arrow container frames,
 * replacing them with flat, borderless chevron buttons. Focus hooks dynamically update the border color
 * between standard and active states.
 * </p>
 *
 * @param <E> the type of elements contained inside this combo box selection map
 * @see JComboBox
 * @see FlatClientProperties
 */
public class UnderlinedComboBox<E> extends JComboBox<E> {

    /**
     * Constructs an empty selection combo box, initializing properties and borders.
     */
    public UnderlinedComboBox() {
        super();
        initStyle();
    }

    /**
     * Constructs a choice selector initialized with a flat item collection array.
     *
     * @param items an array containing the selection menu options
     */
    public UnderlinedComboBox(E[] items) {
        super(items);
        initStyle();
    }

    /**
     * Enforces non-opaque visibility states and overrides component behaviors via FlatLaf dictionary adjustments.
     */
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

    /**
     * Recreates the bottom inline border using a designated color constraint.
     *
     * @param color the color matrix to paint the active bottom edge line element
     */
    private void updateBorder(Color color) {
        setBorder(new MatteBorder(0, 0, 1, 0, color));
        repaint();
    }
}