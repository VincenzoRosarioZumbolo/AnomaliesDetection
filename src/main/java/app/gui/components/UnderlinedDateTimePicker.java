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

/**
 * A sleek, flat-designed version of the {@link DateTimePicker} widget.
 * <p>
 * This composite picker strips default input framing in favor of minimalist bottom-underline fields.
 * It sets explicit calendar color matrices matching the app design language, enforces a standard
 * 24-hour display layout format (HH:mm), and adjusts calendar/time popup button states.
 * </p>
 *
 * @see DateTimePicker
 * @see DatePickerSettings
 * @see TimePickerSettings
 */
public class UnderlinedDateTimePicker extends DateTimePicker {

    /**
     * Constructs a pre-configured, styled date-time picker preset to an English formatting locale.
     */
    public UnderlinedDateTimePicker() {
        super(createDateSettings(), createTimeSettings());
        initStyle();
    }

    /**
     * Constructs default layout variables for the dropdown date picker popup grid.
     *
     * @return a configured {@link DatePickerSettings} layout context map
     */
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

    /**
     * Constructs operational settings for time menus, locking input patterns to 24-hour clocks.
     *
     * @return a configured {@link TimePickerSettings} layout block
     */
    private static TimePickerSettings createTimeSettings() {
        TimePickerSettings settings = new TimePickerSettings(Locale.ENGLISH);

        settings.setFormatForDisplayTime("HH:mm");
        settings.setFormatForMenuTimes("HH:mm");

        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextValidTime, Color.DARK_GRAY);

        return settings;
    }

    /**
     * Applies corporate style adjustments across parent panels, embedded sub-pickers,
     * inputs, and toggles.
     */
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

    /**
     * Strips a text field down and maps an underline matte layout border that shifts colors interactively on focus.
     *
     * @param field the input {@link JTextField} instance to target
     */
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

    /**
     * Alters popup control toggle structures into transparent icons utilizing pointer hand cursors.
     *
     * @param button the trigger {@link JButton} component to target
     */
    private void styleButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}