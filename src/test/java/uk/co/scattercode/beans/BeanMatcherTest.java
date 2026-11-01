package uk.co.scattercode.beans;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import demos.validate.FirstName;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BeanMatcherTest {

    private final BeanMatcher matcher = new BeanMatcher();

    private static FirstName john() {
        FirstName name = new FirstName();
        name.setValue("John");
        name.setValid(true);
        return name;
    }

    @Test
    void stringFiltersMatchOnTheStringValueOfEachProperty() {
        assertTrue(matcher.matches(john(), new String[] {"value=John", "valid=true"}));
        assertFalse(matcher.matches(john(), new String[] {"value=John", "valid=false"}));
    }

    @Test
    void theFirstLetterOfAStringFilterIsLowerCased() {
        assertTrue(matcher.matches(john(), new String[] {"Value=John"}));
    }

    @Test
    void aFilterOnAPropertyTheBeanDoesNotHaveDoesNotMatch() {
        assertFalse(matcher.matches(john(), new String[] {"surname=Smith"}));
    }

    @Test
    void noFiltersMatchAnything() {
        assertTrue(matcher.matches(john(), new String[0]));
        assertTrue(matcher.matches(john(), new BeanPropertyFilter[0]));
    }

    @Test
    void aStringFilterThatIsNotAKeyValuePairIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> matcher.matches(john(), new String[] {"value"}));
    }

    @Test
    void propertyFiltersCompareStringValues() {
        assertTrue(matcher.matches(john(), new BeanPropertyFilter("valid", Boolean.TRUE)));
        assertTrue(matcher.matches(john(), new BeanPropertyFilter("valid", "true")));
        assertFalse(matcher.matches(john(), new BeanPropertyFilter("value", "Jane")));
    }

    @Test
    void propertyMapsCanBeMatchedDirectly() {
        assertTrue(matcher.matches(Map.<String, Object>of("a", 1, "b", "two"), Map.<String, Object>of("a", "1")));
        assertFalse(matcher.matches(Map.<String, Object>of("a", 1), Map.<String, Object>of("a", "2")));
    }
}
