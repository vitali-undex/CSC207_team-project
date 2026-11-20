package use_case.view_deadlines_calendar;

import java.io.IOException;

/** Online source scoped to the connected Quercus account. */
public interface CalendarAssignmentDataAccessInterface {
    CalendarSnapshot loadAssignments() throws IOException;
}
