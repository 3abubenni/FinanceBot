package org.dubna.bot.message;

import org.dubna.bot.callback.enums.StatisticPeriod;
import org.dubna.bot.callback.impl.ChangePeriodStatisticCallback;
import org.dubna.bot.callback.impl.ChangeStatisticOffsetCallback;
import org.dubna.budget.statistic.OperationsStatistic;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Formats and displays financial statistics in a Telegram message.
 */
public class StatisticMessage extends SendMessage {

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("ru"));

    private static final DateTimeFormatter DAY_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru"));

    private static final DateTimeFormatter WEEK_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final DecimalFormat CURRENCY_FORMATTER =
            new DecimalFormat("#,##0.00");

    public StatisticMessage(Long chatId, OperationsStatistic stats) {
        this(chatId, stats, 0, StatisticPeriod.MONTH);
    }

    public StatisticMessage(Long chatId,
                            OperationsStatistic stats,
                            int offset,
                            StatisticPeriod period) {
        this.setChatId(chatId.toString());
        this.setText(formatStatistics(stats, period));
        this.setReplyMarkup(createButtons(offset, period));
        this.enableMarkdown(true);
    }

    public EditMessageText editMessage(Integer messageId) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(this.getChatId());
        editMessage.setParseMode(this.getParseMode());
        editMessage.setText(getText());
        editMessage.setReplyMarkup((InlineKeyboardMarkup) this.getReplyMarkup());
        editMessage.setMessageId(messageId);
        return editMessage;
    }

    private String formatStatistics(OperationsStatistic stats, StatisticPeriod period) {
        StringBuilder sb = new StringBuilder();

        sb.append("*📊 Статистика операций*\n");
        sb.append("*Период:* ").append(formatPeriod(stats, period)).append("\n\n");

        sb.append("*Доходы:* `").append(formatCurrency(stats.getIncome())).append("`\n");
        sb.append("*Расходы:* `").append(formatCurrency(stats.getExpense())).append("`\n");
        sb.append("*Итог:* `").append(formatCurrency(stats.getTotal())).append("`\n\n");

        if (stats.getAnalyzes() != null && !stats.getAnalyzes().isEmpty()) {
            sb.append("*📈 Детализация по категориям:*\n");
            stats.getAnalyzes().forEach(category -> {
                String changeWithSign = (category.change() >= 0 ? "+" : "") +
                        formatCurrency(category.change());
                sb.append("• *").append(category.name())
                        .append("*: `").append(changeWithSign).append("`\n");
            });
        } else {
            sb.append("_Нет данных по категориям за выбранный период_\n");
        }

        String totalEmoji = stats.getTotal() >= 0 ? "✅" : "⚠️";
        sb.append("\n").append(totalEmoji).append(" *Общий результат: `")
                .append(formatCurrency(stats.getTotal())).append("`*");

        return sb.toString();
    }

    private String formatPeriod(OperationsStatistic stats, StatisticPeriod period) {
        return switch (period) {
            case DAY ->
                    stats.getFrom().format(DAY_FORMATTER);
            case MONTH ->
                    stats.getFrom().format(MONTH_FORMATTER);
            default ->
                    stats.getFrom().format(WEEK_FORMATTER) + " - " +
                            stats.getTo().format(WEEK_FORMATTER);
        };
    }

    private InlineKeyboardMarkup createButtons(int offset, StatisticPeriod period) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (offset == 0) {
            rows.add(List.of(
                    createOffsetButton(offset - 1, period, "<"),
                    createOffsetButton(offset + 1, period, ">")
            ));
        } else {
            rows.add(List.of(
                    createOffsetButton(offset - 1, period, "<"),
                    createOffsetButton(0, period, "Текущий период"),
                    createOffsetButton(offset + 1, period, ">")
            ));
        }

        List<InlineKeyboardButton> periodButtons = new ArrayList<>();
        if (period != StatisticPeriod.DAY) {
            periodButtons.add(createPeriodButton(StatisticPeriod.DAY, "День"));
        }

        if (period != StatisticPeriod.WEEK) {
            periodButtons.add(createPeriodButton(StatisticPeriod.WEEK, "Неделя"));
        }

        if (period != StatisticPeriod.MONTH) {
            periodButtons.add(createPeriodButton(StatisticPeriod.MONTH, "Месяц"));
        }
        rows.add(periodButtons);

        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardButton createPeriodButton(StatisticPeriod period, String text) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(new ChangePeriodStatisticCallback(period).toJson());
        return btn;
    }

    private InlineKeyboardButton createOffsetButton(int offset,
                                                    StatisticPeriod period,
                                                    String text) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(new ChangeStatisticOffsetCallback(offset, period).toJson());
        return btn;
    }

    private String formatCurrency(double amount) {
        return CURRENCY_FORMATTER.format(amount);
    }
}
