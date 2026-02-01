package org.dubna.bot;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "tg-bot")
public interface BotConfig {

    String token();

    String name();

}
