package app.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link JPanel} extension that implements the {@link Scrollable} interface to provide
 * specialized viewport behaviors when embedded inside a {@link JScrollPane}.
 * <p>
 * Crucially, this panel forces its width to match the containing viewport width while allowing
 * its height to expand freely. This design is optimal for vertical-scroll UI feeds where
 * contents wrap naturally and horizontal scrolling needs to be restricted.
 * </p>
 *
 * @see JPanel
 * @see Scrollable
 * @see JScrollPane
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    /**
     * Constructs a new ScrollablePanel using the designated layout manager.
     *
     * @param layout the {@link LayoutManager} configured to govern internal component dimensions
     */
    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * Returns the baseline preferred sizing metrics for this viewport container.
     *
     * @return the {@link Dimension} containing the panel's preferred size
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Defines the small step scroll increment offset when using a fine controller (e.g., arrow keys, wheel ticks).
     *
     * @param visibleRect the view area visible within the viewport
     * @param orientation either {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}
     * @param direction   negative values signal up/left tracking, positive values signal down/right tracking
     * @return a constant pixel offset value of 10
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    /**
     * Defines the larger structural step scroll increment offset when clicking inside track blocks (e.g., Page Up/Down).
     *
     * @param visibleRect the view area visible within the viewport
     * @param orientation either {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}
     * @param direction   negative values signal up/left tracking, positive values signal down/right tracking
     * @return a constant pixel offset value of 100
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    /**
     * Forces the component width to restrictively match the enclosing viewport width.
     *
     * @return {@code true} consistently to prevent horizontal scroll bars from appearing and
     * force layout shrinking
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Permits the component height to expand dynamically past viewport bounds.
     *
     * @return {@code false} consistently to let the container expand vertically and activate vertical scrolling
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}