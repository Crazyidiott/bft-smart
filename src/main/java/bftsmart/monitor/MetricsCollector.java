package bftsmart.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bftsmart.monitor.grpc.MetricsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricsCollector {
    private static final Logger logger = LoggerFactory.getLogger(MetricsCollector.class);
    private static MetricsCollector instance;

    private final List<Integer> throughputList;
    private final List<Integer> latencyList;
    private final List<Integer> requestsList;
    private final List<Integer> requestsSizeList;
    private final AtomicInteger currentBatchSize;
    private final AtomicInteger currentBatchTimeout;
    private final AtomicInteger currentLeader;

    private static final int MAX_METRICS_WINDOW = 100;

    private MetricsCollector() {
        logger.info("Initializing MetricsCollector");
        this.throughputList = new ArrayList<>();
        this.latencyList = new ArrayList<>();
        this.requestsList = new ArrayList<>();
        this.requestsSizeList = new ArrayList<>();
        this.currentBatchSize = new AtomicInteger(0);
        this.currentBatchTimeout = new AtomicInteger(0);
        this.currentLeader = new AtomicInteger(0);
    }

    public static synchronized MetricsCollector getInstance() {
        if (instance == null) {
            instance = new MetricsCollector();
        }
        return instance;
    }

    public void recordThroughput(int throughput) {
        synchronized (throughputList) {
            if (throughputList.size() >= MAX_METRICS_WINDOW) {
                throughputList.remove(0);
            }
            throughputList.add(throughput);
            logger.debug("Recorded throughput: {}", throughput);
        }
    }

    public void recordLatency(int latency) {
        synchronized (latencyList) {
            if (latencyList.size() >= MAX_METRICS_WINDOW) {
                latencyList.remove(0);
            }
            latencyList.add(latency);
            logger.debug("Recorded latency: {}", latency);
        }
    }

    public void recordRequests(int requests) {
        synchronized (requestsList) {
            if (requestsList.size() >= MAX_METRICS_WINDOW) {
                requestsList.remove(0);
            }
            requestsList.add(requests);
            logger.debug("Recorded requests: {}", requests);
        }
    }

    public void recordRequestSize(int size) {
        synchronized (requestsSizeList) {
            if (requestsSizeList.size() >= MAX_METRICS_WINDOW) {
                requestsSizeList.remove(0);
            }
            requestsSizeList.add(size);
            logger.debug("Recorded request size: {}", size);
        }
    }

    public void updateBatchSize(int batchSize) {
        logger.info("Updating batch size to: {}", batchSize);
        currentBatchSize.set(batchSize);
    }

    public void updateBatchTimeout(int batchTimeout) {
        logger.info("Updating batch timeout to: {}", batchTimeout);
        currentBatchTimeout.set(batchTimeout);
    }

    public void updateLeader(int leaderId) {
        logger.info("Updating leader to: {}", leaderId);
        currentLeader.set(leaderId);
    }

    public MetricsRequest getCurrentMetrics() {
        logger.debug("Building current metrics request");
        try {
            MetricsRequest.Builder builder = MetricsRequest.newBuilder();
            
            synchronized (throughputList) {
                builder.addAllThroughput(new ArrayList<>(throughputList));
            }
            synchronized (latencyList) {
                builder.addAllLatency(new ArrayList<>(latencyList));
            }
            synchronized (requestsList) {
                builder.addAllRequests(new ArrayList<>(requestsList));
            }
            synchronized (requestsSizeList) {
                builder.addAllRequestsSize(new ArrayList<>(requestsSizeList));
            }
            
            builder.addBatchSize(currentBatchSize.get())
                   .addBatchTimeout(currentBatchTimeout.get())
                   .addLeader(currentLeader.get());

            return builder.build();
        } catch (Exception e) {
            logger.error("Error building metrics request", e);
            throw e;
        }
    }

    public void clearMetrics() {
        logger.info("Clearing all metrics");
        synchronized (throughputList) {
            throughputList.clear();
        }
        synchronized (latencyList) {
            latencyList.clear();
        }
        synchronized (requestsList) {
            requestsList.clear();
        }
        synchronized (requestsSizeList) {
            requestsSizeList.clear();
        }
    }
}