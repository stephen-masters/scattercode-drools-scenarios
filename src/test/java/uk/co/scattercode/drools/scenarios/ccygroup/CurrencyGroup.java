package uk.co.scattercode.drools.scenarios.ccygroup;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CurrencyGroup {

	/**
	 * The name of the group.
	 */
	private String name;
	
	/**
	 * The currencies in this group.
	 */
	private Set<String> currencies = new HashSet<String>();
	
	private BigDecimal exposure = BigDecimal.ZERO;

	public CurrencyGroup() {
	}

	public CurrencyGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getCurrencies() {
		return Collections.unmodifiableSet(currencies);
	}
	
	/**
	 * Reinitialises the set of currencies with the values provided.
	 * Note that a new {@link Set} is created containing the values, 
	 * so subsequent modifications to the original collection will 
	 * have no effect on its contents. If you wish to modify the list,
	 * you should make use of the add/remove methods.  
	 * 
	 * @param currencies A collection of currencies.
	 */
	public void setCurrencies(Collection<String> currencies) {
		this.currencies = new HashSet<String>();
		this.addCurrencies(currencies);
	}
	
	/**
	 * Adds currencies to the existing {@link Set}.
	 * 
	 * @param currencies A {@link Collection} of currencies.
	 */
	public void addCurrencies(Collection<String> currencies) {
		this.currencies.addAll(currencies);
	}
	
	public void addCurrency(String currency) {
		this.currencies.add(currency);
	}
	
	public void removeCurrency(String currency) {
		this.currencies.remove(currency);
	}

	public BigDecimal getExposure() {
		return exposure;
	}

	public void setExposure(BigDecimal exposure) {
		this.exposure = exposure;
	}

}
