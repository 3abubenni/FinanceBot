package org.dubna.bot.callback.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import org.dubna.bot.callback.Callback;
import org.dubna.bot.callback.impl.ChangePeriodStatisticCallback;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Function;

/**
 * Represents time periods for statistical reporting with date range calculations.
 * Each period can calculate its start and end dates based on an offset from current date.
 */
public enum StatisticPeriod {

    DAY(0,
            offset -> LocalDate.now().plusDays(offset),
            offset -> LocalDate.now().plusDays(offset)),

    WEEK(1,
            offset -> LocalDate.now().plusWeeks(offset).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
            offset -> LocalDate.now().plusWeeks(offset).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    ),

    MONTH(2,
            offset -> LocalDate.now().plusMonths(offset).with(TemporalAdjusters.firstDayOfMonth()),
            offset -> LocalDate.now().plusMonths(offset).with(TemporalAdjusters.lastDayOfMonth())
    );

    public static final InlineKeyboardButton DAY_BTN = createPeriodButton(DAY, "День");
    public static final InlineKeyboardButton WEEK_BTN = createPeriodButton(WEEK, "День");
    public static final InlineKeyboardButton MONTH_BTN = createPeriodButton(MONTH, "Месяц");

    @JsonValue
    private final int value;
    private final Function<Integer, LocalDate> getStart;
    private final Function<Integer, LocalDate> getEnd;

    StatisticPeriod(int value,
                    Function<Integer, LocalDate> getStart,
                    Function<Integer, LocalDate> getEnd) {
        this.value = value;
        this.getStart = getStart;
        this.getEnd = getEnd;
    }

    /**
     * @param offset 0 for current period, positive for future, negative for past
     */
    public LocalDate getStart(int offset) {
        return getStart.apply(offset);
    }

    /**
     * @param offset 0 for current period, positive for future, negative for past
     */
    public LocalDate getEnd(int offset) {
        return getEnd.apply(offset);
    }

    private static InlineKeyboardButton createPeriodButton(StatisticPeriod period, String text) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        Callback callback = new ChangePeriodStatisticCallback(period);
        btn.setCallbackData(callback.toJson());
        return btn;
    }

}