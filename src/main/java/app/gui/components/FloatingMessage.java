package app.gui.components;

import app.gui.style.AppColors;
import javax.swing.*;
import java.awt.*;

/**
 * A utility class that spawns transient, non-blocking notification windows above the UI.
 * <p>
 * This component initializes an undecorated {@link JWindow} mapped relative to a calling button's
 * screen position. It utilizes sequential swing timers to display a message with type-specific background
 * styling (Error, Warning, Success), gracefully fades out by adjusting opacity metrics,
 * and automatically disposes of its allocated window context.
 * </p>
 *
 * @see JWindow
 * @see Timer
 */
public class FloatingMessage {

    /** Error notification type classification flag. */
    public static final int ERROR_MESSAGE = 1;

    /** Warning notification type classification flag. */
    public static final int WARNING_MESSAGE = 2;

    /** Success notification type classification flag. */
    public static final int SUCCESS_MESSAGE = 3;

    /** The underlying window frame holding the floating notification content. */
    private final JWindow messageWindow;

    /** The stylized canvas block encapsulating notification text labels. */
    private final CardPanel contentPanel;

    /**
     * Constructs and displays a transient floating notification window anchored near a specific component button.
     *
     * @param messageText   the narrative string text to be rendered within the notification alert area
     * @param callingButton the reference {@link JButton} component used to position the notification window on screen
     * @param messageType   the structural style categorization code (e.g., {@code ERROR_MESSAGE}, {@code WARNING_MESSAGE}, {@code SUCCESS_MESSAGE})
     */
    public FloatingMessage(String messageText, JButton callingButton, int messageType) {
        messageWindow = new JWindow();
        messageWindow.setAlwaysOnTop(true);
        messageWindow.setSize(300, 100);
        messageWindow.setOpacity(0.90f);

        contentPanel = new CardPanel(new BorderLayout(), false);
        messageWindow.setContentPane(contentPanel);

        setupContent(messageText, messageType);
        positionWindow(callingButton);

        new DisposeTimers(messageWindow);
        messageWindow.setVisible(true);
    }

    /**
     * Assigns message content, alignments, text constraints, and colors based on the requested notification code.
     *
     * @param messageText the string text context to output inside HTML-bounded formatting labels
     * @param messageType the theme classification integer specifying background fill guidelines
     */
    private void setupContent(String messageText, int messageType) {
        setColor(messageType);

        JLabel messageLabel = new BoldLabel("<html><body style='width: 220px; text-align: center;'>"
                + messageText + "</body></html>");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        contentPanel.add(messageLabel, BorderLayout.CENTER);
    }

    /**
     * Determines the physical screen placement coordinates for the notification window.
     * <p>
     * If the triggering button is visible on screen, the window positions itself dynamically using the alignment
     * calculations; otherwise, it defaults to the center of the desktop screen.
     * </p>
     *
     * @param callingButton the target reference {@link JButton} component to anchor against
     */
    private void positionWindow(JButton callingButton) {
        if (callingButton != null && callingButton.isShowing()) {
            Point buttonLocation = callingButton.getLocationOnScreen();
            messageWindow.setLocation(calculatePoint(callingButton, buttonLocation));
        } else {
            messageWindow.setLocationRelativeTo(null);
        }
    }

    /**
     * Calculates the exact pixel coordinates to display the window directly above the calling button.
     * <p>
     * Includes bounds-checking rules to prevent the window from rendering off-screen:
     * <ul>
     * <li>Ensures a minimum 5-pixel horizontal margin from left and right desktop display edges.</li>
     * <li>If rendering the window above the button would push it past the top edge of the monitor,
     * it flips below the button instead.</li>
     * </ul>
     * </p>
     *
     * @param callingButton         the target reference button component
     * @param callingButtonLocation the absolute on-screen coordinates of the button
     * @return a {@link Point} object representing the safe rendering target coordinates on screen
     */
    private Point calculatePoint(JButton callingButton, Point callingButtonLocation) {
        int x = (int) callingButtonLocation.getX() + (callingButton.getWidth() - messageWindow.getWidth()) / 2;
        int y = (int) callingButtonLocation.getY() - messageWindow.getHeight() - 10;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (x < 5)
            x = 5;
        else if (x + messageWindow.getWidth() > screenSize.width - 5)
            x = screenSize.width - messageWindow.getWidth() - 5;

        if (y < 0)
            y = (int) callingButtonLocation.getY() + callingButton.getHeight() + 10;

        return new Point(x, y);
    }

    /**
     * Maps the container's background surface colors to match semantic visual requirements.
     *
     * @param messageType the classification integer specifying theme color adjustments
     * @throws IllegalArgumentException if the provided message type code does not map to any recognized parameters
     */
    private void setColor(int messageType) {
        switch (messageType) {
            case ERROR_MESSAGE -> contentPanel.setBackground(AppColors.DANGER);
            case WARNING_MESSAGE -> contentPanel.setBackground(AppColors.WARNING);
            case SUCCESS_MESSAGE -> contentPanel.setBackground(AppColors.SUCCESS);
            default -> throw new IllegalArgumentException("Invalid message type: " + messageType);
        }
    }

    /**
     * An internal structural container managing multi-tier coordinate lifecycle timers.
     * <p>
     * Handles sequential fade-out transitions and schedules standard execution intervals to decrement
     * target transparency before purging native window environments.
     * </p>
     */
    private static class DisposeTimers {

        /** The timer instance tasked with gradually decreasing window transparency at fine intervals. */
        private final Timer decreaseOpacityTimer;

        /**
         * Constructs a coordinated cluster of lifecycle tracking timers targeting the designated window.
         *
         * @param window the reference {@link JWindow} container context to fade out and close
         */
        public DisposeTimers(JWindow window) {
            decreaseOpacityTimer = new Timer(10, e -> {
                if(window.getOpacity() > 0.01f){
                    window.setOpacity(window.getOpacity()-0.01f);
                }
                else{
                    window.setOpacity(0.0f);
                }
            });
            decreaseOpacityTimer.setRepeats(true);

            Timer startDecreaseOpacityTimer = new Timer(1500, e -> {
                ((Timer) e.getSource()).stop();
                decreaseOpacityTimer.start();
            });
            startDecreaseOpacityTimer.setRepeats(false);


            Timer disposeTimer = new Timer(2500, e -> {
                window.dispose();
                ((Timer) e.getSource()).stop();
                decreaseOpacityTimer.setRepeats(false);
                decreaseOpacityTimer.stop();
            });

            disposeTimer.start();
            startDecreaseOpacityTimer.start();
        }
    }
}