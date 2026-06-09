package app.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A customized {@link JLabel} that forces its display text to be bold.
 * <p>
 * This component retains all baseline configuration metrics of a default text label,
 * but derives and overrides its active text styling font to {@link Font#BOLD} upon instantiation.
 * </p>
 *
 * @see JLabel
 */
public class BoldLabel extends JLabel {

    /**
     * Constructs a BoldLabel initialized with the specified text string.
     *
     * @param text the text to be displayed by the label component
     */
    public BoldLabel(String text) {
        super(text);
        setFont(getFont().deriveFont(Font.BOLD));
    }
}