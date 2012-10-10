package uk.co.scattercode.validation.ranges;

/**
 * Generic interface for ranges which define top and bottom limits.
 * 
 * @author Stephen Masters
 *
 * @param <T>
 */
public interface LimitRange<T> {

	public T breachAmount(T value);
	
	public boolean isBreach(T value);
	
	public boolean isBreachTop(T value);
	
	public boolean isBreachBottom(T value);

	public T getTopLimit();

	public void setTopLimit(T topLimit);

	public T getBottomLimit();

	public void setBottomLimit(T bottomLimit);
	
}
