package interface_adapter.tasks;

/**
 * One entry in the course menu of the tasks tab.
 */
public class CourseOption {

    /** The first entry in the menu, for a task that is not linked to a course. */
    public static final CourseOption NONE = new CourseOption(null, "None");

    private final String courseId;
    private final String label;

    /**
     * Creates a course menu entry.
     *
     * @param courseId the course's id, or null for no course
     * @param label    what the menu shows
     */
    public CourseOption(String courseId, String label) {
        this.courseId = courseId;
        this.label = label;
    }

    /**
     * Returns the id of the course this entry stands for.
     *
     * @return the course id, or null for no course
     */
    public String getCourseId() {
        return courseId;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the label, so a combo box shows it. */
    @Override
    public String toString() {
        return label;
    }
}
