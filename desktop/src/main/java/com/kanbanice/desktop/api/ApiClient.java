package com.kanbanice.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanbanice.desktop.state.AppState;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE = "http://localhost:8080";
    private static ApiClient instance;

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ApiClient get() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    private HttpRequest.Builder base(String path) {
        var b = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json");
        String tok = AppState.getInstance().getToken();
        if (tok != null) b.header("Authorization", "Bearer " + tok);
        return b;
    }

    public <T> T get(String path, Class<T> type) throws Exception {
        var req = base(path).GET().build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
        return mapper.readValue(res.body(), type);
    }

    public <T> T get(String path, TypeReference<T> type) throws Exception {
        var req = base(path).GET().build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
        return mapper.readValue(res.body(), type);
    }

    public <T> T post(String path, Object body, Class<T> type) throws Exception {
        String json = body != null ? mapper.writeValueAsString(body) : "{}";
        var req = base(path).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
        if (type == Void.class || res.body() == null || res.body().isBlank()) return null;
        return mapper.readValue(res.body(), type);
    }

    public <T> T put(String path, Object body, Class<T> type) throws Exception {
        String json = body != null ? mapper.writeValueAsString(body) : "{}";
        var req = base(path).PUT(HttpRequest.BodyPublishers.ofString(json)).build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
        if (type == Void.class || res.body() == null || res.body().isBlank()) return null;
        return mapper.readValue(res.body(), type);
    }

    public void delete(String path) throws Exception {
        var req = base(path).DELETE().build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
    }

    public void postNoBody(String path) throws Exception {
        var req = base(path).POST(HttpRequest.BodyPublishers.noBody()).build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        check(res);
    }

    private void check(HttpResponse<String> res) throws Exception {
        if (res.statusCode() >= 400) {
            throw new RuntimeException("API " + res.statusCode() + ": " + res.body());
        }
    }

    public ObjectMapper getMapper() { return mapper; }
}
