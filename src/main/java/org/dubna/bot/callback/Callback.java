package org.dubna.bot.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NoArgsConstructor;
import org.dubna.bot.callback.enums.CallbackType;
import org.dubna.bot.callback.impl.ChangeCategoryCallback;
import org.dubna.bot.callback.impl.ChangePeriodStatisticCallback;
import org.dubna.bot.callback.impl.ChangeStatisticOffsetCallback;
import org.dubna.bot.callback.impl.SetCategoryCallback;
import org.dubna.util.JsonUtil;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        property = "t"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SetCategoryCallback.class, name = "0"),
        @JsonSubTypes.Type(value = ChangeCategoryCallback.class, name = "1"),
        @JsonSubTypes.Type(value = ChangeStatisticOffsetCallback.class, name = "2"),
        @JsonSubTypes.Type(value = ChangePeriodStatisticCallback.class, name = "3"),
})
public abstract class Callback {

    @JsonProperty("t")
    private CallbackType type;

    public abstract void execute(CallbackQuery callbackQuery, DefaultAbsSender sender);

    public Callback(CallbackType type) {
        this.type = type;
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }

    public static Callback fromJson(String json) {
        return JsonUtil.fromJson(json, Callback.class);
    }

}
