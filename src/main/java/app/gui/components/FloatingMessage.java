package app.gui.components;

import app.gui.style.AppColors;
import javax.swing.*;
import java.awt.*;

public class FloatingMessage {

    public static final int ERROR_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int SUCCESS_MESSAGE = 3;

    private final JWindow messageWindow;
    private final CardPanel contentPanel;

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

    private void setupContent(String messageText, int messageType) {

        setColor(messageType);

        JLabel messageLabel = new BoldLabel("<html><body style='width: 220px; text-align: center;'>"
                + messageText + "</body></html>");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        contentPanel.add(messageLabel, BorderLayout.CENTER);
    }

    private void positionWindow(JButton callingButton) {

        if (callingButton != null && callingButton.isShowing()) {

            Point buttonLocation = callingButton.getLocationOnScreen();
            messageWindow.setLocation(calculatePoint(callingButton, buttonLocation));
        } else {
            messageWindow.setLocationRelativeTo(null);
        }
    }

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

    private void setColor(int messageType) {

        switch (messageType) {
            case ERROR_MESSAGE -> contentPanel.setBackground(AppColors.DANGER);
            case WARNING_MESSAGE -> contentPanel.setBackground(AppColors.WARNING);
            case SUCCESS_MESSAGE -> contentPanel.setBackground(AppColors.SUCCESS);
            default -> throw new IllegalArgumentException("Invalid message type: " + messageType);
        }
    }

    private static class DisposeTimers {

        private final Timer decreaseOpacityTimer;

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