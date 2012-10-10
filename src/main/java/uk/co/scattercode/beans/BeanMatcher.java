package uk.co.scattercode.beans;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This class provides some general utility methods to make it easier to match
 * whether particular fields and values exist on a bean. Because it uses the
 * Apache commons {@link BeanUtils}, it performs matches on the string values
 * rather than the actual object values. i.e.:
 * <pre>
 *     o1.property.toString().equals(filterValue.toString())
 * </pre>
 * 
 * @author Stephen Masters
 */
public interface BeanMatcher {
	
	/**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     * 
     * The filters should be defined as an array of String, where each property
     * name/value pair is a String, like so:
     * <pre>
     *   String[] filters = { &quot;uncle=Bob&quot;, &quot;aunt=Fanny&quot; };
     * </pre>
     * This is a convenience method so that matchers can be defined with minimal LOC.
     */
    public boolean matches(Object bean, String[] filters);
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     * 
     * @param bean The bean to examine.
     * @param filters Vararg list of filters.
     * @return True if all the filter properties match on the bean.
     */
    public boolean matches(Object bean, BeanPropertyFilter... filters);
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public boolean matches(Object bean, Map<String, Object> filters);

    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public boolean matches(Map<String, Object> beanProperties, Map<String, Object> filters);
    
}
