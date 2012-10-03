package uk.co.scattercode.drools.scenarios.ccygroups;

public class CurrencyGroupCurrency {

	private String currency;
	private String group;

	public CurrencyGroupCurrency() {
	}

	public CurrencyGroupCurrency(String group, String currency) {
		this.group = group;
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public String toString() {
		return "CurrencyGroupCurrency:{ group=" + group + ", ccy=" + currency + "}";
	}
}
