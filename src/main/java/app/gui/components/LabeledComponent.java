package app.gui.components;

import app.gui.style.AppColors;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * A structural UI wrapper that places a bold descriptive label side-by-side with any input component.
 * <p>
 * This class inherits transparency properties from {@link InvisiblePanel} and uses a
 * {@link GridBagLayout} to right-align both components with uniform spacing.
 * </p>
 *
 * @see InvisiblePanel
 * @see BoldLabel
 * @see GridBagLayout
 */
public class LabeledComponent extends InvisiblePanel {

    /** The bold descriptor text label positioned on the left side of this panel layout. */
    @Getter
    private JLabel label;

    /** The actual interactive field or control component positioned on the right side of this panel layout. */
    @Getter
    private JComponent component;

    /**
     * Constructs a container wrapping a label string paired beside a child component container.
     *
     * @param label     the description string to assign into the left-anchored {@link BoldLabel}
     * @param component the interactive input element to position on the right
     */
    public LabeledComponent(String label, JComponent component) {
        super();

        this.label = new BoldLabel(label);
        this.component = component;

        // Adds the text descriptor to the grid (column 0)
        this.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(10, 10, 10, 0), 0, 0));

        // Adds the target input component to the grid (column 1)
        this.add(this.component, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
    }
}