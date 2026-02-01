package org.dubna.bot;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dubna.bot.annotation.BotCommand;
import org.dubna.bot.message.StatisticMessage;
import org.dubna.budget.statistic.OperationsStatistic;
import org.dubna.budget.statistic.StatisticRepository;
import org.dubna.user.UserContextHolder;
import org.dubna.user.UserEntity;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@ApplicationScoped
public class TgCommandHandlerImpl extends TgCommandHandler {

    private final StatisticRepository statisticRepository;

    @Inject
    public TgCommandHandlerImpl(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    @SneakyThrows
    @BotCommand("/start")
    public void startCommand(Update update, DefaultAbsSender sender) {
        SendMessage message = new SendMessage();
        message.setText("""
                📊 Привет! Это бот для учёта финансов — просто, быстро, понятно.
                
                *Он поможет вам:*
                ✅ *Записывать доходы и расходы* (например: “кофе 300”, “ЗП 50000”)
                ✅ *Сортировать по категориям* (еда, транспорт, здоровье, доходы)
                ✅ *Смотреть аналитику* — сколько потратили за месяц, на что уходят деньги
                ✅ *Контролировать свои финансы без лишней сложности*
                
                *Что можно сделать прямо сейчас:*
                
                    Добавить операцию — напишите, например:
                    5000 ЗП или
                    300 кофе
                
                    Указывать знак (+ или -) не обязательно, это может определится в зависимости
                    от категории.
                
                    Посмотреть статистику за месяц — командой /stats
                
                    Настроить категории — /categories
                
                Начните с простого — добавьте свою первую запись! 💸
                """);
        message.enableMarkdown(true);
        message.setChatId(update.getMessage().getChatId());
        sender.execute(message);
    }

    @SneakyThrows
    @BotCommand("/statistic")
    public void statisticCommand(Update update, DefaultAbsSender sender) {
        UserEntity user = UserContextHolder.getOrThrow();
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = today.with(TemporalAdjusters.lastDayOfMonth());

        OperationsStatistic stats = statisticRepository.getAnalyze(user.getId(), start, end);
        SendMessage message = new StatisticMessage(update.getMessage().getChatId(), stats);
        sender.execute(message);
    }

}
