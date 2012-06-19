package uk.co.scattercode.drools.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class designed to validate whether beans contain all the properties
 * and values defined in a set of filters.
 * 
 * @author Stephen Masters
 */
public class BeanMatcher {

    private static Logger log = LoggerFactory.getLogger(BeanMatcher.class);
    
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
    public static boolean matches(Object bean, String[] filters) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> beanProperties = BeanUtils.describe(bean);
            return matches(beanProperties, parseFilters(filters));
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public static boolean matches(Object bean, Map<String, Object> filters) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> beanProperties = BeanUtils.describe(bean);
            return matches(beanProperties, filters);
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Can all key/value pairs in the filters be found in the bean properties?
     * If any filter is not in the bean properties or the value differs, then
     * this will return false.
     */
    public static boolean matches(Map<String, Object> beanProperties, Map<String, Object> filters) {
        logComparison(beanProperties, filters);
        for(String key : filters.keySet()) {
            if (!beanProperties.containsKey(key) 
                    ||  !filters.get(key).equals(beanProperties.get(key))) {
                return false;
            }
        }
        // None of the filters failed to match, so it must be okay.
        return true;
    }
    
    private static void logComparison(Map<String, Object> beanProperties, Map<String, Object> filters) {
        StringBuilder filterValues = new StringBuilder();
        StringBuilder beanValues = new StringBuilder();
        
        for(String key : filters.keySet()) {
            filterValues.append(key + "=" + filters.get(key) + ",");
            if (beanProperties.containsKey(key)) {
                beanValues.append(key + "=" + beanProperties.get(key) + "," );
            } else {
                beanValues.append("No property.");
            }
        }
        
        log.debug("Matching filters: " + filterValues.toString() + "\nto bean properties:" + beanValues.toString());
    }

    /**
     * When a fact is retrieved from the working memory as a bean and we parse
     * its get/set methods, all properties have their first letter in lower
     * case. However, it's much more readable (particularly in the tests) if we
     * define the field we're looking for with the same text as that associated
     * with the fact. i.e. "Last_Updated" rather than "last_Updated". So we
     * lower-case the first letter of the property defined in the filters so
     * that it doesn't matter.
     * <p>
     * This is partly why this is in our Drools utility package rather than a
     * fully generic bean utility package. We're deliberately losing the ability
     * to perform case-sensitive matches where the first letter makes a
     * difference.
     * </p>
     */
    protected static String lowercaseFirstLetter(String text) {
        if (text == null || text.length() == 0) {
            return text;
        } else {
            return text.substring(0, 1).toLowerCase() + text.substring(1, text.length());
        }
    }
    
    /**
     * Takes an array of <code>String</code> defined like so:
     * <pre>
     *   String[] filters = { &quot;uncle=Bob&quot;, &quot;aunt=Fanny&quot; };
     * </pre>
     * Converts this into a Map of String keys to Object values by splitting
     * each element on the "=" symbol.
     */
    private static Map<String, Object> parseFilters(String[] filters) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String filter : filters) {
            String[] keyval = filter.split("=");
            if (keyval.length != 2) {
                throw new IllegalArgumentException(
                  "Filter [" + filter + "] does not parse to a key/value pair.");
            }
            map.put(lowercaseFirstLetter(keyval[0]), keyval[1]);
        }
        return map;
    }
    
}
