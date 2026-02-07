package org.dubna.bot.callback.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CallbackType {

    SET_CATEGORY(0),
    CHANGE_CATEGORY(1),
    CHANGE_STATISTIC_OFFSET(2),
    CHANGE_STATISTIC_PERIOD(3);

    @JsonValue
    public final int value;

    CallbackType(int value) {
        this.value = value;
    }

}
