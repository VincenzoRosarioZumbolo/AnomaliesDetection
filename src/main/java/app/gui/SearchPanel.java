package app.gui;

import app.controller.Controller;
import app.exception.*;
import app.gui.components.*;
import app.gui.style.PaddingConstants;
import app.util.LoggerUtil;
import com.github.lgooddatepicker.components.DateTimePicker;

import javax.swing.*;
import java.awt.*;
import java.security.InvalidParameterException;

public class SearchPanel extends CardPanel {

    private final MainPage mainPage;

    private JComboBox<String> assetComboBox;
    private static final String[] ASSETS = {"Bitcoin", "S&P 500", "Gold", "Oil", "USD index"};

    private JComboBox<String> granularityComboBox;
    private static final String[] GRANULARITIES = {"Minutely", "Hourly", "Daily"};

    private DateTimePicker startDatePicker;
    private DateTimePicker endDatePicker;

    private JButton searchButton;

    public SearchPanel(MainPage mainPage) {

        this.mainPage = mainPage;

        addAssetComboBox();
        addGranularityComboBox();
        addStartDatePicker();
        addEndDatePicker();
        addSearchButton();

        this.setVisible(true);
    }

    private void addAssetComboBox() {

        assetComboBox = new UnderlinedComboBox<>(ASSETS);
        assetComboBox.setSelectedIndex(0);
        assetComboBox.setVisible(true);

        this.add(new LabeledComponent("Asset:", assetComboBox), new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addGranularityComboBox() {

        granularityComboBox = new UnderlinedComboBox<>(GRANULARITIES);
        granularityComboBox.setSelectedIndex(2);
        granularityComboBox.setVisible(true);

        this.add(new LabeledComponent("Granularity:", granularityComboBox), new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addStartDatePicker() {

        startDatePicker = new UnderlinedDateTimePicker();

        add(new LabeledComponent("Start date:", startDatePicker), new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addEndDatePicker() {

        endDatePicker = new UnderlinedDateTimePicker();

        add(new LabeledComponent("End date:", endDatePicker), new GridBagConstraints(1, 1, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_STANDARD, 0, 0));
    }

    private void addSearchButton() {

        searchButton = new PrimaryButton("SEARCH");
        searchButton.addActionListener(e -> search());
        searchButton.setVisible(true);

        this.add(searchButton, new GridBagConstraints(0, 2, 2, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, PaddingConstants.PADDING_LARGE, 0, 0));
    }

    private void search() {

        try {

            Controller.getInstance().searchData(
                    (String) assetComboBox.getSelectedItem(),
                    (String) granularityComboBox.getSelectedItem(),
                    startDatePicker.getDateTimePermissive(),
                    endDatePicker.getDateTimePermissive()
            );

            mainPage.createCharts();

        } catch (ValidationException | ApiRequestException | NetworkException | DataParsingException e) {

            LoggerUtil.logInfo("Search interrupted: " + e.getMessage());
            new FloatingMessage(e.getMessage(), searchButton, FloatingMessage.ERROR_MESSAGE);

        } catch (Exception e) {

            LoggerUtil.logError("SearchPanel", "Unexpected exception: " + e.getMessage());
            new FloatingMessage("An unexpected error occurred. Check system logs.", searchButton, FloatingMessage.ERROR_MESSAGE);
        }
    }
}
