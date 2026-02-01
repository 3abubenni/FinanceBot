package org.dubna.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dubna.bot.annotation.BotCommand;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TG Command handler.
 */
@Slf4j
public abstract class TgCommandHandler {

    private final Map<String, Method> commands = new HashMap<>();

    public TgCommandHandler() {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BotCommand.class)) {
                validateMethod(method);
                BotCommand annotation = method.getAnnotation(BotCommand.class);
                String command = annotation.value();
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                commands.put(command, method);
            }
        }
        log.info("{} is inited", this.getClass().getSimpleName());
    }

    /**
     * Execute a command if message has it.
     *
     * @param update TG update
     * @param sender TG bot
     * @return message has a command.
     */
    @SneakyThrows
    public boolean execute(Update update, DefaultAbsSender sender) {
        String command = extractCommand(update);
        if (command == null) {
            return false;
        }

        if (commands.containsKey(command)) {
            Method method = commands.get(command);
            method.invoke(this, update, sender);
            log.info("User with tgId '{}' invoked '{}' command", update.getMessage().getFrom().getId(), command);
        }

        return true;
    }

    private String extractCommand(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.startsWith("/")) {
                text = text.substring(1);
                return text.split(" ")[0];
            }
        }
        return null;
    }

    private void validateMethod(Method method) {
        Type[] types = method.getGenericParameterTypes();

        if (types.length != 2) {
            throw new IllegalArgumentException(
                    String.format("Method must have exactly 2 parameters, but has %d: %s",
                            types.length, Arrays.toString(types))
            );
        }

        Class<?>[] parameterClasses = method.getParameterTypes();

        boolean isFirstParamUpdate = Update.class.isAssignableFrom(parameterClasses[0]);
        boolean isSecondParamAbsSender = DefaultAbsSender.class.isAssignableFrom(parameterClasses[1]);

        if (!isFirstParamUpdate || !isSecondParamAbsSender) {
            throw new IllegalArgumentException(
                    String.format("Method parameters must be (Update, DefaultAbsSender), but are: %s",
                            Arrays.toString(parameterClasses))
            );
        }
    }

}
