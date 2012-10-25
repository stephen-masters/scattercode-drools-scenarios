package uk.co.scattercode.drools.scenarios.ccygroup;

import java.math.BigDecimal;

public class CurrencyExposure {

	private String currency;
	private BigDecimal exposure;

	public CurrencyExposure() {
	}

	public CurrencyExposure(String currency, BigDecimal exposure) {
		this.currency = currency;
		this.exposure = exposure;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getExposure() {
		return exposure;
	}

	public void setExposure(BigDecimal exposure) {
		this.exposure = exposure;
	}

}
