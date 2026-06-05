package app.gui;

import app.gui.components.FullScreenFrame;
import app.gui.components.ScrollablePanel;
import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;

public class MainPage {

    private JFrame mainFrame;
    private JPanel mainPanel;
    private MenuPanel menuPanel;

    public  MainPage() {

        setFlatLaf();

        createMainFrame();
        createScrollPane();
        addSearchPanel();
        addResultsCharts();

        mainFrame.revalidate();
        mainFrame.repaint();

    }

    private void setFlatLaf() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        FlatLightLaf.setup();

        UIManager.put("Component.focusColor", AppColors.PRIMARY);
        UIManager.put("Button.focusedBorderColor", AppColors.PRIMARY);
    }

    private void createMainFrame() {

        mainFrame = new FullScreenFrame("Financial Tracker");
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setSize(new Dimension(1000,800));
        mainFrame.setIconImage(new FlatSVGIcon("images/appIcon.svg", 30, 30).getImage());
        mainFrame.setVisible(true);
    }

    private void createScrollPane() {

        mainPanel = new ScrollablePanel(new GridBagLayout());
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setVisible(true);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(mainPanel);
        scrollPane.setVisible(true);

        mainFrame.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    private void addSearchPanel() {

        mainPanel.add(new SearchPanel(this), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 10, 20), 0, 0));
    }

    private void addResultsCharts() {

        menuPanel = new MenuPanel();

        mainPanel.add(menuPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(30, 60, 40, 60), 0, 0));
    }

    public void createCharts() {
        menuPanel.createCharts();
    }
}
