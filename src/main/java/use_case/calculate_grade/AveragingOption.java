package use_case.calculate_grade;

/**
 * An averaging rule the student can pick for a group, such as "By points".
 */
public class AveragingOption {

    private final String id;
    private final String name;

    /**
     * Creates an averaging option.
     *
     * @param id   the rule's id
     * @param name the rule's name as the student sees it
     */
    public AveragingOption(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
