package app.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FullScreenFrame extends JFrame {

    private boolean isFullScreen = false;
    private Rectangle screenBounds;
    private int screenState = JFrame.MAXIMIZED_BOTH;

    public FullScreenFrame(String title) {
        super(title);

        screenBounds = getBounds();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBehaviour();
    }

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

    private void toggleFullScreen() {

        this.dispose();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if(isFullScreen) {
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
