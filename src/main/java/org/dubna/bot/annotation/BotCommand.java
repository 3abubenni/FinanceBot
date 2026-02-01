package org.dubna.bot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as a Telegram bot command handler.
 *
 * <p>The annotated method must have one of the following signatures:
 * <ul>
 * <li>{@code (Update update, DefaultAbsSender sender)} - for command with bot instance</li>
 * <li>{@code (Update update)} - for simple command handling</li>
 * <li>{@code (Message message, DefaultAbsSender sender)} - for direct message handling</li>
 * <li>{@code (CallbackQuery callbackQuery, DefaultAbsSender sender)} - for inline button callbacks</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * @BotCommand("/start")
 * public void handleStart(Update update, DefaultAbsSender sender) {
 *     // Command logic here
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotCommand {

    /**
     * The command name (e.g., "/start", "/help").
     * Should start with '/' character for Telegram commands.
     *
     * @return command name
     */
    String value();

    /**
     * Command description for Telegram's /help command.
     * This will be shown when user calls /help command.
     *
     * @return command description
     */
    String description() default "";

}