package org.dubna.bot.message;

import org.dubna.bot.callback.Callback;
import org.dubna.bot.callback.impl.ChangeCategoryCallback;
import org.dubna.bot.callback.impl.SetCategoryCallback;
import org.dubna.budget.Operation;
import org.dubna.budget.OperationType;
import org.dubna.category.Category;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class OperationMessage extends SendMessage {

    private final Operation operation;

    private List<Category> categories;

    private boolean hasChangeCategoryBtn = false;

    public OperationMessage(Long chatId, Operation o) {
        setChatId(chatId);

        StringBuilder sb = new StringBuilder();
        sb.append("💸 *Операция создана!*\n\n");

        sb.append("Сумма: `");
        if (o.getType() == OperationType.DEBIT) {
            sb.append(o.getType().sign);
        }
        sb.append(o.getChange()).append("`\n");

        if (o.getCategory() == null) {
            sb.append("\n❓ *Категория не определена*\n");
            if (o.getKeyword() != null) {
                sb.append("Ключевое слово: _\"").append(o.getKeyword()).append("\"_\n");
            }
        } else {
            sb.append("\n✅ Категория: *").append(o.getCategory().getName()).append("*");
        }

        setText(sb.toString());
        enableMarkdown(true);

        this.operation = o;
    }

    public OperationMessage withChangeCategoryButton() {
        setReplyMarkup(changeCategoryButton());
        hasChangeCategoryBtn = true;
        return this;
    }

    public OperationMessage withCategoriesButtons(List<Category> categories) {
        this.categories = categories;
        setText(getText() + "\n👇 Выберите категорию:");
        setReplyMarkup(categoriesButtons(categories, operation));
        return this;
    }

    public EditMessageText editMessage(Integer messageId) {
        EditMessageText editMessage = new EditMessageText();

        editMessage.setChatId(this.getChatId());
        editMessage.setMessageId(messageId);
        editMessage.setText(this.getText());
        editMessage.enableMarkdown(true);

        if (this.getReplyMarkup() != null && categories != null) {
            editMessage.setReplyMarkup(categoriesButtons(categories, operation));
        }

        if (hasChangeCategoryBtn) {
            editMessage.setReplyMarkup(changeCategoryButton());
        }

        return editMessage;
    }

    private InlineKeyboardMarkup changeCategoryButton() {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText("\uD83D\uDD04 Изменить категорию");
        Callback callback = new ChangeCategoryCallback(operation.getId());
        btn.setCallbackData(callback.toJson());
        return new InlineKeyboardMarkup(List.of(List.of(btn)));
    }

    private InlineKeyboardMarkup categoriesButtons(List<Category> categories, Operation o) {
        List<List<InlineKeyboardButton>> btns = new ArrayList<>();
        categories.forEach(category -> {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            SetCategoryCallback callback = new SetCategoryCallback(category.getId(), o.getId());
            btn.setCallbackData(callback.toJson());
            btn.setText(category.getName());
            btns.add(List.of(btn));
        });

        return new InlineKeyboardMarkup(btns);
    }

}
