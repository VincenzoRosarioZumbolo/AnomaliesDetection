package app.gui.components;

import app.gui.style.AppColors;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;

public class UnderlinedDateTimePicker extends DateTimePicker {

    public UnderlinedDateTimePicker() {

        super(createDateSettings(), createTimeSettings());
        initStyle();
    }

    private static DatePickerSettings createDateSettings() {

        DatePickerSettings settings = new DatePickerSettings(Locale.ENGLISH);

        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, Color.WHITE);
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, Color.WHITE);
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, Color.WHITE);
        settings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, Color.WHITE);
        settings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, Color.WHITE);

        settings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, AppColors.PRIMARY_HOVER);
        settings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, Color.WHITE);
        settings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, Color.DARK_GRAY);
        settings.setColor(DatePickerSettings.DateArea.TextTodayLabel, AppColors.PRIMARY_HOVER);
        settings.setColor(DatePickerSettings.DateArea.TextClearLabel, AppColors.DANGER);

        settings.setBorderCalendarPopup(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        return settings;
    }

    private static TimePickerSettings createTimeSettings() {

        TimePickerSettings settings = new TimePickerSettings(Locale.ENGLISH);

        settings.setFormatForDisplayTime("HH:mm");
        settings.setFormatForMenuTimes("HH:mm");

        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextValidTime, Color.DARK_GRAY);

        return settings;
    }

    private void initStyle() {

        this.setOpaque(false);
        this.setBackground(AppColors.EMPTY);

        this.getDatePicker().setOpaque(false);
        this.getDatePicker().setBackground(AppColors.EMPTY);
        this.getTimePicker().setOpaque(false);
        this.getTimePicker().setBackground(AppColors.EMPTY);

        styleField(getDatePicker().getComponentDateTextField());
        styleField(getTimePicker().getComponentTimeTextField());

        styleButton(getDatePicker().getComponentToggleCalendarButton());
        styleButton(getTimePicker().getComponentToggleTimeMenuButton());
    }

    private void styleField(JTextField field) {

        field.setOpaque(false);
        field.setBackground(AppColors.EMPTY);
        field.putClientProperty("JComponent.focusWidth", 0);
        field.setBorder(new MatteBorder(0, 0, 1, 0, AppColors.TEXT));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 1, 0, AppColors.PRIMARY));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new MatteBorder(0, 0, 1, 0, AppColors.TEXT));
            }
        });
    }

    private void styleButton(JButton button) {

        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}