# MaesterOfWesterosBot

**Nome:** MaesterOfWesterosBot  
**Username Telegram:** `MaesterOfWesteros_bot`  
**Tema:** Game of Thrones  
**Tutorial di riferimento:** [Telegram Bots Java](https://rubenlagus.github.io/TelegramBotsDocumentation/lesson-1.html)

---

## Descrizione

MaesterOfWesterosBot è un bot Telegram che permette di esplorare nel dettaglio l’universo di George R. R. Martin, concentrandosi sui personaggi e le casate di *Game of Thrones*.  

Il bot offre:
- Informazioni sui personaggi
- Immagini dei personaggi
- Funzioni di gestione preferiti e statistiche utente

Il bot integra **tre API pubbliche** per fornire contenuti ricchi e aggiornati.

---

## API utilizzata

| API | Descrizione | Documentazione |
|-----|------------|----------------|
| [ThronesAPI](https://thronesapi.com/) | Immagini dei personaggi | [Swagger](https://thronesapi.com/swagger/index.html?urls.primaryName=Game%20of%20Thrones%20API%20v2) |

---

## Setup e installazione

### Prerequisiti
- Java JDK 21
- Maven
- Telegram Bot Token (ottenuto da [BotFather](https://t.me/BotFather))

### Configurazione API Key
1. Creare un file `config.properties` nella cartella `src/main/resources/`
2. Inserire le chiavi nel formato:

```properties
BOT_TOKEN=Inserisci_il_tuo_token
