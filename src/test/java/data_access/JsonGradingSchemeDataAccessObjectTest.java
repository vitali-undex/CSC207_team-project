package data_access;

import entity.EqualWeightAveraging;
import entity.GradingScheme;
import entity.GroupWeight;
import entity.PointsAveraging;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonGradingSchemeDataAccessObjectTest {

    private static final double DELTA = 1e-9;

    @TempDir
    Path folder;

    private Path file() {
        return folder.resolve("quercus-tracker").resolve("grading-schemes.json");
    }

    private JsonGradingSchemeDataAccessObject store() {
        return new JsonGradingSchemeDataAccessObject(file());
    }

    private static GradingScheme scheme(String courseId, double quizzes, double finalExam) {
        return new GradingScheme(courseId, List.of(
                new GroupWeight("quizzes", quizzes, new EqualWeightAveraging(), 1, 0, false),
                new GroupWeight("final", finalExam, new PointsAveraging(), 0, 2, true)));
    }

    private void writeFile(String json) throws IOException {
        Files.createDirectories(file().getParent());
        Files.writeString(file(), json);
    }

    @Test
    void nothingIsSavedAtFirst() throws IOException {
        assertTrue(store().loadScheme("course-1").isEmpty());
        assertFalse(Files.exists(file()));
    }

    @Test
    void savesASchemeAndLoadsItBack() throws IOException {
        store().saveScheme(scheme("course-1", 20, 80));

        GradingScheme loaded = store().loadScheme("course-1").get();
        assertEquals("course-1", loaded.getCourseId());
        GroupWeight quizzes = loaded.getWeights().get(0);
        assertEquals("quizzes", quizzes.getGroupId());
        assertEquals(20.0, quizzes.getWeightPercent(), DELTA);
        assertEquals(1, quizzes.getDropLowest());
        assertTrue(quizzes.getAveraging() instanceof EqualWeightAveraging);
        GroupWeight finalExam = loaded.getWeights().get(1);
        assertTrue(finalExam.getAveraging() instanceof PointsAveraging);
        assertEquals(2, finalExam.getKeepBest());
        assertTrue(finalExam.isCapAtFullMarks());
        assertTrue(Files.readString(file()).contains("\"averaging\": \"equal\""));
        assertTrue(Files.readString(file()).contains("\"capAtFullMarks\": true"));
    }

    @Test
    void schemesSavedBeforeCountBestAndCapStillLoad() throws IOException {
        writeFile("{\"course-1\": {\"weights\": [{\"groupId\": \"quizzes\", \"weightPercent\": 100, "
                + "\"dropLowest\": 1, \"averaging\": \"points\"}]}}");

        GroupWeight quizzes = store().loadScheme("course-1").get().getWeights().get(0);
        assertEquals(1, quizzes.getDropLowest());
        assertEquals(0, quizzes.getKeepBest());
        assertFalse(quizzes.isCapAtFullMarks());
    }

    @Test
    void savingOneCourseKeepsTheOthers() throws IOException {
        store().saveScheme(scheme("course-1", 20, 80));
        store().saveScheme(scheme("course-2", 40, 60));
        store().saveScheme(scheme("course-1", 25, 75));

        assertEquals(25.0, store().loadScheme("course-1").get().getWeights().get(0).getWeightPercent(), DELTA);
        assertEquals(40.0, store().loadScheme("course-2").get().getWeights().get(0).getWeightPercent(), DELTA);
    }

    @Test
    void leavesNoTemporaryFilesBehind() throws IOException {
        store().saveScheme(scheme("course-1", 20, 80));

        try (Stream<Path> files = Files.list(file().getParent())) {
            assertEquals(List.of("grading-schemes.json"),
                    files.map(path -> path.getFileName().toString()).collect(Collectors.toList()));
        }
    }

    @Test
    void anEmptyFileMeansNothingIsSaved() throws IOException {
        writeFile("");
        assertTrue(store().loadScheme("course-1").isEmpty());

        writeFile("{\"course-1\": null}");
        assertTrue(store().loadScheme("course-1").isEmpty());
    }

    @Test
    void aFileThatIsntJsonCantBeRead() throws IOException {
        writeFile("{not json");
        assertThrows(IOException.class, () -> store().loadScheme("course-1"));

        writeFile("\"not a scheme file\"");
        assertThrows(IOException.class, () -> store().loadScheme("course-1"));
        assertThrows(IOException.class, () -> store().saveScheme(scheme("course-1", 20, 80)));
    }

    @Test
    void invalidSavedSchemesCantBeRead() throws IOException {
        String[] invalid = {
            "{\"course-1\": {}}",
            "{\"course-1\": {\"weights\": [null]}}",
            "{\"course-1\": {\"weights\": [{\"weightPercent\": 50, \"dropLowest\": 0, \"averaging\": \"points\"}]}}",
            "{\"course-1\": {\"weights\": [{\"groupId\": \"a\", \"weightPercent\": 50, \"averaging\": \"median\"}]}}",
            "{\"course-1\": {\"weights\": [{\"groupId\": \"a\", \"weightPercent\": 150, \"averaging\": \"points\"}]}}",
            "{\"course-1\": {\"weights\": [{\"groupId\": \"a\", \"weightPercent\": 50, \"averaging\": \"points\", "
                + "\"dropLowest\": 1, \"keepBest\": 2}]}}",
        };
        for (String json : invalid) {
            writeFile(json);
            assertThrows(IOException.class, () -> store().loadScheme("course-1"), json);
        }
    }
}
