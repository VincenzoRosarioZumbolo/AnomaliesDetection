package app.gui.style;

import java.awt.Color;

/**
 * A central style registry defining the consistent color palette used across the application user interface.
 * <p>
 * This utility class bundles static color references categorized by their roles, including branding accents,
 * semantic feedback states (success, warning, error), canvas layers, typographic shades, and specific chart plotting elements.
 * </p>
 *
 * @see Color
 */
public class AppColors {

    // Primary branding design constraints

    /** The core primary branding accent color. */
    public static final Color PRIMARY = new Color(0, 102, 204);

    /** The interactive hover highlight variation of the primary brand color. */
    public static final Color PRIMARY_HOVER = new Color(0, 120, 215);

    // Semantic status alert color definitions

    /** Semantic feedback color signaling successful processes or normal operational boundaries. */
    public static final Color SUCCESS = new Color(40, 167, 69);

    /** Semantic feedback color signaling critical failures, validation errors, or high-risk states. */
    public static final Color DANGER = new Color(186, 40, 54);

    /** Semantic feedback color signaling mild warnings, pending tasks, or cautious threshold violations. */
    public static final Color WARNING = new Color(255, 193, 7);

    // Structural background canvas layers

    /** The standard stark white backdrop surface fill applied across cards and primary components. */
    public static final Color BACKGROUND_WHITE = Color.WHITE;

    /** The soft charcoal tone matching drop shadow border layers to create container depth. */
    public static final Color SHADOW = new Color(100, 100, 100);

    /** A completely transparent color map used to strip default frame fills or layer outlines. */
    public static final Color EMPTY = new Color(0, 0, 0, 0);

    // Typographic rendering variations

    /** The standard baseline dark gray font paint applied across structural labels and input borders. */
    public static final Color TEXT = new Color(108, 117, 125);

    // Chart component visualization channels

    /** The highlight color channel used for anomaly thresholds. */
    public static final Color CHART_ANOMALY = new Color(209, 20, 20);

    /** The default primary series color for multi-line time-series line charts. */
    public static final Color CHART_LINE1 = new Color(0, 175, 23);

    /** The secondary alternative data series color. */
    public static final Color CHART_LINE2 = new Color(255, 106, 0);

    /** The default primary series color for bar charts. */
    public static final Color CHART_SCORE = new Color(76, 0, 197);
}