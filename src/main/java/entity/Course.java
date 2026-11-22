package entity;

/** Course identity and labels supplied by Quercus. */
public final class Course {
    private final String id;
    private final String code;
    private final String name;

    /** Creates a course with its Canvas ID, course code, and display name. */
    public Course(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
