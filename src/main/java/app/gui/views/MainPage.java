package app.gui.views;

import app.gui.components.FullScreenFrame;
import app.gui.components.ScrollablePanel;
import app.gui.style.AppColors;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * The master application layout coordinator initializing top-level window structures.
 * <p>
 * Manages look-and-feel bindings (FlatLaf engine updates), window sizing schemes,
 * navigation scroll wrappers, query panel injections, and conditional metric rendering layouts.
 * </p>
 *
 * @see FullScreenFrame
 * @see SearchPanel
 * @see MenuPanel
 */
public class MainPage {

    /** The absolute top-level visual container framing screen layout constraints. */
    private JFrame mainFrame;

    /** The master container wrapping internal panels to accommodate fluid vertical scaling. */
    private JPanel mainPanel;

    /** The persistent control panel switching visibility between analytics sub-views. */
    private MenuPanel menuPanel;

    /**
     * Constructs a MainPage layout context, bootstrapping look-and-feel layers
     * and rendering baseline components.
     */
    public MainPage() {
        setFlatLaf();

        createMainFrame();
        createScrollPane();
        addSearchPanel();
        addResultsCharts();

        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * Configures universal FlatLaf theme skin metrics and registers system color variables.
     */
    private void setFlatLaf() {
        Locale.setDefault(Locale.ENGLISH);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        FlatLightLaf.setup();

        UIManager.put("Component.focusColor", AppColors.PRIMARY);
        UIManager.put("Button.focusedBorderColor", AppColors.PRIMARY);
    }

    /**
     * Assembles foundational frame traits, title parameters, and window icon pathways.
     */
    private void createMainFrame() {
        mainFrame = new FullScreenFrame("Financial Tracker");
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setSize(new Dimension(1000, 800));
        mainFrame.setIconImage(new FlatSVGIcon("images/appIcon.svg", 30, 30).getImage());
        mainFrame.setVisible(true);
    }

    /**
     * Encapsulates the master canvas block within a custom scroll pane to manage responsive view constraints.
     */
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

    /**
     * Places the initial historical metric parameter filter component at the top of the interface.
     */
    private void addSearchPanel() {
        mainPanel.add(new SearchPanel(this), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 10, 20), 0, 0));
    }

    /**
     * Instantiates the core navigation menu workspace panel context within the main container flow.
     */
    private void addResultsCharts() {
        menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 20, 20, 20), 0, 0));
    }

    /**
     * Commands the downstream navigation dashboard tab elements to plot charts following dataset queries.
     */
    public void createCharts() {
        menuPanel.createCharts();
    }
}