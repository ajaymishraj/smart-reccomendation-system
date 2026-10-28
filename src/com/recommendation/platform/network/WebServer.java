package com.recommendation.platform.network;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.*;

/**
 * Embedded HTTP server providing REST API endpoints and static web hosting (BCSE301 Unit 4).
 */
public class WebServer {

    private final int port;
    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;
    private HttpServer server;

    public WebServer(int port, UserService us, CatalogService cs,
                     InteractionService is, RecommendationService rs, ReportService rep) {
        this.port = port;
        this.userService = us;
        this.catalogService = cs;
        this.interactionService = is;
        this.recommendationService = rs;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/courses", this::handleCourses);
            server.createContext("/api/users", this::handleUsers);
            server.createContext("/api/recommendations", this::handleRecs);
            server.createContext("/api/enroll", this::handleEnroll);
            server.createContext("/api/analytics", this::handleAnalytics);
            server.createContext("/", this::handleStatic);
            server.setExecutor(null);
            server.start();
            System.out.println("🚀 [Server] Web Server & REST API active at http://localhost:" + port + "/");
        } catch (IOException e) {
            System.err.println("❌ [Server] Failed to start HTTP server: " + e.getMessage());
        }
    }

    private void handleCourses(HttpExchange ex) throws IOException {
        StringBuilder json = new StringBuilder("[");
        List<Item> items = catalogService.getAvailableItems();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            json.append(String.format(Locale.US, "{\"id\":%d,\"title\":\"%s\",\"category\":\"%s\",\"price\":%.2f}",
                    item.getItemId(), item.getTitle().replace("\"", "\\\""), item.getCategory(), item.getPrice()));
            if (i < items.size() - 1) json.append(",");
        }
        json.append("]");
        sendJson(ex, json.toString());
    }

    private void handleUsers(HttpExchange ex) throws IOException {
        StringBuilder json = new StringBuilder("[");
        List<User> users = userService.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"role\":\"%s\"}", u.getUserId(), u.getName(), u.getRole()));
            if (i < users.size() - 1) json.append(",");
        }
        json.append("]");
        sendJson(ex, json.toString());
    }

    private void handleRecs(HttpExchange ex) throws IOException {
        Map<String, String> query = parseQuery(ex.getRequestURI().getQuery());
        int uid = Integer.parseInt(query.getOrDefault("userId", "3"));
        User user = userService.findById(uid);
        List<Item> recs = (user != null) ? recommendationService.getRecommendations(user) : catalogService.getAvailableItems();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < recs.size(); i++) {
            Item item = recs.get(i);
            json.append(String.format(Locale.US, "{\"id\":%d,\"title\":\"%s\",\"category\":\"%s\",\"price\":%.2f}",
                    item.getItemId(), item.getTitle().replace("\"", "\\\""), item.getCategory(), item.getPrice()));
            if (i < recs.size() - 1) json.append(",");
        }
        json.append("]");
        sendJson(ex, json.toString());
    }

    private void handleEnroll(HttpExchange ex) throws IOException {
        Map<String, String> query = parseQuery(ex.getRequestURI().getQuery());
        int uid = Integer.parseInt(query.getOrDefault("userId", "3"));
        int cid = Integer.parseInt(query.getOrDefault("courseId", "1"));
        try {
            interactionService.enroll(uid, cid);
            sendText(ex, 200, "Successfully enrolled in course #" + cid);
        } catch (Exception e) {
            sendText(ex, 400, "Error: " + e.getMessage());
        }
    }

    private void handleAnalytics(HttpExchange ex) throws IOException {
        String json = String.format("{\"totalUsers\":%d,\"totalCourses\":%d,\"totalEnrollments\":%d}",
                userService.getTotalUsers(), catalogService.getTotalItems(), interactionService.getTotalEnrollments());
        sendJson(ex, json);
    }

    private void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path == null || path.equals("/")) path = "/index.html";
        File file = new File("web", path.substring(1));
        if (file.exists() && !file.isDirectory()) {
            String mime = path.endsWith(".html") ? "text/html" : path.endsWith(".css") ? "text/css" :
                    path.endsWith(".js") ? "application/javascript" : "application/octet-stream";
            byte[] bytes = Files.readAllBytes(file.toPath());
            ex.getResponseHeaders().set("Content-Type", mime);
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        } else {
            sendText(ex, 404, "404 Not Found");
        }
    }

    private Map<String, String> parseQuery(String q) {
        Map<String, String> m = new HashMap<>();
        if (q == null) return m;
        for (String p : q.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2) m.put(kv[0], kv[1]);
        }
        return m;
    }

    private void sendJson(HttpExchange ex, String json) throws IOException {
        byte[] b = json.getBytes("UTF-8");
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(200, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private void sendText(HttpExchange ex, int code, String text) throws IOException {
        byte[] b = text.getBytes("UTF-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }
}
