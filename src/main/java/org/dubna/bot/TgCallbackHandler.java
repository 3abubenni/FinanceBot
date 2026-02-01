package org.dubna.bot;

import jakarta.enterprise.context.ApplicationScoped;
import org.dubna.bot.callback.Callback;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@ApplicationScoped
public class TgCallbackHandler {

    public void handle(CallbackQuery query, DefaultAbsSender sender) {
        String data = query.getData();
        Callback callback = Callback.fromJson(data);
        callback.execute(query, sender);
    }

}
