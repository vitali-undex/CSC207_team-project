package entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {
    @Test
    void exposesAssignmentDetailsAndAllowsUndatedWork() {
        Instant due = Instant.parse("2027-01-01T03:00:00Z");
        Assignment assignment = new Assignment("2", "1", "Quiz", due, new BigDecimal("10.5"));
        assertEquals("2", assignment.getId());
        assertEquals("1", assignment.getCourseId());
        assertEquals("Quiz", assignment.getName());
        assertEquals(due, assignment.getDueAt());
        assertEquals(new BigDecimal("10.5"), assignment.getPointsPossible());
        assertNull(new Assignment("3", "1", "Practice", null, null).getDueAt());
    }
}
