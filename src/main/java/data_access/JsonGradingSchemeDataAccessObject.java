package data_access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import entity.AveragingStrategies;
import entity.AveragingStrategy;
import entity.GradingScheme;
import entity.GroupWeight;
import use_case.calculate_grade.GradingSchemeDataAccessInterface;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Saves every course's grading scheme in one JSON file in the student's home
 * folder, so a syllabus only has to be typed in once. The file is replaced in
 * one step, so a crash while saving never leaves half a file behind.
 *
 * <pre>
 * {
 *   "demo-101": {
 *     "weights": [
 *       { "groupId": "demo-101-quizzes", "weightPercent": 20.0, "dropLowest": 1,
 *         "averaging": "equal" }
 *     ]
 *   }
 * }
 * </pre>
 */
public final class JsonGradingSchemeDataAccessObject implements GradingSchemeDataAccessInterface {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type FILE_TYPE =
            TypeToken.getParameterized(Map.class, String.class, StoredScheme.class).getType();

    private final Path file;

    /**
     * Creates the data access object.
     *
     * @param file the JSON file to keep the schemes in; it is created on the first save
     */
    public JsonGradingSchemeDataAccessObject(Path file) {
        this.file = file.toAbsolutePath();
    }

    @Override
    public Optional<GradingScheme> loadScheme(String courseId) throws IOException {
        final StoredScheme stored = readAll().get(courseId);
        if (stored == null) {
            return Optional.empty();
        }
        return Optional.of(toScheme(courseId, stored));
    }

    @Override
    public void saveScheme(GradingScheme scheme) throws IOException {
        final Map<String, StoredScheme> all = readAll();
        all.put(scheme.getCourseId(), toStored(scheme));
        final Path directory = Files.createDirectories(file.getParent());
        final Path temporary = Files.createTempFile(directory, "grading-schemes-", ".json");
        try {
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(all, FILE_TYPE, writer);
            }
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }

    private Map<String, StoredScheme> readAll() throws IOException {
        final Map<String, StoredScheme> all = new TreeMap<>();
        try (Reader reader = Files.newBufferedReader(file)) {
            final Map<String, StoredScheme> saved = GSON.fromJson(reader, FILE_TYPE);
            if (saved != null) {
                all.putAll(saved);
            }
        }
        catch (NoSuchFileException exception) {
            return all;
        }
        catch (JsonParseException exception) {
            throw new IOException("The grading scheme file isn't valid JSON: " + file, exception);
        }
        return all;
    }

    private static GradingScheme toScheme(String courseId, StoredScheme stored) throws IOException {
        if (stored.weights == null) {
            throw invalid(courseId, null);
        }
        final List<GroupWeight> weights = new ArrayList<>();
        for (StoredWeight saved : stored.weights) {
            if (saved == null || saved.groupId == null) {
                throw invalid(courseId, null);
            }
            final Optional<AveragingStrategy> averaging = AveragingStrategies.byId(saved.averaging);
            if (averaging.isEmpty()) {
                throw invalid(courseId, null);
            }
            try {
                weights.add(new GroupWeight(saved.groupId, saved.weightPercent, saved.dropLowest,
                        averaging.get()));
            }
            catch (IllegalArgumentException exception) {
                throw invalid(courseId, exception);
            }
        }
        return new GradingScheme(courseId, weights);
    }

    private static IOException invalid(String courseId, Exception cause) {
        return new IOException("The saved grading scheme for " + courseId + " isn't valid.", cause);
    }

    private static StoredScheme toStored(GradingScheme scheme) {
        final StoredScheme stored = new StoredScheme();
        stored.weights = new ArrayList<>();
        for (GroupWeight weight : scheme.getWeights()) {
            final StoredWeight saved = new StoredWeight();
            saved.groupId = weight.getGroupId();
            saved.weightPercent = weight.getWeightPercent();
            saved.dropLowest = weight.getDropLowest();
            saved.averaging = weight.getAveraging().getId();
            stored.weights.add(saved);
        }
        return stored;
    }

    /** One course's scheme as it is written in the file. */
    private static final class StoredScheme {
        private List<StoredWeight> weights;
    }

    /** One group's weight as it is written in the file. */
    private static final class StoredWeight {
        private String groupId;
        private double weightPercent;
        private int dropLowest;
        private String averaging;
    }
}
