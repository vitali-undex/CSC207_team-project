package use_case.view_deadlines_calendar;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public final class DeadlineSummary {
    private final String course;
    private final String title;
    private final ZonedDateTime dueAt;
    private final BigDecimal pointsPossible;

    public DeadlineSummary(String course, String title, ZonedDateTime dueAt, BigDecimal pointsPossible) {
        this.course = course;
        this.title = title;
        this.dueAt = dueAt;
        this.pointsPossible = pointsPossible;
    }

    public String getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public ZonedDateTime getDueAt() {
        return dueAt;
    }

    public BigDecimal getPointsPossible() {
        return pointsPossible;
    }
}
