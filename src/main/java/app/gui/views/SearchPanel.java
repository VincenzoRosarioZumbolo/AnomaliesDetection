package app.gui.views;

import app.controller.Controller;
import app.exception.*;
import app.gui.components.*;
import app.gui.style.PaddingConstants;
import app.util.LoggerUtil;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;

/**
 * An analytical configuration form panel handling historical asset metadata ingestion criteria.
 * <p>
 * Houses selection tools for asset classification tracking rows, granularity adjustments (Minutely, Hourly, Daily),
 * and permissive point-in-time boundaries. On click, it triggers asynchronous pipeline queries via the controller layer.
 * </p>
 *
 * @see CardPanel
 * @see Controller
 * @see MainPage
 */
public class SearchPanel extends CardPanel {

    /** Reference callback structure used to notify parent layers when new query responses are ready. */
    private final MainPage mainPage;

    /** Selection box identifying specific asset tracking types to query. */
    private JComboBox<String> assetComboBox;

    /** Global lookup array specifying the hardcoded asset classes available for tracking. */
    private static final String[] ASSETS = {"Bitcoin", "S&P 500", "Gold", "Oil", "USD index"};

    /** Selection box mapping mathematical time tracking step sizes to data series rows. */
    private JComboBox<String> granularityComboBox;

    /** Global configuration reference listing supported chart step frequencies. */
    private static final String[] GRANULARITIES = {"Minutely", "Hourly", "Daily"};

    /** Interactive entry container setting the lower query datetime constraint limit. */
    private DateTimePicker startDatePicker;

    /** Interactive entry container setting the upper query datetime constraint limit. */
    private DateTimePicker endDatePicker;

    /** The UI action button that runs contextual evaluation queries. */
    private JButton searchButton;

    /**
     * Constructs a SearchPanel, configuring selection widgets and mapping grid layout parameters.
     *
     * @param mainPage the main window context instance tasked with downstream coordinate updates
     */
    public SearchPanel(MainPage mainPage) {
        this.mainPage = mainPage;

        addAssetComboBox();
        addGranularityComboBox();
        addStartDatePicker();
        addEndDatePicker();
        addSearchButton();

        this.setVisible(true);
    }

    /**
     * Initializes structural layout components tracking asset classification selections.
     */
    private void addAssetComboBox() {
        assetComboBox = new UnderlinedComboBox<>(ASSETS);
        assetComboBox.setSelectedIndex(0);
        assetComboBox.setVisible(true);

        this.add(new LabeledComponent("Asset:", assetComboBox), new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes structural layout components tracking data frequency steps.
     */
    private void addGranularityComboBox() {
        granularityComboBox = new UnderlinedComboBox<>(GRANULARITIES);
        granularityComboBox.setSelectedIndex(2);
        granularityComboBox.setVisible(true);

        this.add(new LabeledComponent("Granularity:", granularityComboBox), new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes input components tracking lower datetime search boundaries.
     */
    private void addStartDatePicker() {
        startDatePicker = new UnderlinedDateTimePicker();
        startDatePicker.setVisible(true);

        this.add(new LabeledComponent("Start date:", startDatePicker), new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Initializes input components tracking upper datetime search boundaries.
     */
    private void addEndDatePicker() {
        endDatePicker = new UnderlinedDateTimePicker();
        endDatePicker.setVisible(true);

        this.add(new LabeledComponent("End date:", endDatePicker), new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    /**
     * Binds action routines to the action button used to process queries.
     */
    private void addSearchButton() {
        searchButton = new PrimaryButton("SEARCH", e -> search());
        searchButton.setVisible(true);

        this.add(searchButton, new GridBagConstraints(0, 2, 2, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    /**
     * Dispatches query parameter data models to backend processing endpoints, forcing view
     * element construction upon completion.
     * <p>
     * Displays transient visual notifications if network, parsing, or parameter validations fail.
     * </p>
     */
    private void search() {
        try {
            Controller.getInstance().searchData(
                    (String) assetComboBox.getSelectedItem(),
                    (String) granularityComboBox.getSelectedItem(),
                    startDatePicker.getDateTimePermissive(),
                    endDatePicker.getDateTimePermissive()
            );

            mainPage.createCharts();

        } catch (ValidationException | ApiException | NetworkException | DataParsingException e) {
            LoggerUtil.logInfo("Search interrupted: " + e.getMessage());
            new FloatingMessage(e.getMessage(), searchButton, FloatingMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            LoggerUtil.logError("SearchPanel", "Unexpected exception: " + e.getMessage());
            new FloatingMessage("An unexpected error occurred. Check system logs.", searchButton, FloatingMessage.ERROR_MESSAGE);
        }
    }
}