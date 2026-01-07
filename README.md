# MaesterOfWesterosBot

🐉 **Telegram Bot informativo su Game of Thrones**

---

## Descrizione
MaesterOfWesterosBot è un bot Telegram che permette di:

- Cercare personaggi di *Game of Thrones* tramite l'API pubblica [Game of Thrones Character Api](https://thronesapi.com/)
- Visualizzare informazioni dettagliate su ogni personaggio: nome completo, titolo, casata e immagine
- Gestire una lista personale di personaggi preferiti
- Visualizzare statistiche sull'utilizzo dei comandi

Il bot è sviluppato in **Java 21** con **Maven** e utilizza **SQLite** come database locale.

---

## API utilizzata
- [Game of Thrones Character Api](https://thronesapi.com/)
- Nessuna chiave API richiesta

---

## Setup

1. Clonare la repository:

```bash
  git clone https://github.com/ilaaaaaaaa/Meggiolaro_Telegram_Bot.git
```

2. Configurare il token del bot:

   - Copiare config.properties.example in config.properties 
   - Inserire il token del bot:

     |BOT_TOKEN=inserisci_qui_il_token_bot


3. Installare le dipendenze Maven:
```bash

```

4. Avviare il bot:
```bash

```

---

## Comandi disponibili

/start – Avvia il bot

/help – Mostra il menù e i comandi disponibili

/character <nome> – Cerca un personaggio per nome

/fav add <nome> – Aggiunge un personaggio ai preferiti

/fav remove <nome> – Rimuove un personaggio dai preferiti

/fav list – Mostra i tuoi personaggi preferiti

/stats <user|command|recent> – Mostra statistiche sull’uso dei comandi

#### Esempi:

/character Jon Snow
/fav add Arya Stark
/fav list
/stats user

## Database
### Tabelle

users – informazioni sugli utenti (id, telegram_id, username, first_name, last_name, created_at)

characters – informazioni sui personaggi (id, api_id, first_name, last_name, full_name, title, family)

character_images – URL delle immagini dei personaggi (id, character_id, image_url, source)

user_favorites – relazioni utenti-personaggi preferiti (user_id, character_id, created_at)

usage_logs – log dei comandi eseguiti dagli utenti (id, user_id, command, parameter, created_at)

### Schema
users(id, telegram_id, username, first_name, last_name, created_at)
characters(id, api_id, first_name, last_name, full_name, title, family)
character_images(id, character_id, image_url, source)
user_favorites(user_id, character_id, created_at)
usage_logs(id, user_id, command, parameter, created_at)

### Statistiche / Queries implementate

/stats user → numero di comandi eseguiti per utente

/stats command → numero di volte che ogni comando è stato eseguito

/stats recent → ultimi 10 comandi eseguiti

#### Esempi di query SQL:

-- Comandi per utente

SELECT u.username, COUNT(*) AS total
FROM usage_logs ul
JOIN users u ON ul.user_id = u.id
GROUP BY ul.user_id
ORDER BY total DESC;

-- Comandi per tipo di comando

SELECT command, COUNT(*) AS total
FROM usage_logs
GROUP BY command
ORDER BY total DESC;

-- Ultimi 10 comandi

SELECT u.username, command, parameter, created_at
FROM usage_logs ul
JOIN users u ON ul.user_id = u.id
ORDER BY created_at DESC
LIMIT 10;

Screenshot ed esempi

