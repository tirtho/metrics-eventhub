package com.ab.azure.stream.eventhub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ab.azure.stream.metrics.Metrics;
import com.ab.azure.stream.metrics.anomaly.AnomalyInputData;
import com.ab.azure.stream.metrics.anomaly.AzureAnomalyDetectorRequestData;
import com.ab.azure.stream.metrics.vm.VMMetrics;
import com.ab.azure.stream.metrics.vm.VMMetricsData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ABEventInsightTrainer {
	private static final String TRAINING_DATA_IN_JSON_TXT = "trainingDataInJson.txt";
	private static final int MINUTELY_TRAINING_INTERVAL = 60000;
	private static final String MINUTELY = "minutely";
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private EventHubProducerClient producer;
	private ABEventInsight eventInsight;
	private AzureAnomalyDetectorRequestData requestDataObject;
	int INITIAL_DELAY = 10;
	Metrics metricsCollector;
	
	public ABEventInsightTrainer(
			String connectionString, 
			String eventHubName, 
			int sendIntervalMillis,
			String anomalyDetectorApiAddress,
			String anomalyDetectorEndpoint,
			String anomalyDetectorKey)  {
		this.metricsCollector = new VMMetrics();
		this.eventInsight = new ABEventInsight(anomalyDetectorApiAddress, anomalyDetectorEndpoint, anomalyDetectorKey);
		
        final Runnable send = new Runnable() {
        	public void run () {
        		send();
        	}   	
        };
        scheduler.scheduleAtFixedRate(send, INITIAL_DELAY, MINUTELY_TRAINING_INTERVAL, TimeUnit.MILLISECONDS);
	}
	
	public void send() {
		try {
			String requestData = new String(Files.readAllBytes(Paths.get(TRAINING_DATA_IN_JSON_TXT)), "utf-8");
			String anomalyResponseString = eventInsight.getAnomaly(requestData);
			System.out.printf("[%s] Training data sent to Anomaly Detector: %s\n", ZonedDateTime.now(), anomalyResponseString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendToRefactor() {
        String vmMetricsDataString = metricsCollector.getMetrics();
        if (vmMetricsDataString == null) {
        	System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), "Failed to collect metrics");
        	return;
        }

        ObjectMapper obj = new ObjectMapper();
		VMMetricsData vmx;
		try {
			vmx = obj.readValue(vmMetricsDataString, VMMetricsData.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// Running anomaly detection on the VM's available storage space
		long valueToDetect = vmx.getAvailableStorageSpace();
		AnomalyInputData inputData = new AnomalyInputData(Instant.ofEpochMilli(vmx.getTimeInMillis()).toString(), 
				valueToDetect);
		if (requestDataObject == null) {
			requestDataObject = new AzureAnomalyDetectorRequestData(MINUTELY, inputData);
			// Need at least 12 data points in the series, before calling Anomaly Detector
			// So, returning for next round of data collection
			String collectedTrainingData;
			try {
				collectedTrainingData = obj.writeValueAsString(requestDataObject);
				System.out.printf("[%s] Training data collected: %s\n", ZonedDateTime.now(), collectedTrainingData);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		if (requestDataObject.getSeries().size() < 12) {
			requestDataObject.setAnomalyInputData(inputData);
			// Need at least 12 data points in the series, before calling Anomaly Detector
			// So, returning for next round of data collection
			String collectedTrainingData;
			try {
				collectedTrainingData = obj.writeValueAsString(requestDataObject);
				System.out.printf("[%s] Training data collected: %s\n", ZonedDateTime.now(), collectedTrainingData);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try {
			String anomalyResponseString = eventInsight.getAnomaly(obj.writeValueAsString(requestDataObject));
			System.out.printf("[%s] Training data sent to Anomaly Detector: %s\n", ZonedDateTime.now(), anomalyResponseString);
			// Reset the training data list for next round
			requestDataObject = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public void close() {
		if (producer != null) {
			producer.close();
		}
	}
}
