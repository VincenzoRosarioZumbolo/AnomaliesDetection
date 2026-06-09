package app.gui.components;

import app.gui.style.AppColors;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;

import javax.swing.*;
import java.awt.*;

/**
 * A customized {@link JPanel} designed to mimic a modern visual card component.
 * <p>
 * This component features standard rounded corners (via FlatLaf client properties) and a
 * configurable drop shadow border to give it an elevated appearance above the background.
 * </p>
 *
 * @see JPanel
 * @see FlatDropShadowBorder
 */
public class CardPanel extends JPanel {

    /**
     * Constructs a CardPanel with a specified layout manager and optional drop shadow.
     *
     * @param layout the {@link LayoutManager} to be used for positioning components
     * @param shadow {@code true} to apply a drop shadow border to the panel; {@code false} otherwise
     */
    public CardPanel(LayoutManager layout, boolean shadow) {
        super(layout);
        setBackground(AppColors.BACKGROUND_WHITE);

        // Applies a rounded corner styling using FlatLaf specific properties
        putClientProperty("FlatLaf.style", "arc: 16");

        if (shadow) {
            this.setBorder(new FlatDropShadowBorder(
                    AppColors.SHADOW, new Insets(0, 0, 8, 8), 1));
        }
    }

    /**
     * Constructs a CardPanel with a specified layout manager and an enabled drop shadow.
     *
     * @param layout the {@link LayoutManager} to be used for positioning components
     */
    public CardPanel(LayoutManager layout) {
        this(layout, true);
    }

    /**
     * Constructs a CardPanel initialized with a {@link GridBagLayout} and an enabled drop shadow.
     */
    public CardPanel() {
        this(new GridBagLayout());
    }
}