package com.ab.azure.stream.eventhub;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.ab.azure.stream.metrics.vm.VMMetrics;

public class ABMetrics2Eventhub {
	private static final String EVENTHUB_NAMESPACE_CONNECTION_STRING = "eventhub.namespace.connection.string";
	private static final String EVENTHUB_NAME = "eventhub.name";
	private static final String SEND_INTERVAL_MILLIS = "SEND_INTERVAL_MILLIS";

	private static final Integer DEFAULT_SEND_INTERVAL = 60000; // 1 minute
	private static Configuration config;

	public static void main(String[] args)
			throws ConfigurationException {

		if (args == null || args.length < 2) {
			System.out.println(
					"Usage: ABMetrics2Eventhub [properties file path] [send/receive]");
			return;
		}

		config = new PropertiesConfiguration(args[0]);
		
        final String connectionString = config.getString(EVENTHUB_NAMESPACE_CONNECTION_STRING);
        final String eventHubName = config.getString(EVENTHUB_NAME);
        final int sendIntervalMillis = System.getenv(SEND_INTERVAL_MILLIS) != null ? 
        		Integer.valueOf(System.getenv(SEND_INTERVAL_MILLIS)).intValue() : 
        			config.getInteger(SEND_INTERVAL_MILLIS, DEFAULT_SEND_INTERVAL);
        
		if (StringUtils.equalsIgnoreCase(args[1], "send")) {
			// Sender
	        // create a producer using the namespace connection string and event hub name
			// Create the VMMetrics instance to collect metrics
			VMMetrics vmMetrics = new VMMetrics();
			ABEventProducer sender = new ABEventProducer(connectionString, eventHubName, sendIntervalMillis, vmMetrics);
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
		} else {
			System.out.printf("Unknown action '%s' passed in command. Aborting program...", args[1]);
			return;
		}
		return;
	}
}