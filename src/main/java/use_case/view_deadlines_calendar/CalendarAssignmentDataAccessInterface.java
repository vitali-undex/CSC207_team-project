package use_case.view_deadlines_calendar;

import java.io.IOException;

/** Online source scoped to the connected Quercus account. */
public interface CalendarAssignmentDataAccessInterface {
    /** Loads a snapshot of active courses and their assignments. */
    CalendarSnapshot loadAssignments() throws IOException;
}
