package org.dubna.bot.callback.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dubna.bot.callback.Callback;
import org.dubna.bot.callback.enums.CallbackType;
import org.dubna.bot.callback.enums.StatisticPeriod;
import org.dubna.bot.message.StatisticMessage;
import org.dubna.budget.statistic.OperationsStatistic;
import org.dubna.budget.statistic.StatisticRepository;
import org.dubna.user.UserContextHolder;
import org.dubna.user.UserEntity;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

@Slf4j
@NoArgsConstructor
public class ChangePeriodStatisticCallback extends Callback {

    @JsonProperty("p")
    private StatisticPeriod period;

    public ChangePeriodStatisticCallback(StatisticPeriod period) {
        super(CallbackType.CHANGE_STATISTIC_PERIOD);
        this.period = period;
    }

    @Override
    @SneakyThrows
    public void execute(CallbackQuery query, DefaultAbsSender sender) {
        log.info("Change period to '{}'", period);
        final int offset = 0;
        LocalDate start = period.getStart(offset);
        LocalDate end = period.getEnd(offset);
        UserEntity user = UserContextHolder.getOrThrow();
        Integer messageId = query.getMessage().getMessageId();
        OperationsStatistic statistic = StatisticRepository.getInstance().getAnalyze(user.getId(), start, end);
        EditMessageText message = new StatisticMessage(
                query.getMessage().getChatId(),
                statistic,
                offset,
                period
        ).editMessage(messageId);
        sender.execute(message);
    }

}
