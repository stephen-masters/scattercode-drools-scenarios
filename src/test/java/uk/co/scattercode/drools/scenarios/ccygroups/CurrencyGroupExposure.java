package uk.co.scattercode.drools.scenarios.ccygroups;

import java.math.BigDecimal;

public class CurrencyGroupExposure {

	private String group;
	private BigDecimal exposure;

	public CurrencyGroupExposure() {
	}

	public CurrencyGroupExposure(String group, BigDecimal exposure) {
		this.group = group;
		this.exposure = exposure;
	}
	
	public CurrencyGroupExposure(String group, Number exposure) {
		this.group = group;
		this.exposure = new BigDecimal(exposure.doubleValue());
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public BigDecimal getExposure() {
		return exposure;
	}

	public void setExposure(BigDecimal exposure) {
		this.exposure = exposure;
	}
	
}
