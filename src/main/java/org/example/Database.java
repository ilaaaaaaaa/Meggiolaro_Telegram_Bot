package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Database instance;
    private Connection connection;

    private Database() throws SQLException {
        String url = "jdbc:sqlite:database/game_of_thrones.db";
        connection = DriverManager.getConnection(url);
        System.out.println("Connessione al database stabilita.");

        // Creazione tabelle
        String createUsers = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            telegram_id INTEGER UNIQUE NOT NULL,
            username TEXT,
            first_name TEXT,
            last_name TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        );
    """;

        String createCharacters = """
        CREATE TABLE IF NOT EXISTS characters (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            api_id INTEGER UNIQUE,
            first_name TEXT,
            last_name TEXT,
            full_name TEXT NOT NULL,
            title TEXT,
            family TEXT
        );
    """;

        String createCharacterImages = """
        CREATE TABLE IF NOT EXISTS character_images (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            character_id INTEGER NOT NULL,
            image_url TEXT NOT NULL,
            source TEXT DEFAULT 'thronesapi',
            FOREIGN KEY (character_id) REFERENCES characters(id)
        );
    """;

        String createUserFavorites = """
        CREATE TABLE IF NOT EXISTS user_favorites (
            user_id INTEGER,
            character_id INTEGER,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            PRIMARY KEY (user_id, character_id),
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (character_id) REFERENCES characters(id)
        );
    """;

        String createUsageLogs = """
        CREATE TABLE IF NOT EXISTS usage_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            command TEXT,
            parameter TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id)
        );
    """;

        // Esecuzione delle query
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createCharacters);
            stmt.execute(createCharacterImages);
            stmt.execute(createUserFavorites);
            stmt.execute(createUsageLogs);
        }
    }

    public static Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Metodo che restituisce l'id dell'utente, creandolo se non esiste [tabella users]
    public int getOrCreateUser(long telegramId, String username, String firstName, String lastName) {

        String select = "SELECT id FROM users WHERE telegram_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(select)) {
            stmt.setLong(1, telegramId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ignored) {}

        String insert = """
            INSERT INTO users (telegram_id, username, first_name, last_name)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, telegramId);
            stmt.setString(2, username);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Errore creazione utente: " + e.getMessage());
        }

        return -1;
    }

    // Metodo che restituisce l'id del personaggio, creandolo se non esiste [tabella characters]
    public int getOrCreateCharacter(Character c) {

        String select = "SELECT id FROM characters WHERE api_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(select)) {
            stmt.setInt(1, c.getId()); // ID API
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // id interno DB
            }
        } catch (SQLException ignored) {}

        String insert = """
            INSERT INTO characters (api_id, first_name, last_name, full_name, title, family)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, c.getId()); // api_id
            stmt.setString(2, c.getFirstName());
            stmt.setString(3, c.getLastName());
            stmt.setString(4, c.getFullName());
            stmt.setString(5, c.getTitle());
            stmt.setString(6, c.getFamily());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); // id interno DB
            }
        } catch (SQLException e) {
            System.err.println("Errore inserimento personaggio: " + e.getMessage());
        }

        return -1;
    }

    // Metodo che salva l'url dell'immagine e l'id del personaggio nel database [tabella character_images]
    public void saveCharacterImage(int characterId, String imageUrl) {

        String query = """
                INSERT OR IGNORE INTO character_images (character_id, image_url)
                VALUES (?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, characterId);
            stmt.setString(2, imageUrl);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio immagine: " + e.getMessage());
        }
    }

    // Metodo che salva un personaggio tra i preferiti [tabella user_favorites]
    public boolean addFavorite(int userId, int characterId) {

        String query = """
            INSERT OR IGNORE INTO user_favorites (user_id, character_id)
            VALUES (?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, characterId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Errore aggiunta preferito: " + e.getMessage());
            return false;
        }
    }

    // Metodo che rimuove un personaggio dai preferiti [tabella user_favorites]
    public boolean removeFavorite(int userId, int characterId) {

        String query = """
            DELETE FROM user_favorites
            WHERE user_id = ? AND character_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, characterId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Errore rimozione preferito: " + e.getMessage());
            return false;
        }
    }

    // Metodo che ritorna la lista dei personaggi salvati nei preferiti [tabella user_favorites]
    public List<Character> getUserFavorites(int userId) {

        List<Character> favorites = new ArrayList<>();

        String query = """
        SELECT c.*
        FROM characters c
        JOIN user_favorites uf ON c.id = uf.character_id
        WHERE uf.user_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Character c = new Character(
                        rs.getInt("api_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("full_name"),
                        rs.getString("title"),
                        rs.getString("family"),
                        null,
                        null
                );
                favorites.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura preferiti: " + e.getMessage());
        }

        return favorites;
    }

    // Metodo che tiene traccia dei comandi utilizzati dall'utente [usage_logs]
    public void logCommand(int userId, String command, String parameter) {

        String query = """
                INSERT INTO usage_logs (user_id, command, parameter)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, command);
            stmt.setString(3, parameter);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore log comando: " + e.getMessage());
        }
    }

    // Metodo che ritorna l'url con l'immagine di un personaggio [tabella character_images]
    public String getCharacterImage(int characterId) {
        String query = """
            SELECT image_url
            FROM character_images
            WHERE character_id = ?
            LIMIT 1
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("image_url");
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura immagine: " + e.getMessage());
        }

        return null;
    }

    // Metodi per il comando /stats
    // 1. Numero totale di comandi di un utente
    public int getUserCommandCount(int userId) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM usage_logs WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("total");
        }
        return 0;
    }

    // 2. Conteggio comandi totali per tipo
    public String getCommandUsageStats() throws SQLException {
        String query = "SELECT command, COUNT(*) AS total FROM usage_logs GROUP BY command ORDER BY total DESC";
        StringBuilder sb = new StringBuilder();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sb.append(rs.getString("command")).append(": ").append(rs.getInt("total")).append("\n");
            }
        }
        return sb.toString();
    }

    // 3. Ultimi n comandi eseguiti da un utente
    public List<String> getUserRecentCommands(int userId, int limit) throws SQLException {
        List<String> list = new ArrayList<>();
        String query = "SELECT command FROM usage_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("command"));
            }
        }
        return list;
    }
}
