package org.dubna.bot.callback.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dubna.bot.callback.Callback;
import org.dubna.bot.callback.enums.CallbackType;
import org.dubna.bot.message.OperationMessage;
import org.dubna.budget.Operation;
import org.dubna.category.Category;
import org.dubna.category.Keyword;
import org.dubna.user.UserContextHolder;
import org.dubna.user.UserEntity;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ChangeCategoryCallback extends Callback {

    @JsonProperty("oid")
    private long operationId;

    public ChangeCategoryCallback(long operationId) {
        super(CallbackType.CHANGE_CATEGORY);
        this.operationId = operationId;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void execute(CallbackQuery query, DefaultAbsSender sender) {
        log.info("Change category for operation with id '{}'", operationId);
        Operation operation = Operation.findById(operationId);
        operation.updateCategory(null);
        operation.updateType(null);

        if (operation.getCategory() != null && operation.getKeyword() != null) {
            Category category = operation.getCategory();
            Keyword.deleteByKeywordAndCategory(operation.getKeyword(), category);
        }

        UserEntity user = UserContextHolder.getOrThrow();
        EditMessageText message = new OperationMessage(query.getMessage().getChatId(), operation)
                .withCategoriesButtons(Category.findByUserId(user.getId()))
                .editMessage(query.getMessage().getMessageId());

        sender.execute(message);
    }

}
