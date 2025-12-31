package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MaesterOfWesterosBot implements LongPollingSingleThreadUpdateConsumer {
    private String bot_token = ConfigurationSingleton.getInstance().getProperty("BOT_TOKEN");
    private TelegramClient telegramClient = new OkHttpTelegramClient(bot_token);

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            // comando = prima parola (es: /start)
            String command = text.split(" ")[0];

        }
    }
}