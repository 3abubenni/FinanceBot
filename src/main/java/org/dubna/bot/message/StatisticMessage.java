package org.dubna.bot.message;

import org.dubna.budget.statistic.CategoryStatistic;
import org.dubna.budget.statistic.OperationsStatistic;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class StatisticMessage extends SendMessage {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("#,##0.00");

    public StatisticMessage(Long chatId, OperationsStatistic stats) {
        this.setChatId(chatId.toString());
        this.setText(formatStatistics(stats));
        this.enableMarkdown(true);
    }

    private String formatStatistics(OperationsStatistic stats) {
        StringBuilder sb = new StringBuilder();


        sb.append("*📊 Статистика операций*\n");
        sb.append("*Период:* ")
                .append(stats.getFrom().format(DATE_FORMATTER))
                .append(" - ")
                .append(stats.getTo().format(DATE_FORMATTER))
                .append("\n\n");

        sb.append("*Доходы:* `").append(formatCurrency(stats.getIncome())).append("`\n");
        sb.append("*Расходы:* `").append(formatCurrency(stats.getExpense())).append("`\n");
        sb.append("*Итог:* `").append(formatCurrency(stats.getTotal())).append("`\n\n");

        if (stats.getAnalyzes() != null && !stats.getAnalyzes().isEmpty()) {
            sb.append("*📈 Детализация по категориям:*\n");

            for (CategoryStatistic category : stats.getAnalyzes()) {
                String changeFormatted = formatCurrency(category.change());
                String changeWithSign = category.change() >= 0 ?
                        "+" + changeFormatted : changeFormatted;

                sb.append("• *")
                        .append(category.name())
                        .append("*: `")
                        .append(changeWithSign)
                        .append("`\n");
            }
        } else {
            sb.append("_Нет данных по категориям за выбранный период_\n");
        }

        String totalEmoji = stats.getTotal() >= 0 ? "✅" : "⚠️";
        sb.append("\n").append(totalEmoji).append(" *Общий результат: `")
                .append(formatCurrency(stats.getTotal())).append("`*");

        return sb.toString();
    }

    private String formatCurrency(double amount) {
        return CURRENCY_FORMATTER.format(amount);
    }

}
