package app.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A specialized {@link JFrame} that provides built-in support for full-screen mode toggling.
 * <p>
 * Pressing the <b>F11</b> key natively triggers a smooth transition between standard windowed mode
 * (maximized window boundaries with native decorations) and a seamless, undecorated true
 * full-screen mode utilizing the system's default graphics device.
 * </p>
 *
 * @see JFrame
 * @see GraphicsDevice
 */
public class FullScreenFrame extends JFrame {

    /** Tracks whether the window is currently rendering in full-screen mode. */
    private boolean isFullScreen = false;

    /** Caches the window bounds prior to toggling full-screen mode to allow faithful restoration. */
    private Rectangle screenBounds;

    /** Stores the extended state window tracking flag (e.g., MAXIMIZED_BOTH). */
    private int screenState = JFrame.MAXIMIZED_BOTH;

    /**
     * Constructs a FullScreenFrame with a window title, starts it in a maximized state,
     * and initializes full-screen key bindings.
     *
     * @param title the title displayed on the native window title bar
     */
    public FullScreenFrame(String title) {
        super(title);

        screenBounds = getBounds();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBehaviour();
    }

    /**
     * Configures the key binding map to capture the F11 key stroke globally inside the focused window.
     */
    private void setBehaviour() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        String actionName = "toggleFullScreen";

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(keyStroke, actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });
    }

    /**
     * Switches the application window between standard windowed/maximized presentation
     * and undecorated true hardware full-screen mode.
     * <p>
     * The frame is temporarily disposed and re-validated during this state change to safely modify
     * its decoration state without throwing exceptions.
     * </p>
     */
    private void toggleFullScreen() {
        this.dispose();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (isFullScreen) {
            this.setUndecorated(false);
            gd.setFullScreenWindow(null);
            setBounds(screenBounds);
            setExtendedState(screenState);
            isFullScreen = false;
        } else {
            this.setUndecorated(true);
            gd.setFullScreenWindow(this);
            screenBounds = getBounds();
            screenState = getExtendedState();
            isFullScreen = true;
        }

        this.setVisible(true);
    }
}