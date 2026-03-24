package org.sodales;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuth2Client {

    // CONFIG
    protected static String TOKEN_URL = "https://auth-server.com/oauth/token";
    protected static String CLIENT_ID = "your-client-id";
    protected static String CLIENT_SECRET = "your-client-secret";
    protected static String GRANT_TYPE = "client_credentials"; // or "authorization_code"

    // TOKEN CACHE
    protected static String accessToken;
    protected static String refreshToken;
    protected static Instant tokenExpiry;

    protected static final HttpClient client = HttpClient.newHttpClient();
    protected static final ObjectMapper mapper = new ObjectMapper();

    static{
        TOKEN_URL=PropertyLoader.loadProperties("TOKEN_URL");
        CLIENT_ID=PropertyLoader.loadProperties("CLIENT_ID");
        CLIENT_SECRET=PropertyLoader.loadProperties("CLIENT_SECRET");
        GRANT_TYPE=PropertyLoader.loadProperties("GRANT_TYPE");
    }

    // Request new access token
    protected static void getAccessToken() throws IOException, InterruptedException {
        String body = "grant_type=" + GRANT_TYPE +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8);

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization","Basic "+ Base64.getEncoder().encodeToString((CLIENT_ID+":"+CLIENT_SECRET).getBytes()))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to obtain access token. Code: " + response.statusCode());
        }

        parseTokenResponse(response.body());
        System.out.println("New access token acquired, expires at: " + tokenExpiry);
    }


    // Refresh expired access token
    protected static void refreshAccessToken() throws IOException, InterruptedException {
        String body = "grant_type=refresh_token" +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8) +
                "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        HttpRequest refreshRequest = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(refreshRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println("Refresh token failed, requesting a new access token...");
            getAccessToken();
            return;
        }

        parseTokenResponse(response.body());
        System.out.println("Access token refreshed successfully.");
    }

    // Helper: Parse JSON response
    @SuppressWarnings("unchecked")
    protected static void parseTokenResponse(String json) throws IOException {
        Map<String, Object> map = mapper.readValue(json, HashMap.class);
        accessToken = (String) map.get("access_token");
        refreshToken = (String) map.get("refresh_token"); // may be null for client_credentials
        Integer expiresIn = (Integer) map.getOrDefault("expires_in", 3600);
        tokenExpiry = Instant.now().plusSeconds(expiresIn - 30); // subtract buffer
    }
}

