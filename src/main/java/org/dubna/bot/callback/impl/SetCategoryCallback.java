package org.dubna.bot.callback.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dubna.bot.callback.Callback;
import org.dubna.bot.callback.enums.CallbackType;
import org.dubna.bot.message.OperationMessage;
import org.dubna.budget.Operation;
import org.dubna.category.Category;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class SetCategoryCallback extends Callback {

    @JsonProperty("cid")
    private long categoryId;

    @JsonProperty("oid")
    private long operationId;

    public SetCategoryCallback(long categoryId, long operationId) {
        super(CallbackType.SET_CATEGORY);
        this.categoryId = categoryId;
        this.operationId = operationId;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void execute(
            CallbackQuery callbackQuery,
            DefaultAbsSender sender
    ) {
        log.info("Set category id '{}' for operation with id '{}'", categoryId, operationId);
        Operation o = Operation.findById(operationId);
        Category category = Category.findById(categoryId);

        String keyword = o.getKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            category.addKeyword(keyword);
        }

        o.updateCategory(category);
        if (o.getType() == null) {
            o.updateType(category.getType());
        }

        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        EditMessageText editMessage = new OperationMessage(chatId, o)
                .withChangeCategoryButton()
                .editMessage(messageId);

        sender.execute(editMessage);
    }

}
