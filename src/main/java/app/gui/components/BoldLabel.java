package app.gui.components;

import javax.swing.*;
import java.awt.*;

public class BoldLabel extends JLabel {

    public BoldLabel(String text) {

        super(text);
        setFont(getFont().deriveFont(Font.BOLD));
    }
}