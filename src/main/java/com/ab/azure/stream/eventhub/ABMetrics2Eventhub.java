package com.ab.azure.stream.eventhub;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

public class ABMetrics2Eventhub {
	private static final String SEND_INTERVAL_MILLIS = "SEND_INTERVAL_MILLIS";

	private static final Integer DEFAULT_SEND_INTERVAL = 60000; // 1 minute
	private static final String ANOMALY_API = "ANOMALY_API";
	private static final String ANOMALY_ENDPOINT = "ANOMALY_ENDPOINT";
	private static final String ANOMALY_KEY = "ANOMALY_KEY";
	private static final String EVENTHUB_CONNECTION_STRING = "EVENTHUB_CONNECTION_STRING";
	private static final String EVENTHUB_NAME = "EVENTHUB_NAME";
	private static Configuration config;

	public static void main(String[] args)
			throws ConfigurationException {

		if (args == null || args.length < 2) {
			System.out.println(
					"Usage: ABMetrics2Eventhub [properties file path] [send/receive/insight]");
			return;
		}

		config = new PropertiesConfiguration(args[0]);
		
        final String connectionString = System.getenv(EVENTHUB_CONNECTION_STRING) != null ?
        		System.getenv(EVENTHUB_CONNECTION_STRING) : config.getString(EVENTHUB_CONNECTION_STRING);
        final String eventHubName = System.getenv(EVENTHUB_NAME) != null ?
        		System.getenv(EVENTHUB_NAME) : config.getString(EVENTHUB_NAME);
        final int sendIntervalMillis = System.getenv(SEND_INTERVAL_MILLIS) != null ? 
        		Integer.valueOf(System.getenv(SEND_INTERVAL_MILLIS)).intValue() : 
        			config.getInteger(SEND_INTERVAL_MILLIS, DEFAULT_SEND_INTERVAL);
        
		if (StringUtils.equalsIgnoreCase(args[1], "send")) {
			// Sender
	        // create a producer using the namespace connection string and event hub name
			ABEventProducer sender = new ABEventProducer(connectionString, eventHubName, sendIntervalMillis);
			return;
		} else if (StringUtils.equalsIgnoreCase(args[1], "receive")) {
			// Receiver
			ABEventConsumer receiver = new ABEventConsumer(connectionString, eventHubName);
			try {
				receiver.receive();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (StringUtils.equalsIgnoreCase(args[1], "train")){
			final String anomalyDetectorApiAddress = System.getenv(ANOMALY_API);
			final String anomalyDetectorEndpoint = System.getenv(ANOMALY_ENDPOINT);
			final String anomalyDetectorKey = System.getenv(ANOMALY_KEY);
			ABEventInsightTrainer insightTrainer = new ABEventInsightTrainer(connectionString, eventHubName, sendIntervalMillis, 
				anomalyDetectorApiAddress, anomalyDetectorEndpoint, anomalyDetectorKey);
		} else if (StringUtils.equalsIgnoreCase(args[1], "insight")){
			final String anomalyDetectorApiAddress = System.getenv(ANOMALY_API);
			final String anomalyDetectorEndpoint = System.getenv(ANOMALY_ENDPOINT);
			final String anomalyDetectorKey = System.getenv(ANOMALY_KEY);
			ABEventInsightProducer insightProducer = new ABEventInsightProducer(connectionString, eventHubName, sendIntervalMillis, 
				anomalyDetectorApiAddress, anomalyDetectorEndpoint, anomalyDetectorKey);
		} else {
			System.out.printf("Unknown action '%s' passed in command. Aborting program...", args[1]);
			return;
		}
		return;
	}
}