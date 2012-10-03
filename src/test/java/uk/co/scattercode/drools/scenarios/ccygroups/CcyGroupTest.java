package uk.co.scattercode.drools.scenarios.ccygroups;

import static org.drools.builder.ResourceType.*;

import java.math.BigDecimal;

import org.junit.Test;

import uk.co.scattercode.drools.util.AbstractStatefulSessionRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * 
 * @author Stephen Masters
 */
public class CcyGroupTest extends AbstractStatefulSessionRulesTest {

    @Override
    protected DroolsResource[] getResources() {
        return new DroolsResource[] { 
            new DroolsResource("scenarios/ccygroups/CcyGroups.drl", CLASSPATH, DRL)
        };
    }
    
    /**
     * 
     */
    @Test
    public void shouldAggregateExposuresForGroup() throws FactMarshallingException {

    	insert(new CurrencyGroupCurrency("G1", "GBP"));
    	insert(new CurrencyGroupCurrency("G1", "EUR"));
    	insert(new CurrencyGroupCurrency("G1", "USD"));
    	insert(new CurrencyGroupCurrency("G2", "EUR"));
    	insert(new CurrencyGroupCurrency("G2", "USD"));
    	
    	insert(new CurrencyExposure("GBP", new BigDecimal("100000.00")));
    	insert(new CurrencyExposure("EUR", new BigDecimal("50000.00")));
    	insert(new CurrencyExposure("USD", new BigDecimal("7000.00")));
    	
        fireAllRules();
       
    }

}
