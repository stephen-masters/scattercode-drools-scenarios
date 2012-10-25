package uk.co.scattercode.validation.ranges;

/**
 * Generic interface for ranges which define top and bottom limits.
 * 
 * @author Stephen Masters
 *
 * @param <T>
 */
public interface LimitRange<T> extends Comparable<T> {

	/**
	 * If the value breaches the range, the size of the breach is returned.
	 * Otherwise, a zero value will be returned if the value is within the
	 * range.
	 * 
	 * @param value The value to test.
	 * @return The size of breach, or zero if there is no breach.
	 */
	public T breachAmount(T value);
	
	/**
	 * Determine where the range sits in comparison to a value.
	 * <pre>
	 *     -1  :  max   &lt;  value
	 *      0  :  min   &lt;= value &lt;= max
	 *      1  :  value &lt;  min
	 * </pre>
	 * 
	 * Note that a zero response does not evaluate to <code>equals</code>. It
	 * indicates that the value is within the range.
	 * 
	 * @return -1, 0 or 1 depending on whether the range is less than, contains
	 *         or greater than the value.
	 */
	@Override
	public int compareTo(T value);
	
	/**
	 * Is the value within the range:
	 * <pre>
	 * 	   min &lt;= value &lt;= max
	 * </pre>
	 * 
	 * @param value The value to test.
	 * @return Is the value in the range.
	 */
	public boolean contains(T value);

	/**
	 * @return The upper limit of the range.
	 */
	public T getMax();

	/**
	 * @return The lower limit of the range.
	 */
	public T getMin();
	
	/**
	 * Define the limits of this range. Although the args are named min &amp;
	 * max, if they are provided in the wrong order, the lower of the two will
	 * always be assigned to <code>min</code>, and the higher will be assigned to <code>max</code>.
	 * 
	 * @param min
	 *            The lower limit in the range.
	 * @param max
	 *            The upper limit of the range.
	 */
	public void setLimits(T min, T max);
}
