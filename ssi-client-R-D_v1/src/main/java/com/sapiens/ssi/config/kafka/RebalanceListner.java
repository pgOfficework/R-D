package com.sapiens.ssi.config.kafka;

import java.util.*;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.*;

import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.JsonImpl;

public class RebalanceListner implements ConsumerRebalanceListener {
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(RebalanceListner.class);

    private KafkaConsumer consumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap();

    public RebalanceListner(KafkaConsumer con) {
        this.consumer = con;
    }

    public void addOffset(String topic, int partition, long offset) {
        currentOffsets.put(new TopicPartition(topic, partition), new OffsetAndMetadata(offset, SSIConstant.Commit));
    }

    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffsets() {
        return currentOffsets;
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
       log.debug("Following Partitions Assigned ....");
        for (TopicPartition partition : partitions)
        	log.debug(partition.partition() + ",");
    }

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
    	log.debug("Following Partitions Revoked ....");
        for (TopicPartition partition : partitions)
        	log.debug(partition.partition() + ",");


        log.debug("Following Partitions commited ....");
        for (TopicPartition tp : currentOffsets.keySet())
        	log.debug(tp.partition());

        consumer.commitSync(currentOffsets);
        currentOffsets.clear();
    }
}
