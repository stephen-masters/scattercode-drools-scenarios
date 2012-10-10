package uk.co.scattercode.validation.ranges;

import java.math.BigDecimal;

public class BigDecimalLimitRange implements LimitRange<BigDecimal> {
	
	private static final int EQUAL = 0;
	
	private BigDecimal topLimit = null;
	private BigDecimal bottomLimit = null;
	
	public BigDecimalLimitRange() {
		
	}
	
	public BigDecimalLimitRange(BigDecimal bottomLimit, BigDecimal topLimit) {
		this.bottomLimit = bottomLimit;
		this.topLimit = topLimit;
	}
	
	public BigDecimal breachAmount(BigDecimal value) {
		if (value.compareTo(this.topLimit) > EQUAL) {
			return value.subtract(this.topLimit);
		} else if (value.compareTo(this.bottomLimit) < EQUAL) {
			return this.bottomLimit.subtract(value);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	public boolean isBreach(BigDecimal value) {
		return isBreachTop(value) || isBreachBottom(value);
	}
	
	public boolean isBreachTop(BigDecimal value) {
		if (value == null || this.topLimit == null) return false;
		return value.compareTo(this.topLimit) > EQUAL;
	}
	public boolean isBreachBottom(BigDecimal value) {
		if (value == null || this.bottomLimit == null) return false;
		return value.compareTo(this.bottomLimit) < EQUAL;
	}

	public BigDecimal getTopLimit() {
		return topLimit;
	}

	public void setTopLimit(BigDecimal topLimit) {
		this.topLimit = topLimit;
	}

	public BigDecimal getBottomLimit() {
		return bottomLimit;
	}

	public void setBottomLimit(BigDecimal bottomLimit) {
		this.bottomLimit = bottomLimit;
	}
	
}
