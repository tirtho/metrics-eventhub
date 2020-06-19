package com.ab.azure.stream.eventhub;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ab.azure.stream.metrics.Metrics;
import com.ab.azure.stream.metrics.anomaly.AnomalyInputData;
import com.ab.azure.stream.metrics.anomaly.AnomalyOutputData;
import com.ab.azure.stream.metrics.anomaly.AnomalyResponseData;
import com.ab.azure.stream.metrics.anomaly.AzureAnomalyDetectorRequestData;
import com.ab.azure.stream.metrics.vm.VMMetrics;
import com.ab.azure.stream.metrics.vm.VMMetricsData;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ABEventInsightProducer {
	private static final String MINUTELY = "minutely";
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private EventHubProducerClient producer;
	private ABEventInsight eventInsight;
	int INITIAL_DELAY = 10;
	Metrics metricsCollector;
	private AzureAnomalyDetectorRequestData requestDataObject;

	public ABEventInsightProducer(
			String connectionString, 
			String eventHubName, 
			int sendIntervalMillis,
			String anomalyDetectorApiAddress,
			String anomalyDetectorEndpoint,
			String anomalyDetectorKey)  {
		this.metricsCollector = new VMMetrics();
		this.eventInsight = new ABEventInsight(anomalyDetectorApiAddress, anomalyDetectorEndpoint, anomalyDetectorKey);
		
        this.producer = new EventHubClientBuilder()
	            .connectionString(connectionString, eventHubName)
	            .buildProducerClient();
        final Runnable send = new Runnable() {
        	public void run () {
        		send();
        	}   	
        };
        scheduler.scheduleAtFixedRate(send, INITIAL_DELAY, sendIntervalMillis, TimeUnit.MILLISECONDS);
	}
	
	public void send() {
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
		AnomalyInputData inputData = new AnomalyInputData(Instant.ofEpochMilli(vmx.getTimeInMillis()).truncatedTo(ChronoUnit.MINUTES).toString(), 
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
			System.out.printf("[%s] Anomaly data obtained from Anomaly Detector: %s\n", ZonedDateTime.now(), anomalyResponseString);
			// Now send all the metrics to EventHub
			AnomalyResponseData responseData = obj.readValue(anomalyResponseString, AnomalyResponseData.class);
			long periodStart = Instant.parse(requestDataObject.getSeries().get(0).getTimestamp()).toEpochMilli();
			long periodEnd = periodStart;
			for (AnomalyInputData aid : requestDataObject.getSeries()) {
				periodEnd = Instant.parse(aid.getTimestamp()).toEpochMilli();
			}
			AnomalyOutputData aod = new AnomalyOutputData();
			// TODO compute average from input
			aod.setActualDataLong(responseData.getExpectedValue());
			aod.setIsAnomaly(responseData.getIsAnomaly());
			aod.setIsNegativeAnomaly(responseData.getIsNegativeAnomaly());
			aod.setIsPositiveAnomaly(responseData.getIsPositiveAnomaly());
			aod.setExpectedValue(responseData.getExpectedValue());
			aod.setLowerMargin(responseData.getLowerMargin());
			aod.setUpperMargin(responseData.getUpperMargin());
			aod.setPeriod(responseData.getPeriod());
			aod.setSuggestedWindow(responseData.getSuggestedWindow());
			aod.setTimeInMillis(periodEnd);
	        String sendEventData = obj.writeValueAsString(aod);
			if (sendEventData != null) {
	            EventDataBatch batch = producer.createBatch();
	            batch.tryAdd(new EventData(sendEventData));
	            // send the batch of events to the event hub
	            producer.send(batch);
	    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), sendEventData);
	        } else {
	    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), "Error: Azure Anomaly Detector returned null");
	        }	

			// Reset the training data list for next round
			requestDataObject = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	
//	public void sendOld() {
//        String vmMetricsDataString = metricsCollector.getMetrics();
//        if (vmMetricsDataString == null) {
//        	System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), "Failed to collect metrics");
//        	return;
//        }
//
//        ObjectMapper obj = new ObjectMapper();
//		VMMetricsData vmx;
//		try {
//			vmx = obj.readValue(vmMetricsDataString, VMMetricsData.class);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//		// Running anomaly detection on the VM's available storage space
//		long valueToDetect = vmx.getAvailableStorageSpace();
//		AnomalyInputData inputData = new AnomalyInputData(Instant.ofEpochMilli(vmx.getTimeInMillis()).truncatedTo(ChronoUnit.MINUTES).toString(), 
//				valueToDetect);
//		AzureAnomalyDetectorRequestData requestDataObject = new AzureAnomalyDetectorRequestData(MINUTELY, inputData);
//        String sendEventData;
//		try {
//			String requestData = obj.writeValueAsString(requestDataObject);
//			System.out.printf("[%s] Sending data to Anomaly Detector: %s\n", ZonedDateTime.now(), requestData);
//			String anomalyResponseString = eventInsight.getAnomaly(requestData);
//			AnomalyOutputData anomalyRespObj = obj.readValue(anomalyResponseString, AnomalyOutputData.class);
//			anomalyRespObj.setActualDataLong(valueToDetect);
//			anomalyRespObj.setTimeInMillis(vmx.getTimeInMillis());
//			sendEventData = obj.writeValueAsString(anomalyRespObj);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return;
//		}
//        if (sendEventData != null) {
//            EventDataBatch batch = producer.createBatch();
//            batch.tryAdd(new EventData(sendEventData));
//            // send the batch of events to the event hub
//            producer.send(batch);
//    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), sendEventData);
//        } else {
//    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), "Error: Azure Anomaly Detector returned null");
//        }
//	}
	
	public void close() {
		if (producer != null) {
			producer.close();
		}
	}
}
