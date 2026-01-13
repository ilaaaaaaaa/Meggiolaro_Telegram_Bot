package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class ImagesAndCharactersAPI {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private static final String BASE_URL = "https://thronesapi.com/api/v2/Characters";

    // Metodo che recupera tutti i personaggi
    public List<Character> getAllCharacters() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            Character[] charactersArray = gson.fromJson(resp.body(), Character[].class);
            return Arrays.asList(charactersArray);

        } catch (IOException | InterruptedException e) {
            System.out.println("Errore nella richiesta API: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Metodo che recupera un personaggio per ID
    public Character getCharacterById(int id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(resp.body(), Character.class);

        } catch (IOException | InterruptedException e) {
            System.out.println("Errore nella richiesta API: " + e.getMessage());
            return null;
        }
    }

    // Metodo che cerca un personaggio per nome completo
    public Character cercaCharacter(String nome) {
        if (nome == null || nome.isBlank()) return null;

        for (Character c : getAllCharacters()) {
            if (c.getFullName() != null &&
                    c.getFullName().equalsIgnoreCase(nome.trim())) {
                return c;
            }
        }
        return null;
    }
}
