package org.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

public class MaesterOfWesterosBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final APIRequests api = new APIRequests();

    public MaesterOfWesterosBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        setupCommands();
    }

    @Override
    public void consume(Update update) {
        // Chiamata dei metodi per ogni comando
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            // comando = prima parola (es: /start)
            String command = text.split(" ")[0];

            switch (command.toLowerCase()) {
                case "/start":
                    handleStart(update);
                    break;
                case "/help":
                    handleHelp(update);
                    break;
                case "/character":
                    handleCharacter(update);
                    break;
                case "/fav":
                    handleFavorite(update);
                    break;
                default:
                    handleUnknown(update);
                    break;
            }
        }
    }

    // Funzione per mandare un messaggio
    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("Markdown")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Funzione per il setup dell'interfaccia menù di telegram con la lista dei comandi
    private void setupCommands() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Avvia il bot"),
                new BotCommand("/help", "Mostra il menù"),
                new BotCommand("/character", "Cerca un personaggio"),
                new BotCommand("/fav", "Gestisci i preferiti")
        );

        SetMyCommands setCommands = new SetMyCommands(commands);

        try {
            telegramClient.execute(setCommands);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Funzione per il parsing del comando
    private String getCommandArgument(String text) {
        String[] parts = text.split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : null;
    }

    // Funzione per gestire la risposta ad un comando non riconosciuto
    private void handleUnknown(Update update) {
        long chatId = update.getMessage().getChatId();
        sendMessage(chatId, "❓ Comando non riconosciuto. Usa /help per vedere i comandi disponibili.");
    }

    // Funzione per gestire ricerce di personaggi inesistenti/non trovati
    private String safe(String value) {
        return (value == null || value.isBlank()) ? "Sconosciuto" : value;
    }

    // Funzione per mandare la foto ricavata dall'API
    private void sendPhoto(long chatId, String imageUrl) {
        SendPhoto photo = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(new InputFile(imageUrl))
                .build();

        try {
            telegramClient.execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Funzione per il comando /start
    private void handleStart(Update update) {
        long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getChat().getFirstName();
        String message = """
            🐉 Benvenuto, viandante!

            Sono il *Maester di Westeros* 📜
            Posso raccontarti storie e informazioni sui personaggi di *Game of Thrones*.

            📌 Cosa posso fare:
            • Cercare un personaggio
            • Mostrarti i membri di una casata
            • Suggerirti un personaggio casuale

            Digita /help per vedere tutti i comandi disponibili.
            """;
        sendMessage(chatId, message);

        // Log comando
        try {
            Database db = Database.getInstance();
            int userId = db.getOrCreateUser(chatId,
                    update.getMessage().getChat().getUserName(),
                    firstName,
                    update.getMessage().getChat().getLastName());
            db.logCommand(userId, "/start", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funzione per il comando /help
    private void handleHelp(Update update) {
        long chatId = update.getMessage().getChatId();

        String message = """
        📜 *Comandi disponibili*

        /start – Avvia il bot
        /help – Mostra questo menù
        /character <nome> – Cerca un personaggio
        /fav add <nome> – Aggiungi un personaggio ai preferiti
        /fav list – Mostra i tuoi preferiti
        /fav remove <nome> – Rimuovi un preferito

        🧭 *Esempi*
        • /character Jon Snow
        • /fav add Arya Stark
        • /fav list
        """;

        sendMessage(chatId, message);

        // Log comando
        try {
            Database db = Database.getInstance();
            int userId = db.getOrCreateUser(chatId,
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getChat().getFirstName(),
                    update.getMessage().getChat().getLastName());
            db.logCommand(userId, "/help", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funzione per il comando /character
    private void handleCharacter(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        String nome = getCommandArgument(text);

        if (nome == null || nome.isBlank()) {
            sendMessage(chatId, "❌ Devi specificare un nome.\nEsempio:\n/character Jon Snow");
            return;
        }

        Character character = api.cercaCharacter(nome);

        if (character == null) {
            sendMessage(chatId, "😕 Nessun personaggio trovato con il nome *" + nome + "*");
            return;
        }

        String message = """
            👤 *%s*
            
            🏷 *Soprannome:* %s
            🏰 *Casata:* %s
            """.formatted(
                character.getFullName(),
                safe(character.getTitle()),
                safe(character.getFamily())
        );

        sendMessage(chatId, message);

        // invio immagine se presente
        if (character.getImageUrl() != null && !character.getImageUrl().isBlank()) {
            sendPhoto(chatId, character.getImageUrl());
        }

        // Log comando
        try {
            Database db = Database.getInstance();
            int userId = db.getOrCreateUser(chatId,
                    update.getMessage().getChat().getUserName(),
                    update.getMessage().getChat().getFirstName(),
                    update.getMessage().getChat().getLastName());
            db.logCommand(userId, "/character", nome);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funzione per il comando /fav
    // Funzione per il comando /fav
    private void handleFavorite(Update update) {

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String argument = getCommandArgument(text);

        if (argument == null || argument.isBlank()) {
            sendMessage(chatId,
                    "❌ Devi specificare un sottocomando: add, list o remove.\n" +
                            "Esempio:\n/fav add Arya Stark");
            return;
        }

        String[] parts = argument.split(" ", 2);
        String subCommand = parts[0].toLowerCase();
        String param = parts.length > 1 ? parts[1].trim() : null;

        try {
            Database db = Database.getInstance();

            long telegramId = update.getMessage().getChat().getId();
            String username = update.getMessage().getChat().getUserName();
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();

            int userId = db.getOrCreateUser(telegramId, username, firstName, lastName);

            /* ================= ADD ================= */
            if (subCommand.equals("add")) {

                if (param == null || param.isBlank()) {
                    sendMessage(chatId, "❌ Devi specificare il nome del personaggio da aggiungere.");
                    return;
                }

                Character character = api.cercaCharacter(param);

                if (character == null) {
                    sendMessage(chatId, "❌ Personaggio \"" + param + "\" non trovato.");
                    return;
                }

                int characterId = db.getOrCreateCharacter(character);
                boolean added = db.addFavorite(userId, characterId);

                if (added) {
                    sendMessage(chatId, "✅ \"" + character.getFullName() + "\" aggiunto ai tuoi preferiti.");
                } else {
                    sendMessage(chatId, "ℹ️ \"" + character.getFullName() + "\" è già tra i tuoi preferiti.");
                }

                db.logCommand(userId, "/fav add", param);
                return;
            }

            /* ================ REMOVE =============== */
            if (subCommand.equals("remove")) {

                if (param == null || param.isBlank()) {
                    sendMessage(chatId, "❌ Devi specificare il nome del personaggio da rimuovere.");
                    return;
                }

                Character character = api.cercaCharacter(param);

                if (character == null) {
                    sendMessage(chatId, "❌ Personaggio \"" + param + "\" non trovato.");
                    return;
                }

                int characterId = db.getOrCreateCharacter(character);
                boolean removed = db.removeFavorite(userId, characterId);

                if (removed) {
                    sendMessage(chatId, "✅ \"" + character.getFullName() + "\" rimosso dai tuoi preferiti.");
                } else {
                    sendMessage(chatId, "ℹ️ \"" + character.getFullName() + "\" non era tra i tuoi preferiti.");
                }

                db.logCommand(userId, "/fav remove", param);
                return;
            }

            /* ================= LIST ================ */
            if (subCommand.equals("list")) {

                List<Character> favorites = db.getUserFavorites(userId);

                if (favorites.isEmpty()) {
                    sendMessage(chatId, "ℹ️ Non hai ancora preferiti.");
                    return;
                }

                StringBuilder message = new StringBuilder("⭐ I tuoi preferiti:\n\n");

                for (Character c : favorites) {
                    message.append("• ")
                            .append(c.getFullName())
                            .append(" (")
                            .append(safe(c.getFamily()))
                            .append(")\n");
                }

                sendMessage(chatId, message.toString());
                db.logCommand(userId, "/fav list", null);
                return;
            }

            /* ============== COMANDO ERRATO ============== */
            sendMessage(chatId, "❌ Sottocomando non riconosciuto. Usa: add, remove, list");

        } catch (Exception e) {
            sendMessage(chatId, "❌ Errore durante la gestione dei preferiti.");
            e.printStackTrace();
        }
    }
}