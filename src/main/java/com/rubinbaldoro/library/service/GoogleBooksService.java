package com.rubinbaldoro.library.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class GoogleBooksService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper(); // Parses JSON

    // Record to hold the data we want (Title, Author, Image URL)
    public record BookInfo(String title, String author, String description, String imageUrl) {}

    public Optional<BookInfo> searchByIsbn(String isbn) {
        try {
            // 1. Build the Google Books API URL
            String cleanIsbn = isbn.replaceAll("[^0-9]", ""); // Remove dashes
            String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + cleanIsbn;

            // 2. Send the Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Parse the JSON Response
            JsonNode root = mapper.readTree(response.body());
            if (root.path("totalItems").asInt() == 0) {
                return Optional.empty(); // No book found
            }

            // 4. Extract Details
            JsonNode volumeInfo = root.path("items").get(0).path("volumeInfo");
            
            String title = volumeInfo.path("title").asText("Unknown Title");
            
            // Authors is an array, we just take the first one
            String author = "Unknown Author";
            if (volumeInfo.has("authors")) {
                author = volumeInfo.path("authors").get(0).asText();
            }
            
            String description = volumeInfo.path("description").asText("");

            // Get Image Link (prefer 'thumbnail')
            String imageUrl = "";
            if (volumeInfo.has("imageLinks")) {
                imageUrl = volumeInfo.path("imageLinks").path("thumbnail").asText();
                // Google often sends http links, we want https
                imageUrl = imageUrl.replace("http://", "https://"); 
            }

            return Optional.of(new BookInfo(title, author, description, imageUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}