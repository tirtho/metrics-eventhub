package com.ab.azure.stream.metrics.anomaly;

public class AnomalyOutputData {
	long timeInMillis;
	long actualDataLong;

	long expectedValue;

	boolean isAnomaly;
	boolean isNegativeAnomaly;
	boolean isPositiveAnomaly;

	long lowerMargin;
	long upperMargin;
	int period;
	int suggestedWindow;
	
	public AnomalyOutputData() {
		super();
		// TODO Auto-generated constructor stub
	}

	// TODO add a builder pattern to load data
	public AnomalyOutputData(long timeInMillis, long actualDataLong, long expectedDataLong, boolean isAnomaly,
			boolean isNegativeAnomaly, boolean isPositiveAnomaly, long lowerMargin, long upperMargin, int period,
			int suggestedWindow) {
		super();
		this.timeInMillis = timeInMillis;
		this.actualDataLong = actualDataLong;
		this.expectedValue = expectedDataLong;
		this.isAnomaly = isAnomaly;
		this.isNegativeAnomaly = isNegativeAnomaly;
		this.isPositiveAnomaly = isPositiveAnomaly;
		this.lowerMargin = lowerMargin;
		this.upperMargin = upperMargin;
		this.period = period;
		this.suggestedWindow = suggestedWindow;
	}

	public long getTimeInMillis() {
		return timeInMillis;
	}

	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	public long getActualDataLong() {
		return actualDataLong;
	}

	public void setActualDataLong(long actualDataLong) {
		this.actualDataLong = actualDataLong;
	}

	public long getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(long expectedDataLong) {
		this.expectedValue = expectedDataLong;
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
