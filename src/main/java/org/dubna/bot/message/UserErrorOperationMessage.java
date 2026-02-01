package org.dubna.bot.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class UserErrorOperationMessage extends SendMessage {

    public UserErrorOperationMessage(Long chatId) {
        setChatId(chatId);
        StringBuilder sb = new StringBuilder();

        sb.append("❌ *Неверный формат сообщения*\n\n");
        sb.append("Для создания операции обязательно укажите сумму\\!\n\n");
        sb.append("📋 *Примеры правильного ввода:*\n");
        sb.append("```\n");
        sb.append("100 пицца\n");
        sb.append("зарплата 20000\n");
        sb.append("200\n");
        sb.append("ресторан 5000.12\n");
        sb.append("1000 продукты\n");
        sb.append("```\n\n");
        sb.append("💡 *Сумма может быть целым числом или десятичной дробью*\n");
        sb.append("💡 *Название категории необязательно*");

        setText(sb.toString());
        enableMarkdownV2(true);
        disableWebPagePreview();
    }

}
