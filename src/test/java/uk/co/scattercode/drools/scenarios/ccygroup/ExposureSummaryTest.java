package uk.co.scattercode.drools.scenarios.ccygroup;

import static org.drools.builder.ResourceType.*;

import java.math.BigDecimal;

import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

import uk.co.scattercode.beans.BeanPropertyFilter;
import uk.co.scattercode.drools.test.AbstractStatefulSessionRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * This scenario 
 * 
 * @author Stephen Masters
 */
public class ExposureSummaryTest extends AbstractStatefulSessionRulesTest {

    @Override
    protected DroolsResource[] getResources() {
        return new DroolsResource[] { 
            new DroolsResource("scenarios/ccygroup/CurrencyExposures.drl", CLASSPATH, DRL)
        };
    }
    
    /**
	 * We don't define {@link CurrencyGroup} up front. Instead, when
	 * {@link CurrencyGroupCurrency} facts are inserted into working memory, 
	 * groups are created automatically, to cover all the mappings that exist.
	 */
    @Test
    public void shouldCreateCurrencyGroup() {
    	insert(new CurrencyGroupCurrency("G1", "GBP"));
    	insert(new CurrencyGroupCurrency("G1", "EUR"));
        fireAllRules();
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G1"));
    }
    
    /**
     * 
     */
    @Test
    public void shouldAggregateExposuresForGroup() {
    	insert(new CurrencyGroupCurrency("G1", "GBP"));
    	insert(new CurrencyGroupCurrency("G1", "EUR"));
    	insert(new CurrencyGroupCurrency("G1", "USD"));
    	insert(new CurrencyGroupCurrency("G2", "EUR"));
    	insert(new CurrencyGroupCurrency("G2", "USD"));
    	
    	FactHandle gbpHandle = insert(new CurrencyExposure("GBP", new BigDecimal("100000.00")));
    	FactHandle eurHandle = insert(new CurrencyExposure("EUR", new BigDecimal("50000.00")));
    	FactHandle usdHandle = insert(new CurrencyExposure("USD", new BigDecimal("7000.00")));
    	FactHandle myrHandle = insert(new CurrencyExposure("MYR", new BigDecimal("800.00")));
    	
        fireAllRules();
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G1"),
        		new BeanPropertyFilter("exposure", "157000"));
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G2"),
        		new BeanPropertyFilter("exposure", "57000"));
     
        update(gbpHandle, new CurrencyExposure("GBP", new BigDecimal("200000.00")));
        fireAllRules();
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G1"),
        		new BeanPropertyFilter("exposure", "257000"));
        
        update(myrHandle, new CurrencyExposure("MYR", new BigDecimal("900.00")));
        fireAllRules();
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G1"),
        		new BeanPropertyFilter("exposure", "257000"));
        
        insert(new CurrencyGroupCurrency("G1", "MYR"));
        fireAllRules();
        assertFactIsInWorkingMemory("CurrencyGroup", 
        		new BeanPropertyFilter("name", "G1"),
        		new BeanPropertyFilter("exposure", "257900"));
    }

}
