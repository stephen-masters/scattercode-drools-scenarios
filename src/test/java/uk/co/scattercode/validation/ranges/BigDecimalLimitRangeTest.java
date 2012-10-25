package uk.co.scattercode.validation.ranges;

import java.math.BigDecimal;

import org.junit.Test;
import static org.junit.Assert.*;

public class BigDecimalLimitRangeTest {

	@Test
	public void shouldIdentifyBounds() {
		BigDecimalLimitRange range = 
				new BigDecimalLimitRange(new BigDecimal("-80.00"), new BigDecimal("80.00"));
		
		assertEquals(0, range.compareTo(new BigDecimal("10.00")));
		assertEquals(1, range.compareTo(new BigDecimal("-90.00")));
		assertEquals(-1, range.compareTo(new BigDecimal("90.00")));
	}
	
    @Test
    public void shouldCalculateBreachAmount() {
    	BigDecimalLimitRange range = 
    			new BigDecimalLimitRange(new BigDecimal("-80.00"), new BigDecimal("80.00"));
		
		assertEquals(BigDecimal.ZERO, range.breachAmount(new BigDecimal("10.00")));
		assertEquals(new BigDecimal("20.00"), range.breachAmount(new BigDecimal("100.00")));
		assertEquals(new BigDecimal("30.00"), range.breachAmount(new BigDecimal("-110.00")));
    }
	
}
