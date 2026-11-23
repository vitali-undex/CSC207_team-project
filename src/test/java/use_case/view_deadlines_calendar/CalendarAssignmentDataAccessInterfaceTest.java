package use_case.view_deadlines_calendar;

import data_access.CanvasApiDataAccessObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CalendarAssignmentDataAccessInterfaceTest {
    @Test
    void exposesAnIoFailureWhenTheOnlineSourceCannotBeRead() {
        final CalendarAssignmentDataAccessInterface source = new CanvasApiDataAccessObject(
                HttpClient.newHttpClient(), URI.create("http://127.0.0.1:0"),
                "test-token", Clock.systemUTC());
        assertThrows(IOException.class, source::loadAssignments);
    }
}
