package interface_adapter.calculate_grade;

import java.util.Objects;

/**
 * One item in a drop-down list: an id for the program and a label for the
 * student. Two choices are equal when their ids are.
 */
public class Choice {

    private final String id;
    private final String label;

    /**
     * Creates a choice.
     *
     * @param id    the id passed back to the controller, not null
     * @param label the text the student sees, not null
     */
    public Choice(String id, String label) {
        this.id = Objects.requireNonNull(id, "id");
        this.label = Objects.requireNonNull(label, "label");
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Returns the label, which is what a drop-down list shows.
     *
     * @return the label
     */
    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Choice && id.equals(((Choice) other).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
