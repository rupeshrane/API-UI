package org.sodales;

import model.AuthContext;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuth2Client {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void getAccessToken(AuthContext authContext) throws IOException, InterruptedException {
        String body = "grant_type=" + safeValue(authContext.grantType)
                + "&client_id=" + URLEncoder.encode(safeValue(authContext.clientId), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(safeValue(authContext.clientSecret), StandardCharsets.UTF_8);

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(authContext.tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                        (safeValue(authContext.clientId) + ":" + safeValue(authContext.clientSecret)).getBytes()
                ))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to obtain access token. Code: "
                    + response.statusCode() + ", Response: " + response.body());
        }

        parseTokenResponse(response.body(), authContext);
        System.out.println("New access token acquired, expires at: " + authContext.tokenExpiry);
    }

    public static void refreshAccessToken(AuthContext authContext) throws IOException, InterruptedException {
        if (safeValue(authContext.refreshToken).isEmpty()) {
            getAccessToken(authContext);
            return;
        }

        String body = "grant_type=refresh_token"
                + "&client_id=" + URLEncoder.encode(safeValue(authContext.clientId), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(safeValue(authContext.clientSecret), StandardCharsets.UTF_8)
                + "&refresh_token=" + URLEncoder.encode(safeValue(authContext.refreshToken), StandardCharsets.UTF_8);

        HttpRequest refreshRequest = HttpRequest.newBuilder()
                .uri(URI.create(authContext.tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(refreshRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Refresh token failed, requesting a new access token...");
            getAccessToken(authContext);
            return;
        }

        parseTokenResponse(response.body(), authContext);
        System.out.println("Access token refreshed successfully.");
    }

    public static boolean isTokenMissingOrExpired(AuthContext authContext) {
        return authContext.accessToken == null
                || authContext.tokenExpiry == null
                || Instant.now().isAfter(authContext.tokenExpiry);
    }

    @SuppressWarnings("unchecked")
    private static void parseTokenResponse(String json, AuthContext authContext) throws IOException {
        Map<String, Object> map = mapper.readValue(json, HashMap.class);

        authContext.accessToken = (String) map.get("access_token");
        authContext.refreshToken = (String) map.get("refresh_token");

        Number expiresInNumber = (Number) map.getOrDefault("expires_in", 3600);
        long expiresIn = expiresInNumber.longValue();

        authContext.tokenExpiry = Instant.now().plusSeconds(expiresIn - 30);
    }

    private static String safeValue(String value) {
        return value == null ? "" : value;
    }
}