package data_access;

import org.junit.jupiter.api.Test;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Clock;
import static org.junit.jupiter.api.Assertions.*;

class CanvasApiDataAccessObjectTest {
    @Test
    void rejectsPaginationOutsideCanvasOrigin() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/v1/courses", exchange -> {
            try {
                exchange.getResponseHeaders().add("Link", "<https://outside.example/courses>; rel=next");
                exchange.sendResponseHeaders(200, 2);
                exchange.getResponseBody().write(new byte[]{'[', ']'});
            }
            finally {
                exchange.close();
            }
        });
        server.start();
        try {
            URI origin = URI.create("http://127.0.0.1:" + server.getAddress().getPort());
            CanvasApiDataAccessObject dao =
                    new CanvasApiDataAccessObject(HttpClient.newHttpClient(), origin, "test-token", Clock.systemUTC());
            assertEquals("Invalid Canvas pagination link.", assertThrows(IOException.class, dao::loadAssignments).getMessage());
        }
        finally {
            server.stop(0);
        }
    }
}
