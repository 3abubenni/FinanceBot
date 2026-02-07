package org.dubna.bot.callback.enums;


import com.fasterxml.jackson.annotation.JsonValue;

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

}