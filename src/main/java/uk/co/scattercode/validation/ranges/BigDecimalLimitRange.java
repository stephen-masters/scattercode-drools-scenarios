package uk.co.scattercode.validation.ranges;

import java.math.BigDecimal;

public class BigDecimalLimitRange implements LimitRange<BigDecimal> {
	
	private static final int EQUAL = 0;
	
	private BigDecimal min = null;
	private BigDecimal max = null;
	
	public BigDecimalLimitRange() {
	}
	
	public BigDecimalLimitRange(BigDecimal min, BigDecimal max) {
		this.setLimits(min, max);
	}
	
	@Override
	public BigDecimal breachAmount(BigDecimal value) {
		if (value.compareTo(this.min) < EQUAL) {
			return this.min.subtract(value);
		} else if (value.compareTo(this.max) > EQUAL) {
			return value.subtract(this.max);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	@Override
	public void setLimits(BigDecimal min, BigDecimal max) {
		if (min == null || max == null || min.compareTo(max) <= EQUAL) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;	
		}
	}
	
	@Override
	public int compareTo(BigDecimal value) {
		if (value == null)
			throw new NullPointerException("Attempted to compare range to a null value.");
		if (this.isBreachMin(value))
			return 1;
		if (this.isBreachMax(value))
			return -1;
		return 0;
	}

	@Override
	public boolean contains(BigDecimal value) {
		return this.compareTo(value) == 0;
	}
	
	@Override
	public String toString() {
		return "BigDecimalLimitRange:{min=" + min + ", max=" + max + "}";
	}
	
	@Override
	public BigDecimal getMax() {
		return max;
	}

	@Override
	public BigDecimal getMin() {
		return min;
	}
	
	private boolean isBreachMax(BigDecimal value) {
		if (value == null || this.max == null) return false;
		return value.compareTo(this.max) > EQUAL;
	}
	
	private boolean isBreachMin(BigDecimal value) {
		if (value == null || this.min == null) return false;
		return value.compareTo(this.min) < EQUAL;
	}

}
