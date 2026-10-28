package com.recommendation.platform.network;

import com.recommendation.platform.model.*;
import com.recommendation.platform.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Unit 4 Requirement: Socket Programming & Client-Server Communication
 * Embedded HTTP server providing REST API endpoints and serving the modern Web UI.
 */
public class WebServer {

    private final int port;
    private final UserService userService;
    private final CatalogService catalogService;
    private final InteractionService interactionService;
    private final RecommendationService recommendationService;
    private final ReportService reportService;
    private HttpServer server;

    public WebServer(int port, UserService us, CatalogService cs,
                     InteractionService is, RecommendationService rs, ReportService rep) {
        this.port = port;
        this.userService = us;
        this.catalogService = cs;
        this.interactionService = is;
        this.recommendationService = rs;
        this.reportService = rep;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/courses", new CoursesHandler());
            server.createContext("/api/users", new UsersHandler());
            server.createContext("/api/recommendations", new RecommendationsHandler());
            server.createContext("/api/enroll", new EnrollHandler());
            server.createContext("/api/rate", new RateHandler());
            server.createContext("/api/analytics", new AnalyticsHandler());
            server.createContext("/api/strategy", new StrategyHandler());
            server.createContext("/", new StaticFileHandler());

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
            System.out.println("🚀 [Server] Web Server & REST API active at http://localhost:" + port + "/");
        } catch (IOException e) {
            System.err.println("❌ [Server] Failed to start HTTP server on port " + port + ": " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("🛑 [Server] Web server stopped.");
        }
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private class CoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Item> items = catalogService.getAvailableItems();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    json.append(serializeCourse(item));
                    if (i < items.size() - 1) json.append(",");
                }
                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> params = parseFormOrJson(body);
                try {
                    String title = params.getOrDefault("title", "Untitled Course");
                    String desc = params.getOrDefault("description", "Course Description");
                    ItemCategory cat = ItemCategory.valueOf(params.getOrDefault("category", "PROGRAMMING").toUpperCase());
                    double price = Double.parseDouble(params.getOrDefault("price", "499"));
                    String inst = params.getOrDefault("instructor", "Faculty Expert");
                    int hours = Integer.parseInt(params.getOrDefault("durationHours", "30"));
                    String level = params.getOrDefault("level", "BEGINNER").toUpperCase();

                    Course c = catalogService.addCourse(title, desc, cat, price, inst, hours, level);
                    sendJsonResponse(exchange, 201, "{\"success\":true,\"message\":\"Course created\",\"course\":" + serializeCourse(c) + "}");
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            List<User> users = userService.getAllUsers();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                json.append(String.format("{\"userId\":%d,\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"active\":%b}",
                        u.getUserId(), escapeJson(u.getName()), escapeJson(u.getEmail()), u.getRole(), u.isActive()));
                if (i < users.size() - 1) json.append(",");
            }
            json.append("]");
            sendJsonResponse(exchange, 200, json.toString());
        }
    }

    private class RecommendationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            int userId = 3; // Default to Student Raj
            if (query != null && query.contains("userId=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("userId=")) {
                        try { userId = Integer.parseInt(param.substring(7)); } catch (Exception ignored) {}
                    }
                }
            }

            try {
                User u = userService.findById(userId);
                List<Item> recs = recommendationService.getRecommendations(u);
                StringBuilder json = new StringBuilder("{\"strategy\":\"" + recommendationService.getCurrentStrategy() + "\",\"recommendations\":[");
                for (int i = 0; i < recs.size(); i++) {
                    json.append(serializeCourse(recs.get(i)));
                    if (i < recs.size() - 1) json.append(",");
                }
                json.append("]}");
                sendJsonResponse(exchange, 200, json.toString());
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class EnrollHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String body = readBody(exchange);
            Map<String, String> params = parseFormOrJson(body);
            try {
                int userId = Integer.parseInt(params.get("userId"));
                int courseId = Integer.parseInt(params.get("courseId"));
                Enrollment e = interactionService.enroll(userId, courseId);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Enrolled successfully!\",\"enrollmentId\":" + e.getEnrollmentId() + "}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class RateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String body = readBody(exchange);
            Map<String, String> params = parseFormOrJson(body);
            try {
                int userId = Integer.parseInt(params.get("userId"));
                int courseId = Integer.parseInt(params.get("courseId"));
                double rating = Double.parseDouble(params.get("rating"));
                interactionService.rateItem(userId, courseId, rating);
                Item item = catalogService.findById(courseId);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Rating recorded!\",\"avgRating\":" + item.getAverageRating() + "}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class StrategyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String body = readBody(exchange);
            Map<String, String> params = parseFormOrJson(body);
            String strat = params.getOrDefault("strategy", "POPULARITY").toUpperCase();
            if (strat.contains("CONTENT")) {
                recommendationService.switchToContentBased();
            } else if (strat.contains("COLLAB")) {
                recommendationService.switchToCollaborative();
            } else {
                recommendationService.switchToPopularity();
            }
            sendJsonResponse(exchange, 200, "{\"success\":true,\"currentStrategy\":\"" + recommendationService.getCurrentStrategy() + "\"}");
        }
    }

    private class AnalyticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            int users = userService.getTotalUsers();
            int courses = catalogService.getTotalItems();
            int enrollments = interactionService.getTotalEnrollments();
            String summary = reportService.generateSummaryReport();

            String json = String.format("{\"totalUsers\":%d,\"totalCourses\":%d,\"totalEnrollments\":%d,\"strategy\":\"%s\",\"summary\":\"%s\"}",
                    users, courses, enrollments, recommendationService.getCurrentStrategy(), escapeJson(summary));
            sendJsonResponse(exchange, 200, json);
        }
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File webDir = new File("web");
            File file = new File(webDir, path.startsWith("/") ? path.substring(1) : path);

            if (!file.exists() || file.isDirectory()) {
                // Try index.html as fallback
                file = new File(webDir, "index.html");
            }

            if (!file.exists()) {
                String notFound = "<html><body><h1>404 Not Found</h1><p>Web UI files not found in web/ directory.</p></body></html>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }

            String mime = "text/html";
            if (path.endsWith(".css")) mime = "text/css";
            else if (path.endsWith(".js")) mime = "application/javascript";
            else if (path.endsWith(".json")) mime = "application/json";
            else if (path.endsWith(".png")) mime = "image/png";
            else if (path.endsWith(".svg")) mime = "image/svg+xml";

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", mime + "; charset=UTF-8");
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private Map<String, String> parseFormOrJson(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        body = body.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            // Simple JSON parser
            String inner = body.substring(1, body.length() - 1);
            String[] pairs = inner.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replace("\"", "");
                    String v = kv[1].trim().replace("\"", "");
                    map.put(k, v);
                }
            }
        } else {
            // URL-encoded form data
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    try {
                        map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                    } catch (Exception ignored) {}
                }
            }
        }
        return map;
    }

    private String serializeCourse(Item item) {
        String instructor = "Dr. Sharma";
        int hours = 30;
        String level = "BEGINNER";
        int enrollments = 0;
        if (item instanceof Course) {
            Course c = (Course) item;
            instructor = c.getInstructor();
            hours = c.getDurationHours();
            level = c.getLevel();
            enrollments = c.getEnrollmentCount();
        }

        return String.format(
                "{\"itemId\":%d,\"title\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"price\":%.2f,\"averageRating\":%.1f,\"totalRatings\":%d,\"viewCount\":%d,\"instructor\":\"%s\",\"durationHours\":%d,\"level\":\"%s\",\"enrollmentCount\":%d}",
                item.getItemId(), escapeJson(item.getTitle()), escapeJson(item.getDescription()),
                item.getCategory().name(), item.getPrice(), item.getAverageRating(),
                item.getTotalRatings(), item.getViewCount(), escapeJson(instructor), hours, level, enrollments
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
