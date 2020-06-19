package com.ab.azure.stream.metrics.anomaly;

public class AnomalyResponseData {
	long expectedValue;

	boolean isAnomaly;
	boolean isNegativeAnomaly;
	boolean isPositiveAnomaly;

	long lowerMargin;
	long upperMargin;
	int period;
	int suggestedWindow;
	
	public AnomalyResponseData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getExpectedValue() {
		return expectedValue;
	}
	public void setExpectedValue(long expectedValue) {
		this.expectedValue = expectedValue;
	}
	public boolean getIsAnomaly() {
		return isAnomaly;
	}
	public void setIsAnomaly(boolean isAnomaly) {
		this.isAnomaly = isAnomaly;
	}
	public boolean getIsNegativeAnomaly() {
		return isNegativeAnomaly;
	}
	public void setIsNegativeAnomaly(boolean isNegativeAnomaly) {
		this.isNegativeAnomaly = isNegativeAnomaly;
	}
	public boolean getIsPositiveAnomaly() {
		return isPositiveAnomaly;
	}
	public void setIsPositiveAnomaly(boolean isPositiveAnomaly) {
		this.isPositiveAnomaly = isPositiveAnomaly;
	}
	public long getLowerMargin() {
		return lowerMargin;
	}
	public void setLowerMargin(long lowerMargin) {
		this.lowerMargin = lowerMargin;
	}
	public long getUpperMargin() {
		return upperMargin;
	}
	public void setUpperMargin(long upperMargin) {
		this.upperMargin = upperMargin;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getSuggestedWindow() {
		return suggestedWindow;
	}
	public void setSuggestedWindow(int suggestedWindow) {
		this.suggestedWindow = suggestedWindow;
	}

}
