package uk.co.scattercode.validation.ranges;

import java.math.BigDecimal;

import org.junit.Test;
import static org.junit.Assert.*;

public class ImprovementTest {

	@Test
	public void shouldIdentifyBounds() {
		BigDecimalLimitRange range = new BigDecimalLimitRange(new BigDecimal("-80.00"), new BigDecimal("80.00"));
		
		BigDecimal value = new BigDecimal("10.00");
		assertFalse(range.isBreachTop(value));
		assertFalse(range.isBreachBottom(value));
		assertFalse(range.isBreach(value));
		
		value = new BigDecimal("-90.00");
		assertFalse(range.isBreachTop(value));
		assertTrue(range.isBreachBottom(value));
		assertTrue(range.isBreach(value));
		
		value = new BigDecimal("90.00");
		assertTrue(range.isBreachTop(value));
		assertFalse(range.isBreachBottom(value));
		assertTrue(range.isBreach(value));
	}
	
    @Test
    public void shouldCalculateBreachAmount() {
    	BigDecimalLimitRange range = new BigDecimalLimitRange(new BigDecimal("-80.00"), new BigDecimal("80.00"));
		
		BigDecimal value = new BigDecimal("10.00");
		assertEquals(BigDecimal.ZERO, range.breachAmount(value));
		
		value = new BigDecimal("100.00");
		assertEquals(new BigDecimal("20.00"), range.breachAmount(value));
		
		value = new BigDecimal("-110.00");
		assertEquals(new BigDecimal("30.00"), range.breachAmount(value));
    }
	
	protected boolean isRejectButImproving(BigDecimal value, BigDecimal previousValue, BigDecimal shortLimit, BigDecimal longLimit) {
		BigDecimalLimitRange range = new BigDecimalLimitRange(shortLimit, longLimit);
		
		if (range.isBreach(value) 
				&& range.breachAmount(value)
				.compareTo(range.breachAmount(previousValue)) < 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
