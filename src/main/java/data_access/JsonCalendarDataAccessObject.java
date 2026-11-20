package data_access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import use_case.view_deadlines_calendar.CalendarCacheDataAccessInterface;
import use_case.view_deadlines_calendar.CalendarSnapshot;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/** Persists one complete calendar snapshot to the account's cache file. */
public final class JsonCalendarDataAccessObject implements CalendarCacheDataAccessInterface {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class,
                    (JsonSerializer<Instant>) (instant, type, context) -> new JsonPrimitive(instant.toString()))
            .registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, type, context) -> Instant.parse(json.getAsString()))
            .create();

    private final Path file;

    public JsonCalendarDataAccessObject(Path file) {
        this.file = file.toAbsolutePath();
    }

    @Override
    public Optional<CalendarSnapshot> readSnapshot() throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            final CalendarSnapshot snapshot = GSON.fromJson(reader, CalendarSnapshot.class);
            if (snapshot == null || snapshot.getCourses() == null || snapshot.getAssignments() == null
                    || snapshot.getSyncedAt() == null) {
                throw new IOException("Incomplete calendar cache.");
            }
            return Optional.of(new CalendarSnapshot(snapshot.getCourses(), snapshot.getAssignments(),
                    snapshot.getSyncedAt()));
        }
        catch (NoSuchFileException exception) {
            return Optional.empty();
        }
        catch (JsonParseException | DateTimeParseException | NullPointerException exception) {
            throw new IOException("Invalid calendar cache.", exception);
        }
    }

    @Override
    public void saveSnapshot(CalendarSnapshot snapshot) throws IOException {
        final Path directory = Files.createDirectories(file.getParent());
        final Path temporary = Files.createTempFile(directory, "deadlines-", ".json");
        try {
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(snapshot, writer);
            }
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }
}
