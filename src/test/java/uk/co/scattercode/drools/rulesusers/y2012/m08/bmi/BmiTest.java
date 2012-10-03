package uk.co.scattercode.drools.rulesusers.y2012.m08.bmi;

import static org.drools.builder.ResourceType.*;
import org.junit.Test;

import uk.co.scattercode.drools.util.AbstractStatefulSessionRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * Test to help out with query from "wichtl" on Drools user list:
 *     "rules not being reconsidered after modify"
 * 
 * @author Stephen Masters
 */
public class BmiTest extends AbstractStatefulSessionRulesTest {

	@Override
	protected DroolsResource[] getResources() {
		return new DroolsResource[] { new DroolsResource(
				"rulesusers/y2012/m08/bmi/Bmi.drl", CLASSPATH, DRL) };
	}

	/**
	 * Just fire off the rules. There's a rule in the DRL which will insert test
	 * facts.
	 */
	@Test
	public void shouldCheckBmi() throws FactMarshallingException {

		knowledgeEnvironment.knowledgeSession.fireAllRules();

	}

}
