package org.dubna.bot.callback.enums;


import com.fasterxml.jackson.annotation.JsonValue;

public enum StatisticPeriod {

    DAY(0),
    WEEK(1),
    MONTH(2);

    @JsonValue
    private final int value;

    StatisticPeriod(int value) {
        this.value = value;
    }

}
