package app;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeCalculatorMainTest {

    @Test
    void hasAStandardMainMethod() throws NoSuchMethodException {
        Method main = GradeCalculatorMain.class.getMethod("main", String[].class);

        assertTrue(Modifier.isStatic(main.getModifiers()));
        assertEquals(void.class, main.getReturnType());
    }

    @Test
    void savesSchemesInTheHomeFolderNotTheProject() {
        Path file = GradeCalculatorMain.schemeFile();

        assertEquals(Path.of(System.getProperty("user.home")), file.getParent().getParent());
        assertEquals(".quercus-tracker", file.getParent().getFileName().toString());
        assertEquals("grading-schemes.json", file.getFileName().toString());
    }
}
