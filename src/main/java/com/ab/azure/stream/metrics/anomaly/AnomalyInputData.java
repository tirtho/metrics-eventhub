package com.ab.azure.stream.metrics.anomaly;

public class AnomalyInputData {
	@Override
	public String toString() {
		return "AnomalyInputData [timestamp=" + timestamp + ", value=" + value + "]";
	}
	String timestamp;
	long value;
	public AnomalyInputData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public AnomalyInputData(String timestamp, long value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}
	
}
