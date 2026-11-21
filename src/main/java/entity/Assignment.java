package entity;

import java.math.BigDecimal;
import java.time.Instant;

/** Assignment data used by the deadlines calendar. */
public final class Assignment {
    private final String id;
    private final String courseId;
    private final String name;
    private final Instant dueAt;
    private final BigDecimal pointsPossible;

    public Assignment(String id, String courseId, String name, Instant dueAt, BigDecimal pointsPossible) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.dueAt = dueAt;
        this.pointsPossible = pointsPossible;
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public BigDecimal getPointsPossible() {
        return pointsPossible;
    }
}
