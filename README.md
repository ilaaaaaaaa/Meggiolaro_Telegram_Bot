# MaesterOfWesterosBot

🐉 **Telegram Bot informativo su Game of Thrones**

---

## Descrizione
MaesterOfWesterosBot è un bot Telegram che permette di:

- Cercare personaggi di *Game of Thrones* tramite l'API pubblica [Game of Thrones Character Api](https://thronesapi.com/)
- Visualizzare informazioni dettagliate su ogni personaggio: nome completo, titolo, casata e immagine
- Fornire informazioni sulle casate della serie tramite l'API pubblica [Game of Thrones Quotes API](https://gameofthronesquotes.xyz/)
- Gestire una lista personale di personaggi preferiti
- Visualizzare statistiche sull'utilizzo dei comandi

Il bot è sviluppato in **Java 21** con **Maven** e utilizza **SQLite** come database locale.

---

## API utilizzate
- [Game of Thrones Character API](https://thronesapi.com/)
- [Game of Thrones Quotes API](https://gameofthronesquotes.xyz/)
- Nessuna chiave API richiesta

### Documentazioni
- [Game of Thrones Character API](https://thronesapi.com/swagger/index.html?urls.primaryName=Game%20of%20Thrones%20API%20v2)
- [Game of Thrones Quotes API](https://github.com/shevabam/game-of-thrones-quotes-api)

---

## Setup

### 1. Prerequisiti

- Java JDK 21 (verificare la versione con il comando bash "java -version")
- Maven 
- Un account Telegram
- Un bot Telegram creato tramite @BotFather
- Connessione Internet per chiamare l'API esterna

### 2. Clonare il progetto

```bash
  git clone https://github.com/ilaaaaaaaa/Meggiolaro_Telegram_Bot.git
  cd Meggiolaro_Telegram_Bot
```

### 3. Configurazione BOT TOKEN
Nel progetto è presente un file denominato `config.properties.example` con cui si può vedere come inserire il bot token.
Segui questi passaggi:
1. Copia il file di esempio `config.properties.example` nella sottocartella `resources` e rinominalo in `config.properties`
2. Apri il file e nel segnaposto `INSERISCI_QUI_IL_TOKEN_DEL_BOT` inserisci il bot token che ti è stato fornito da @BotFather

#### Dove si ottiene il bot token? Il `BOT_TOKEN` si ottiene creando un bot tramite @BotFather su Telegram

### 4. Setup del database SQLite
Il progetto utilizza SQLite.
Il database e le relative tabelle vengono create automaticamente al primo avvio del bot nel file:

`database/game_of_thrones.db`

### 5. Build del progetto

```bash
  mvn clean package
```

### 6. Avvio del bot
Il bot si può avviare tramite il comando bash:

```bash
  mvn exec:java
```
oppure:
```bash
  java -jar target/Meggiolaro_Telegram_Bot-1.0.jar
```
Se avviato correttamente, stamperà la frase *MaesterOfWesterosBot successfully started!*

### Specifiche aggiuntive
Il file `config.properties` è escluso dalla repository tramite `.gitignore` per evitare di pubblicare il token del bot.

## Struttura del progetto

- src/main/java/org.example → codice sorgente 
- src/main/resources → file di configurazione 
- database/ → file SQLite

---
Per maggiori dettagli riguardo i comandi e la struttura del database, consultare il documento [Meggiolaro_Bot_Telegram](https://docs.google.com/document/d/1HcS0vF6XeFO7EDoGTVOPUUecSigtwcvKQ7RJOJYh_RU/edit?usp=sharing)



