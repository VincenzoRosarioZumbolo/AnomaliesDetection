package app.gui.components;

import javax.swing.*;
import java.awt.*;

public class ScrollablePanel extends JPanel implements Scrollable {

    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100;
    }

    // QUESTO È IL METODO CRUCIALE
    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Restituendo true, il pannello si restringe per forzare la larghezza dello scrollpane
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // Restituiamo false per permettere lo scroll verticale
        return false;
    }
}
