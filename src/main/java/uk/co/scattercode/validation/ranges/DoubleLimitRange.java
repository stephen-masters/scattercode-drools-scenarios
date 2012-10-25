package uk.co.scattercode.validation.ranges;

/**
 * 
 * @author Stephen Masters
 */
public class DoubleLimitRange implements LimitRange<Double> {
	
	private static final int EQUAL = 0;
	
	private Double min = null;
	private Double max = null;
	
	public DoubleLimitRange() {
	}
	
	public DoubleLimitRange(Double min, Double max) {
		this.setLimits(min, max);
	}
	
	@Override
	public Double breachAmount(Double value) {
		if (value.compareTo(this.min) < EQUAL) {
			return this.min - value;
		} else if (value.compareTo(this.max) > EQUAL) {
			return value - this.max;
		} else {
			return Double.valueOf(0d);
		}
	}
	
	@Override
	public void setLimits(Double min, Double max) {
		if (min == null || max == null || min.compareTo(max) <= EQUAL) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;	
		}
	}
	
	@Override
	public int compareTo(Double value) {
		if (value == null)
			throw new NullPointerException("Attempted to compare range to a null value.");
		if (this.isBreachMin(value))
			return 1;
		if (this.isBreachMax(value))
			return -1;
		return 0;
	}

	@Override
	public boolean contains(Double value) {
		return this.compareTo(value) == EQUAL;
	}
	
	@Override
	public String toString() {
		return "BigDecimalLimitRange:{min=" + min + ", max=" + max + "}";
	}
	
	@Override
	public Double getMax() {
		return max;
	}

	@Override
	public Double getMin() {
		return min;
	}
	
	private boolean isBreachMax(Double value) {
		if (value == null || this.max == null) return false;
		return value.compareTo(this.max) > EQUAL;
	}
	
	private boolean isBreachMin(Double value) {
		if (value == null || this.min == null) return false;
		return value.compareTo(this.min) < EQUAL;
	}

}
