package uk.co.scattercode.validation.ranges;

import org.junit.Test;
import static org.junit.Assert.*;

public class IntegerLimitRangeTest {

	@Test
	public void shouldIdentifyBounds() {
		IntegerLimitRange range = 
				new IntegerLimitRange(-80, 80);
		
		assertEquals(0, range.compareTo(10));
		assertEquals(1, range.compareTo(-90));
		assertEquals(-1, range.compareTo(90));
	}
	
    @Test
    public void shouldCalculateBreachAmount() {
    	IntegerLimitRange range = 
    			new IntegerLimitRange(-80, 80);
		
		assertEquals(Integer.valueOf(0), range.breachAmount(10));
		assertEquals(Integer.valueOf(20), range.breachAmount(100));
		assertEquals(Integer.valueOf(30), range.breachAmount(-110));
    }
	
}
