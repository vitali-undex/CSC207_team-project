package interface_adapter.calculate_grade;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChoiceTest {

    @Test
    void showsItsLabelAndIsEqualById() {
        Choice choice = new Choice("equal", "Equally");

        assertEquals("equal", choice.getId());
        assertEquals("Equally", choice.getLabel());
        assertEquals("Equally", choice.toString());
        assertEquals(new Choice("equal", "Each counts the same"), choice);
        assertEquals(new Choice("equal", "x").hashCode(), choice.hashCode());
        assertNotEquals(new Choice("points", "Equally"), choice);
        assertFalse(choice.equals("equal"));
    }

    @Test
    void idAndLabelAreRequired() {
        assertThrows(NullPointerException.class, () -> new Choice(null, "Equally"));
        assertThrows(NullPointerException.class, () -> new Choice("equal", null));
    }
}
