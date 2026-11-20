package app;

import org.junit.jupiter.api.Test;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.*;

class DeadlineCalendarMainTest {
    @Test
    void normalizesCanvasAddressAndPreservesCustomPort() {
        assertEquals(URI.create("https://q.utoronto.ca"), DeadlineCalendarMain.canvasOrigin("HTTPS://Q.UTORONTO.CA:443/"));
        assertEquals(URI.create("https://canvas.example:8443"), DeadlineCalendarMain.canvasOrigin("https://canvas.example:8443"));
    }

    @Test
    void rejectsInsecureAddressesAndNonOriginUrls() {
        assertThrows(IllegalArgumentException.class, () -> DeadlineCalendarMain.canvasOrigin("http://canvas.example"));
        assertThrows(IllegalArgumentException.class, () -> DeadlineCalendarMain.canvasOrigin("https://canvas.example/api"));
        assertThrows(IllegalArgumentException.class, () -> DeadlineCalendarMain.canvasOrigin("https://user@canvas.example"));
    }
}
