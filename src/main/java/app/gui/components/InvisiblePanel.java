package app.gui.components;

import app.gui.style.AppColors;

import javax.swing.*;
import java.awt.*;

/**
 * A customized {@link JPanel} subclass configured to be non-opaque.
 * <p>
 * This class acts as a layout structural component that lets underlying graphical backdrops
 * show through completely while retaining default FlatLaf sizing arcs for uniform child constraints.
 * </p>
 *
 * @see JPanel
 */
public class InvisiblePanel extends JPanel {

    /**
     * Constructs an InvisiblePanel with a specified layout manager, setting opacity to false.
     *
     * @param layout the {@link LayoutManager} to use for internal component structure
     */
    public InvisiblePanel(LayoutManager layout) {
        super(layout);
        setBackground(AppColors.BACKGROUND_WHITE);
        setOpaque(false);

        putClientProperty("FlatLaf.style", "arc: 16");
    }

    /**
     * Constructs an InvisiblePanel initialized with a {@link GridBagLayout} and transparency enabled.
     */
    public InvisiblePanel() {
        this(new GridBagLayout());
    }
}