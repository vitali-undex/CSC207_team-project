package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import entity.Assignment;
import entity.Course;
import use_case.view_deadlines_calendar.CalendarAssignmentDataAccessInterface;
import use_case.view_deadlines_calendar.CalendarSnapshot;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads assignments for all active courses of one Canvas account. */
public final class CanvasApiDataAccessObject implements CalendarAssignmentDataAccessInterface {
    private static final Pattern NEXT_LINK =
            Pattern.compile("<([^>]+)>\\s*;\\s*rel=\"?next\"?(?=\\s*[,;]|\\s*$)");
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client;
    private final URI origin;
    private final String token;
    private final Clock clock;

    /** Creates a Canvas assignment source for the supplied server and account token. */
    public CanvasApiDataAccessObject(HttpClient client, URI origin, String token, Clock clock) {
        this.client = client;
        this.origin = origin;
        this.token = token;
        this.clock = clock;
    }

    @Override
    public CalendarSnapshot loadAssignments() throws IOException {
        final List<Course> courses = new ArrayList<>();
        final List<Assignment> assignments = new ArrayList<>();
        try {
            for (JsonObject course
                    : loadPages("/api/v1/courses?enrollment_state=active&per_page=100")) {
                final String id = requiredText(course, "id");
                final String name = requiredText(course, "name");
                final String code = optionalText(course, "course_code");
                courses.add(new Course(id, code == null || code.isBlank() ? name : code, name));

                final String path =
                        "/api/v1/courses/" + URLEncoder.encode(id, StandardCharsets.UTF_8)
                        + "/assignments?override_assignment_dates=true&per_page=100";
                for (JsonObject assignment : loadPages(path)) {
                    final String due = optionalText(assignment, "due_at");
                    final String points = optionalText(assignment, "points_possible");
                    assignments.add(new Assignment(requiredText(assignment, "id"), id,
                            requiredText(assignment, "name"), due == null ? null
                                : Instant.parse(due),
                            points == null ? null
                                : assignment.get("points_possible").getAsBigDecimal()));
                }
            }
        }
        catch (JsonParseException | IllegalStateException | UnsupportedOperationException
                | DateTimeParseException | NumberFormatException exception) {
            throw new IOException("Invalid Canvas response.", exception);
        }
        return new CalendarSnapshot(courses, assignments, clock.instant());
    }

    private List<JsonObject> loadPages(String path) throws IOException {
        final List<JsonObject> items = new ArrayList<>();
        final Set<URI> visited = new HashSet<>();
        URI page = origin.resolve(path);
        while (page != null) {
            if (!sameOrigin(page) || !visited.add(page)) {
                throw new IOException("Invalid Canvas pagination link.");
            }
            final HttpRequest request = HttpRequest.newBuilder(page)
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json")
                    .timeout(TIMEOUT)
                    .GET()
                    .build();
            final HttpResponse<String> response = send(request);
            if (response.statusCode() != 200) {
                throw new IOException("Canvas returned HTTP " + response.statusCode() + '.');
            }
            final JsonArray body = JsonParser.parseString(response.body()).getAsJsonArray();
            for (JsonElement element : body) {
                items.add(element.getAsJsonObject());
            }
            page = nextPage(page, response.headers().allValues("Link"));
        }
        return items;
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Canvas request interrupted.", exception);
        }
    }

    private boolean sameOrigin(URI page) {
        return page.getHost() != null && page.getUserInfo() == null
                && origin.getScheme().equalsIgnoreCase(page.getScheme())
                && origin.getHost().equalsIgnoreCase(page.getHost())
                && effectivePort(origin) == effectivePort(page);
    }

    private static int effectivePort(URI uri) {
        if (uri.getPort() != -1) {
            return uri.getPort();
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private static URI nextPage(URI current, List<String> links) throws IOException {
        for (String link : links) {
            final Matcher matcher = NEXT_LINK.matcher(link);
            if (matcher.find()) {
                try {
                    final String target = matcher.group(1);
                    return current.resolve(target.startsWith("?") ? current.getRawPath() + target
                        : target);
                }
                catch (IllegalArgumentException exception) {
                    throw new IOException("Malformed Canvas pagination link.", exception);
                }
            }
        }
        return null;
    }

    private static String requiredText(JsonObject object, String key) throws IOException {
        final String value = optionalText(object, key);
        if (value == null || value.isBlank()) {
            throw new IOException("Missing Canvas field: " + key);
        }
        return value;
    }

    private static String optionalText(JsonObject object, String key) {
        final JsonElement value = object.get(key);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }
}
