package com.ab.azure.stream.metrics.anomaly;

import java.util.List;

public class AnomalyDetectorResponseArrayData {
	List<Double> expectedValues;
	List<Boolean> isAnomaly;
	List<Boolean> isNegativeAnomaly;
	List<Boolean> isPositiveAnomaly;
	List<Double> lowerMargins;
	List<Double> upperMargins;
	int period;
	public AnomalyDetectorResponseArrayData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public List<Double> getExpectedValues() {
		return expectedValues;
	}
	public void setExpectedValues(List<Double> expectedValues) {
		this.expectedValues = expectedValues;
	}
	public List<Boolean> getIsAnomaly() {
		return isAnomaly;
	}
	public void setIsAnomaly(List<Boolean> isAnomaly) {
		this.isAnomaly = isAnomaly;
	}
	public List<Boolean> getIsNegativeAnomaly() {
		return isNegativeAnomaly;
	}
	public void setIsNegativeAnomaly(List<Boolean> isNegativeAnomaly) {
		this.isNegativeAnomaly = isNegativeAnomaly;
	}
	public List<Boolean> getIsPositiveAnomaly() {
		return isPositiveAnomaly;
	}
	public void setIsPositiveAnomaly(List<Boolean> isPositiveAnomaly) {
		this.isPositiveAnomaly = isPositiveAnomaly;
	}
	public List<Double> getLowerMargins() {
		return lowerMargins;
	}
	public void setLowerMargins(List<Double> lowerMargins) {
		this.lowerMargins = lowerMargins;
	}
	public List<Double> getUpperMargins() {
		return upperMargins;
	}
	public void setUpperMargins(List<Double> upperMargins) {
		this.upperMargins = upperMargins;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	

}
