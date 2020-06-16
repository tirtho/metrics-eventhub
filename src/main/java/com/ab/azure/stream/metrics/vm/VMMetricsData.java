package com.ab.azure.stream.metrics.vm;

public class VMMetricsData {
	public void VMMetrics() {};
	public VMMetricsData(long timeInMillis, double temperature, long processorLoadUser, long availableMemory,
			int processCount, long availableStorageSpace) {
		super();
		this.timeInMillis = timeInMillis;
		this.temperature = temperature;
		this.processorLoadUser = processorLoadUser;
		this.availableMemory = availableMemory;
		this.processCount = processCount;
		this.availableStorageSpace = availableStorageSpace;
	}

	public long getTimeInMillis() {
		return timeInMillis;
	}
	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public long getProcessorLoadUser() {
		return processorLoadUser;
	}
	public void setProcessorLoadUser(long processorLoadUser) {
		this.processorLoadUser = processorLoadUser;
	}
	public long getAvailableMemory() {
		return availableMemory;
	}
	public void setAvailableMemory(long availableMemory) {
		this.availableMemory = availableMemory;
	}
	public int getProcessCount() {
		return processCount;
	}
	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}
	public long getAvailableStorageSpace() {
		return availableStorageSpace;
	}
	public void setAvailableStorageSpace(long availableStorageSpace) {
		this.availableStorageSpace = availableStorageSpace;
	}

	long timeInMillis;
	double temperature;
	long processorLoadUser;
	long availableMemory;
	int processCount;
	long availableStorageSpace;
}
