package uk.co.scattercode.validation.ranges;

/**
 * 
 * @author Stephen Masters
 */
public class IntegerLimitRange implements LimitRange<Integer> {
	
	private static final int EQUAL = 0;
	private static final Integer ZERO = new Integer(0);
	
	private Integer min = null;
	private Integer max = null;
	
	public IntegerLimitRange() {
	}
	
	public IntegerLimitRange(Integer min, Integer max) {
		this.setLimits(min, max);
	}
	
	@Override
	public Integer breachAmount(Integer value) {
		if (value.compareTo(this.min) < EQUAL) {
			return this.min - value;
		} else if (value.compareTo(this.max) > EQUAL) {
			return value - this.max;
		} else {
			return ZERO;
		}
	}
	
	@Override
	public void setLimits(Integer min, Integer max) {
		if (min == null || max == null || min.compareTo(max) <= EQUAL) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;	
		}
	}
	
	@Override
	public int compareTo(Integer value) {
		if (value == null)
			throw new NullPointerException("Attempted to compare range to a null value.");
		if (this.isBreachMin(value))
			return 1;
		if (this.isBreachMax(value))
			return -1;
		return ZERO;
	}

	@Override
	public boolean contains(Integer value) {
		return this.compareTo(value) == EQUAL;
	}
	
	@Override
	public String toString() {
		return "BigDecimalLimitRange:{min=" + min + ", max=" + max + "}";
	}
	
	@Override
	public Integer getMax() {
		return max;
	}

	@Override
	public Integer getMin() {
		return min;
	}
	
	private boolean isBreachMax(Integer value) {
		if (value == null || this.max == null) return false;
		return value.compareTo(this.max) > EQUAL;
	}
	
	private boolean isBreachMin(Integer value) {
		if (value == null || this.min == null) return false;
		return value.compareTo(this.min) < EQUAL;
	}

}
