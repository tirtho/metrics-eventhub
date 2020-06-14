package com.ab.azure.stream.eventhub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.ServiceBusException;

public class ABMetrics2Eventhub {
	private static final String EVENTHUB_NAMESPACE = "eventhub.namespace";
	private static final String EVENTHUB_NAME = "eventhub.name";
	private static final String EVENTHUB_SAS_KEY_NAME = "eventhub.sas.key.name";
	private static final String EVENTHUB_SAS_KEY = "eventhub.sas.key";
	private static final String PARTITION_IDS = "eventhub.partition.ids";
	private static final String NUMBER_OF_SENDERS = "eventhub.sender.count";
	private static final String SEND_INTERVAL_MILLIS = "eventhub.send.interval.millis";

	private static final int MAX_EVENT_COUNT = 2;
	private static final Integer DEFAULT_SENDER_COUNT = 1;
	private static final Integer DEFAULT_SEND_INTERVAL = 60000; // 1 minute
	private static Configuration config;

	public static void main(String[] args)
			throws ServiceBusException, ExecutionException, InterruptedException, IOException, ConfigurationException {

		if (args == null || args.length < 2) {
			System.out.println(
					"Usage: ABMetrics2Eventhub [properties file path] [send/receive]");
			return;
		}

		config = new PropertiesConfiguration(args[0]);

		ConnectionStringBuilder connStr = new ConnectionStringBuilder(config.getString(EVENTHUB_NAMESPACE),
				config.getString(EVENTHUB_NAME), config.getString(EVENTHUB_SAS_KEY_NAME),
				config.getString(EVENTHUB_SAS_KEY));

		if (StringUtils.equalsIgnoreCase(args[1], "send")) {
			// Sender
			EventHubClient sendClient = createEventHubClient(connStr);
			sendMessages(sendClient, config.getInteger(NUMBER_OF_SENDERS, DEFAULT_SENDER_COUNT),
					config.getInteger(SEND_INTERVAL_MILLIS, DEFAULT_SEND_INTERVAL));
			return;
		} else if (StringUtils.equalsIgnoreCase(args[1], "receive")) {
			// Receiver
			EventHubClient receiveClient = createEventHubClient(connStr);
			receiveEvents(receiveClient, config.getStringArray(PARTITION_IDS));
		} else {
			System.out.printf("Unknown action '%s' passed in command. Aborting program...", args[1]);
			return;
		}
		return;
	}

	private static void sendMessages(EventHubClient client, int numberOfSenders, Integer sendIntervalInMillis)
			throws ServiceBusException, IOException, InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 0; i < numberOfSenders; i++) {
			executor.execute(new EventHubEventSender(client, sendIntervalInMillis));
		}
	}

	private static void receiveEvents(EventHubClient client, String[] partitionIds)
			throws ServiceBusException, IOException, InterruptedException, ExecutionException {
		List<PartitionReceiver> receivers = new ArrayList<>();
		// Start an Event Processor for each partition to load balance
		for (String partitionId : partitionIds) {

			CompletableFuture<PartitionReceiver> receiver = client.createReceiver(
					EventHubClient.DEFAULT_CONSUMER_GROUP_NAME, partitionId, PartitionReceiver.START_OF_STREAM, true);

			ABEventhubPartitionReceiveHandler receiveHandler = new ABEventhubPartitionReceiveHandler(MAX_EVENT_COUNT,
					partitionId, EventHubClient.DEFAULT_CONSUMER_GROUP_NAME);
			receiver.get().setReceiveHandler(receiveHandler, true);
			receivers.add(receiver.get());
		}
	}

	private static class EventHubEventSender implements Runnable {
		private EventHubClient client;
		private UUID eventSenderId;
		private Integer sendIntervalInMillis;

		public EventHubEventSender(EventHubClient sendClient, Integer sendIntervalInMillis) {
			super();
			this.client = sendClient;
			this.eventSenderId = UUID.randomUUID();
			this.sendIntervalInMillis = sendIntervalInMillis;
		}

		@Override
		public void run() {
			int count = 0;
			while (true) {
				String data = String.format("%s,%s,%d", eventSenderId.toString(), Calendar.getInstance().getTime(),
						count++);
				System.out.println(String.format("Sending -> %s", data));
				try {
					byte[] payloadBytes;
					payloadBytes = data.getBytes("UTF-8");
					EventData sendEvent = new EventData(payloadBytes);
					client.sendSync(sendEvent);
					// wait for interval
					Thread.sleep(sendIntervalInMillis);
				} catch (ServiceBusException | UnsupportedEncodingException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static EventHubClient createEventHubClient(ConnectionStringBuilder connStr)
			throws ServiceBusException, IOException {
		return EventHubClient.createFromConnectionStringSync(connStr.toString());
	}
}