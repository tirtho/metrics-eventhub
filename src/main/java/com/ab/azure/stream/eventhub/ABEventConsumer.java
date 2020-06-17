package com.ab.azure.stream.eventhub;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

import com.ab.azure.stream.metrics.vm.VMMetricsData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ABEventConsumer {

	private static final String METRICS_CSV_FILE = "METRICS_CSV_FILE_NAME";
	private String connectionString;
	private String eventHubName;
	private PrintWriter writer;
	private FileWriter metricsCSVFileWriter;
	
	public ABEventConsumer(String connectionString, String eventHubName) {
		this.connectionString = connectionString;
		this.eventHubName = eventHubName;
		String metricsCSVFileName = System.getenv(METRICS_CSV_FILE);
		if (metricsCSVFileName != null) {
			try {
				metricsCSVFileWriter = new FileWriter(metricsCSVFileName);
				writer = new PrintWriter(metricsCSVFileWriter);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				writer = null;
			}
		}
	}
	
	public void receive() throws IOException {
        // function to process events
        Consumer<EventContext> processEvent = eventContext  -> {
            System.out.printf("[%s] Received event: ", ZonedDateTime.now());
            // print the body of the event
            String jsonEventData = eventContext.getEventData().getBodyAsString();
            System.out.println(jsonEventData);
            ObjectMapper objMap = new ObjectMapper();
            try {
				VMMetricsData vd = objMap.readValue(jsonEventData, VMMetricsData.class);
				if (writer != null) {
					writer.printf("%d,%.1f,%d,%d,%d,%d\n", vd.getTimeInMillis(), vd.getTemperature(), 
							vd.getProcessorLoadUser(), vd.getProcessCount(), vd.getAvailableMemory(), 
							vd.getAvailableStorageSpace());
					writer.flush();
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            eventContext.updateCheckpoint();
        };

        // function to process errors
        Consumer<ErrorContext> processError = errorContext -> {
            // print the error message
            System.out.println(errorContext.getThrowable().getMessage());
        };


        EventProcessorClient eventProcessorClient = new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .processEvent(processEvent)
                .processError(processError)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .checkpointStore(new ABInMemoryCheckpointStore())
                .buildEventProcessorClient();

        System.out.println("Starting event processor");
        eventProcessorClient.start();

        System.out.println("Press enter to stop.");
        System.in.read();

        System.out.println("Stopping event processor");
        if (writer != null) {
        	writer.close();
        	metricsCSVFileWriter.close();
        }
        eventProcessorClient.stop();
        System.out.println("Event processor stopped.");

        System.out.println("Exiting process");	}

}
