package app.gui.style;

import java.awt.*;

/**
 * A central constants warehouse specifying standardized spacing and layout padding configurations.
 * <p>
 * This utility registers a matrix of reusable {@link Insets} designed to govern inner spacing
 * distributions inside layout managers like {@link GridBagLayout}, guaranteeing structural symmetry across all panels.
 * </p>
 *
 * @see Insets
 * @see GridBagConstraints
 */
public class PaddingConstants {

    /** Standard universal layout separation metrics applying an even 10-pixel buffer across all component edges. */
    public static final Insets PADDING_STANDARD = new Insets(10, 10, 10, 10);

    /** An expansive 30-pixel outer safety boundary applied around complex views or macro panel partitions. */
    public static final Insets PADDING_LARGE = new Insets(30, 30, 30, 30);

    /** An asymmetrical layout allocation prioritizing a deep 30-pixel lower baseline safety buffer. */
    public static final Insets PADDING_BOTTOM = new Insets(10, 10, 30, 10);

    /** An asymmetrical layout allocation prioritizing an elevated 30-pixel header accent safety buffer. */
    public static final Insets PADDING_TOP = new Insets(30, 10, 10, 10);

    /** An asymmetrical layout allocation prioritizing an extended 30-pixel left margin offset alignment. */
    public static final Insets PADDING_LEFT = new Insets(10, 30, 10, 10);

    /** An asymmetrical layout allocation prioritizing an extended 30-pixel right margin offset alignment. */
    public static final Insets PADDING_RIGHT = new Insets(10, 10, 10, 30);

    /** A zeroed margin setting used to completely strip outer component padding constraints. */
    public static final Insets PADDING_NONE = new Insets(0, 0, 0, 0);
}