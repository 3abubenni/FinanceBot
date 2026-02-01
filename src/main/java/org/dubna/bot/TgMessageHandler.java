package org.dubna.bot;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.SneakyThrows;
import org.dubna.bot.message.OperationMessage;
import org.dubna.bot.message.UserErrorOperationMessage;
import org.dubna.budget.Budget;
import org.dubna.budget.Operation;
import org.dubna.category.Category;
import org.dubna.budget.OperationType;
import org.dubna.user.UserContextHolder;
import org.dubna.user.UserEntity;
import org.dubna.util.Pair;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Message;

@ApplicationScoped
public class TgMessageHandler {

    @SneakyThrows
    public void handle(Message message, DefaultAbsSender sender) {
        Pair<String, Pair<OperationType, Float>> keywordAndChange = parseKeywordAndChange(message.getText());
        String keyword = keywordAndChange.first();
        if (keywordAndChange.second() == null) {
            UserErrorOperationMessage errorMessage = new UserErrorOperationMessage(
                    message.getChatId()
            );
            sender.execute(errorMessage);
            return;
        }

        Float change = keywordAndChange.second().second();
        OperationType operationType = keywordAndChange.second().first();

        UserEntity user = UserContextHolder.getOrThrow();
        Budget budget = Budget.findByCreatorId(user.getId());
        Operation operation = keyword == null ?
                budget.createOperation(change, operationType) :
                budget.createOperation(keyword, change, operationType);

        OperationMessage operationCreatedMessage = new OperationMessage(
                message.getChatId(),
                operation
        );

        if (operation.getCategory() == null) {
            operationCreatedMessage = operationCreatedMessage
                    .withCategoriesButtons(Category.findByUserId(user.getId()));
        } else {
            operationCreatedMessage = operationCreatedMessage.withChangeCategoryButton();
        }

        sender.execute(operationCreatedMessage);
    }

    private boolean isFloat(String str) {
        return str != null && str.matches("([-+])?\\d+(\\.\\d+)?");
    }

    private Pair<String, Pair<OperationType, Float>> parseKeywordAndChange(String text) {
        String[] parts = text.replaceAll("\\s+", " ").split(" ");
        if (parts.length == 1) {
            String part = parts[0].replace(",", ".");
            if (isFloat(part)) {
                Float f = Float.parseFloat(part);
                OperationType operationType = detectOperationType(part);
                return new Pair<>(null, new Pair<>(operationType, f));
            }
            return new Pair<>(null, null);
        }

        if (parts.length == 2) {
            String part1 = parts[0].replace(",", ".");
            String part2 = parts[1].replace(",", ".");
            if (isFloat(part1)) {
                Float change = Float.parseFloat(part1);
                OperationType operationType = detectOperationType(part1);
                return new Pair<>(part2, new Pair<>(operationType, change));
            }

            if (isFloat(part2)) {
                Float change = Float.parseFloat(part2);
                OperationType operationType = detectOperationType(part2);
                return new Pair<>(part1, new Pair<>(operationType, change));
            }
        }

        return new Pair<>(null, null);
    }

    private OperationType detectOperationType(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        return switch (str.charAt(0)) {
            case '+' -> OperationType.DEBIT;
            case '-' -> OperationType.TOPUP;
            default -> null;
        };
    }

}
