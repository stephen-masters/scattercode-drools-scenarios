package uk.co.scattercode.validation.ranges;

import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleLimitRangeTest {

	@Test
	public void shouldIdentifyBounds() {
		DoubleLimitRange range = 
				new DoubleLimitRange(-80d, 80d);
		
		assertEquals(0, range.compareTo(10d));
		assertEquals(1, range.compareTo(-90d));
		assertEquals(-1, range.compareTo(90d));
	}
	
    @Test
    public void shouldCalculateBreachAmount() {
    	DoubleLimitRange range = 
    			new DoubleLimitRange(-80d, 80d);
		
		assertEquals(Double.valueOf(0), range.breachAmount(10d));
		assertEquals(Double.valueOf(20), range.breachAmount(100d));
		assertEquals(Double.valueOf(30), range.breachAmount(-110d));
    }
	
}
