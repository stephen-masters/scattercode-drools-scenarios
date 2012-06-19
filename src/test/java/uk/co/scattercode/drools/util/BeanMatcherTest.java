package uk.co.scattercode.drools.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.co.scattercode.drools.util.BeanMatcher;

/**
 * 
 * @author Stephen Masters
 */
public class BeanMatcherTest {

    @Test
    public void shouldLowercaseFirstLetter() {
        assertEquals("camelCase", BeanMatcher.lowercaseFirstLetter("CamelCase"));
        assertEquals("camelCase", BeanMatcher.lowercaseFirstLetter("camelCase"));
        assertEquals("", BeanMatcher.lowercaseFirstLetter(""));
        assertEquals(null, BeanMatcher.lowercaseFirstLetter(null));
    }
    
    @Test
    public void shouldMatchAnythingWithEmptyFilter() {
        assertTrue("An empty filter should always return a match.",
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {}));
    }
    
    @Test
    public void shouldMatchIfAllFilterPropertesInBean() {
        assertTrue("Expected 'getFoo' to return 'foo' and 'getBar' to return 'bar'.", 
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {"foo=foo", "bar=bar"}));
        assertTrue("Expected 'getBar' to return 'bar' and to be sufficient for a match.",
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {"bar=bar"}));
        assertTrue("An empty filter should always return true.",
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {}));
    }
    
    @Test
    public void shouldNotMatchIfFilterPropertyNotInBean() {
        assertFalse("There is no bean property 'uncle', so this should not match.",
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {"uncle=bob"}));
    }
    
    @Test
    public void shouldNotMatchIfFilterPropertyNotEqualToBeanProperty() {
        assertFalse("Bean property 'foo' has value 'foo', not 'bar'.",
                BeanMatcher.matches(new StubBean("foo", "bar"), new String[] {"foo=bar"}));
    }
    
    public class StubBean {
        private String foo;
        private String bar;
        public StubBean(String foo, String bar) {
            this.foo = foo; this.bar = bar;
        }
        public String getFoo() { return foo; }
        public String getBar() { return bar; }
    } 

}
