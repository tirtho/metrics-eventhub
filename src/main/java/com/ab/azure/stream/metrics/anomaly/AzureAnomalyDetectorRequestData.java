package com.ab.azure.stream.metrics.anomaly;

import java.util.ArrayList;
import java.util.List;

public class AzureAnomalyDetectorRequestData {
	String granularity;
	List<AnomalyInputData> series;
	public AzureAnomalyDetectorRequestData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AzureAnomalyDetectorRequestData(String granularity, List<AnomalyInputData> series) {
		super();
		this.granularity = granularity;
		this.series = series;
	}
	public AzureAnomalyDetectorRequestData(String granularity, AnomalyInputData inputData) {
		super();
		this.granularity = granularity;
		series = new ArrayList<AnomalyInputData>();
		series.add(inputData);
	}
	public String getGranularity() {
		return granularity;
	}
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	public List<AnomalyInputData> getSeries() {
		return series;
	}
	public void setSeries(List<AnomalyInputData> series) {
		this.series = series;
	}
	public void setAnomalyInputData(AnomalyInputData anomalyInputData) {
		if (series == null) {
			series = new ArrayList<AnomalyInputData>();
		}
		series.add(anomalyInputData);
	}
	@Override
	public String toString() {
		return "AzureAnomalyDetectorRequestData [granularity=" + granularity + ", series=" + series + "]";
	}

}

