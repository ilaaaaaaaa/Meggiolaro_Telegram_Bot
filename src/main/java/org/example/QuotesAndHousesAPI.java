package org.example;

import com.google.gson.Gson;
import org.example.Quote;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class QuotesAndHousesAPI {

    private static final String BASE_URL = "https://api.gameofthronesquotes.xyz/v1";
    private final HttpClient client;
    private final Gson gson;

    public QuotesAndHousesAPI() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // Metodo per ricavare una citazione random
    public Quote getRandomQuote() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/random"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return gson.fromJson(response.body(), Quote.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per ricavare la lista di tutte le casate
    public List<House> getAllHouses() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/houses"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            House[] houses = gson.fromJson(response.body(), House[].class);
            return List.of(houses);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Metodo per ricavare le informazioni di una casata
    public House getHouseBySlug(String slug) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/house/" + slug))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            House[] result = gson.fromJson(response.body(), House[].class);
            return result.length > 0 ? result[0] : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
