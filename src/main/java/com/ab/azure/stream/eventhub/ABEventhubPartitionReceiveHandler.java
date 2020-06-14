package com.ab.azure.stream.eventhub;

import java.nio.charset.Charset;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.PartitionReceiveHandler;

public class ABEventhubPartitionReceiveHandler extends PartitionReceiveHandler {
	
	String partitionId;
	String consumerGroupName;

	protected ABEventhubPartitionReceiveHandler (int maxEventCount, String partitionId, String consumerGroupName) {
		super(maxEventCount);
		this.partitionId = partitionId;
		this.consumerGroupName = consumerGroupName;
	}

	@Override
	public void onReceive(Iterable<EventData> events) {
		if (events == null) {
			return;
		}
		int batchSize = 0;
		StringBuilder buf = new StringBuilder(
				String.format("------ Partition %s, ConsumerGroup %s\n", partitionId, consumerGroupName));
		for (EventData event : events) {
			buf.append(String.format("\t Offset: %s, SeqNo: %s, EnqueueTime: %s, BatchSize: %d\n",
					event.getSystemProperties().getOffset(),
					event.getSystemProperties().getSequenceNumber(),
					event.getSystemProperties().getEnqueuedTime(), batchSize));
			buf.append(String.format("\tPayload: %s\n",
					new String(event.getBody(), Charset.defaultCharset())));
			batchSize++;			
		}
		// If received any data, print it
		if (batchSize > 0) {
			System.out.println(buf.toString());
		}
	}

	@Override
	public void onError(Throwable error) {
		System.out.println(String.format("------ Partition %s, ConsumerGroup %s\n\tError Receiving data %s",
				partitionId, consumerGroupName, error.toString()));
	}

}
