package bftsmart.monitor.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bftsmart.monitor.grpc.MetricsServiceGrpc;
import bftsmart.monitor.grpc.MetricsRequest;
import bftsmart.monitor.grpc.MetricsResponse;
import bftsmart.monitor.grpc.Timestamp;
import bftsmart.reconfiguration.util.Configuration;
import bftsmart.monitor.MetricsCollector;

public class MetricsServiceImpl extends MetricsServiceGrpc.MetricsServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(MetricsServiceImpl.class);
    private final MetricsCollector metricsCollector;
    private final Configuration configuration;

    public MetricsServiceImpl(Configuration configuration) {
        this.metricsCollector = MetricsCollector.getInstance();
        this.configuration = configuration;
        logger.info("MetricsServiceImpl initialized");
    }

    @Override
    public void sendMetrics(MetricsRequest request, StreamObserver<MetricsResponse> responseObserver) {
        try {
            logger.debug("Received metrics request");
            MetricsRequest metrics = metricsCollector.getCurrentMetrics();
            logger.info("Sending metrics - Throughput: {}, Latency: {}, Requests: {}, RequestSize: {}, BatchSize: {}, BatchTimeout: {}, Leader: {}",
                metrics.getThroughputList(),
                metrics.getLatencyList(),
                metrics.getRequestsList(),
                metrics.getRequestsSizeList(),
                metrics.getBatchSizeList(),
                metrics.getBatchTimeoutList(),
                metrics.getLeaderList());
            
            // Get current configuration values
            int currentBatchSize = Integer.parseInt(
                configuration.getProperty("system.totalordermulticast.maxbatchsize"));
            int currentBatchTimeout = Integer.parseInt(
                configuration.getProperty("system.totalordermulticast.timeout"));

            // Build and send response
            MetricsResponse response = MetricsResponse.newBuilder()
                .setBatchSize(currentBatchSize)
                .setBatchTimeout(currentBatchTimeout)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.debug("Sent metrics response with batchSize={}, batchTimeout={}", 
                currentBatchSize, currentBatchTimeout);
        } catch (Exception e) {
            logger.error("Error processing metrics request", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void connect(Timestamp request, StreamObserver<Timestamp> responseObserver) {
        try {
            logger.debug("Received connect request with timestamp: {}", request.getTimestamp());
            
            // Echo back the timestamp to confirm connection
            Timestamp response = Timestamp.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.debug("Sent connect response with timestamp: {}", response.getTimestamp());
        } catch (Exception e) {
            logger.error("Error processing connect request", e);
            responseObserver.onError(e);
        }
    }

    /**
     * Updates system configuration parameters
     * @param batchSize new batch size value
     * @param batchTimeout new batch timeout value
     * @return true if update was successful
     */
    public boolean updateConfiguration(int batchSize, int batchTimeout) {
        try {
            // In a real implementation, you would update the system configuration here
            // This is a placeholder for the actual configuration update logic
            logger.info("Updating configuration - batchSize: {}, batchTimeout: {}", 
                batchSize, batchTimeout);
            
            // Update metrics collector
            metricsCollector.updateBatchSize(batchSize);
            metricsCollector.updateBatchTimeout(batchTimeout);
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to update configuration", e);
            return false;
        }
    }
}