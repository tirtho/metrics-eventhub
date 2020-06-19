package com.ab.azure.stream.eventhub;

import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ab.azure.stream.metrics.Metrics;
import com.ab.azure.stream.metrics.vm.VMMetrics;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;

public class ABEventProducer {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private EventHubProducerClient producer;
	int INITIAL_DELAY = 10;
	Metrics metricsCollector;
	public ABEventProducer(String connectionString, String eventHubName, int sendIntervalMillis)  {
		this.metricsCollector = new VMMetrics();
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
		
        EventDataBatch batch = producer.createBatch();
//        StringBuilder str = new StringBuilder();
//        str.append(System.currentTimeMillis()).append(",").append(metricsCollector.getMetrics());
//        String data = str.toString();
        String data = metricsCollector.getMetrics();
        if (data != null) {
            batch.tryAdd(new EventData(data));
            //batch.tryAdd(new EventData("First event"));
            // send the batch of events to the event hub
            producer.send(batch);
//    		System.out.printf("[%s] Sent event: timeInMillis,temperature,processorLoadUser,availableMemory,processCount,availableStorageSpace\n%s\n", ZonedDateTime.now(), data);
    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), data);
        } else {
    		System.out.printf("[%s] Sent event: %s\n", ZonedDateTime.now(), "Failed to collect metrics");
        }
	}
	
	public void close() {
		if (producer != null) {
			producer.close();
		}
	}
}
