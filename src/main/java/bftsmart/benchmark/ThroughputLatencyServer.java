package bftsmart.benchmark;

import bftsmart.tests.util.Operation;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.monitor.grpc.*;
import bftsmart.reconfiguration.util.TOMConfiguration;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Server implementation with async gRPC metrics reporting
 */
public class ThroughputLatencyServer extends DefaultSingleRecoverable {
    
    private static final int METRICS_INTERVAL = 1;
    
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private byte[] state;
    private long startTime;
    private long numRequests;
    private final Set<Integer> senders;
    private double maxThroughput;
    private final int processId;
    private ServiceReplica replica;
    private TOMConfiguration config;
    private long totalRequestSize;
    private long totalLatency;

    // gRPC related fields
    private final ManagedChannel channel;
    private final MetricsServiceGrpc.MetricsServiceBlockingStub blockingStub;
    private final MetricsServiceGrpc.MetricsServiceStub asyncStub;
    private final ScheduledExecutorService executorService;
    private volatile boolean isConnected = false;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("USAGE: bftsmart.benchmark.ThroughputLatencyServer <process id> <state size>");
            System.exit(-1);
        }
        int processId = Integer.parseInt(args[0]);
        int stateSize = Integer.parseInt(args[1]);
        new ThroughputLatencyServer(processId, stateSize);
    }

    public ThroughputLatencyServer(int processId, int stateSize) {
        this.processId = processId;
        senders = new HashSet<>(1000);
        state = new byte[stateSize];
        for (int i = 0; i < stateSize; i++) {
            state[i] = (byte) i;
        }

        // Initialize gRPC channel and stubs
        channel = ManagedChannelBuilder.forAddress("localhost", 32767)
                .usePlaintext()
                .build();
        blockingStub = MetricsServiceGrpc.newBlockingStub(channel);
        asyncStub = MetricsServiceGrpc.newStub(channel);
        executorService = Executors.newSingleThreadScheduledExecutor();

        this.replica = new ServiceReplica(processId, this, this);
        this.config = replica.getReplicaContext().getStaticConfiguration();

        connectToTrainer();
        startTime = System.nanoTime();

        // Add shutdown hook for cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Error during shutdown", e);
            }
        }));
    }

    private void connectToTrainer() {
        try {
            Timestamp request = Timestamp.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .build();
            
            Timestamp response = blockingStub.connect(request);
            isConnected = true;
            logger.info("Successfully connected to trainer. Response timestamp: {}", 
                       response.getTimestamp());
        } catch (Exception e) {
            logger.error("Failed to connect to trainer", e);
            executorService.schedule(this::connectToTrainer, 5, TimeUnit.SECONDS);
        }
    }

    private void sendMetricsAsync(double throughput, long averageLatency, long numRequests, 
                                long totalRequestSize, MessageContext msgCtx) {
        if (!isConnected) {
            return;
        }

        MetricsRequest request = MetricsRequest.newBuilder()
                .addThroughput((int)throughput)
                .addLatency((int)(averageLatency))
                .addRequests((int)numRequests) 
                .addRequestsSize((int)totalRequestSize)
                .addBatchSize(config.getMaxBatchSize())
                .addBatchTimeout(config.getBatchTimeout())
                .addLeader(msgCtx != null ? msgCtx.getLeader() : -1)
                .build();

        StreamObserver<MetricsResponse> responseObserver = new StreamObserver<MetricsResponse>() {
            @Override
            public void onNext(MetricsResponse response) {
                executorService.execute(() -> {
                    updateConfigurations(response.getBatchSize(), response.getBatchTimeout());
                });
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Failed to send metrics to trainer", t);
                isConnected = false;
                executorService.schedule(() -> connectToTrainer(), 
                    5, TimeUnit.SECONDS);
            }

            @Override
            public void onCompleted() {
                // Optional: Add any cleanup code here
            }
        };

        try {
            asyncStub.sendMetrics(request, responseObserver);
        } catch (Exception e) {
            logger.error("Failed to send async metrics request", e);
            isConnected = false;
            executorService.schedule(() -> connectToTrainer(),
                5, TimeUnit.SECONDS);
        }
    }

    private void updateConfigurations(int newBatchSize, int newBatchTimeout) {
        if (newBatchSize > 0) {
            config.setMaxBatchSize(newBatchSize);
            logger.info("Updated batch size to: {}", newBatchSize);
        }
        
        if (newBatchTimeout > 0) {
            config.setBatchTimeout(newBatchTimeout);
            logger.info("Updated batch timeout to: {}", newBatchTimeout);
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        numRequests++;
        senders.add(msgCtx.getSender());
        
        ByteBuffer buffer = ByteBuffer.wrap(command);
        Operation op = Operation.getOperation(buffer.get());
        if (op == Operation.PUT) {
            int requestSize = buffer.getInt();
            totalRequestSize += requestSize;
        }

        long requestLatency = System.nanoTime() - msgCtx.receptionTime;
        totalLatency += requestLatency;
        
        byte[] response = null;
        switch (op) {
            case PUT:
                response = new byte[0];
                break;
            case GET:
                response = state;
                break;
        }
        printMeasurement(msgCtx);
        return response;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        numRequests++;
        senders.add(msgCtx.getSender());
        
        ByteBuffer buffer = ByteBuffer.wrap(command);
        Operation op = Operation.getOperation(buffer.get());

        if (op == Operation.GET) {
            int requestSize = buffer.getInt();
            totalRequestSize += requestSize;
        }

        long requestLatency = System.nanoTime() - msgCtx.receptionTime;
        totalLatency += requestLatency;
        
        byte[] response = null;
        if (op == Operation.GET) {
            response = state;
        }
        
        printMeasurement(msgCtx);
        return response;
    }

    private void printMeasurement(MessageContext msgCtx) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - startTime) / 1_000_000_000.0;
        if ((int) (deltaTime / METRICS_INTERVAL) > 0) {
            long delta = currentTime - startTime;
            // double throughput = numRequests/deltaTime;
            double throughput = numRequests;

            if (throughput > maxThroughput) {
                maxThroughput = throughput;
            }

            long averageLatency = numRequests > 0 ? totalLatency / numRequests : 0;
            
            sendMetricsAsync(throughput, averageLatency, numRequests, totalRequestSize, msgCtx);

            logger.info("M:(clients[#]|requests[#]|delta[ns]|throughput[ops/s], max[ops/s])>({}|{}|{}|{}|{})",
                    senders.size(), numRequests, delta, throughput, maxThroughput);
            
            numRequests = 0;
            totalLatency = 0;
            totalRequestSize = 0;
            startTime = currentTime;
            senders.clear();
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        this.state = state;
    }

    @Override
    public byte[] getSnapshot() {
        return state == null ? new byte[0] : state;
    }
}