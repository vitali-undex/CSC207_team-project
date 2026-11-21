package app;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void exposesConventionalJavaLauncher() throws NoSuchMethodException {
        Method entryPoint = Main.class.getMethod("main", String[].class);
        assertTrue(Modifier.isStatic(entryPoint.getModifiers()));
        assertEquals(void.class, entryPoint.getReturnType());
    }
}
