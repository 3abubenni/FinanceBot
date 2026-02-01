package org.dubna.bot;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dubna.user.UserContextHolder;
import org.dubna.user.UserEntity;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

@Slf4j
@Startup
@ApplicationScoped
public class TgBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final TgCommandHandler commandHandler;

    private final TgMessageHandler messageHandler;

    private final TgCallbackHandler callbackHandler;

    @Inject
    public TgBot(final BotConfig config,
                 final TgCommandHandler commandHandler,
                 final TgMessageHandler messageHandler,
                 final TgCallbackHandler callbackHandler) {
        super(config.token());
        this.config = config;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
            log.info("Bot '{}' is ready", config.name());
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void onUpdateReceived(Update update) {
        log.info("Received update: {}", update);
        UserEntity user = getTgUserFromUpdate(update).map(UserEntity::saveTgUser)
                .orElse(null);

        if (user != null) {
            UserContextHolder.set(user);
            try {
                if (commandHandler.execute(update, this)) {
                    log.info("Command executed successfully");

                } else if (update.hasMessage()) {
                    Message message = update.getMessage();
                    messageHandler.handle(message, this);
                } else if (update.hasCallbackQuery()) {
                    CallbackQuery callbackQuery = update.getCallbackQuery();
                    callbackHandler.handle(callbackQuery, this);
                }
            } finally {
                UserContextHolder.remove();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.name();
    }

    private Optional<User> getTgUserFromUpdate(final Update update) {
        if (update.hasMessage()) {
            return Optional.of(update.getMessage().getFrom());
        }

        if (update.hasCallbackQuery()) {
            return Optional.of(update.getCallbackQuery().getFrom());
        }

        return Optional.empty();
    }

}
