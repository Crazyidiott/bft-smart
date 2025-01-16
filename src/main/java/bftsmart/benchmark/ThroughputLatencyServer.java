package bftsmart.benchmark;

import bftsmart.tests.util.Operation;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import bftsmart.monitor.grpc.*;
import bftsmart.reconfiguration.util.TOMConfiguration;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    //每次发送的是1s内所有request的size和
    private long totalRequestSize;
    
    // gRPC related fields
    private final ManagedChannel channel;
    private final MetricsServiceGrpc.MetricsServiceBlockingStub blockingStub;
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

        channel = ManagedChannelBuilder.forAddress("localhost", 32767)
                .usePlaintext()
                .build();
        blockingStub = MetricsServiceGrpc.newBlockingStub(channel);

        this.replica = new ServiceReplica(processId, this, this);
        this.config = replica.getReplicaContext().getStaticConfiguration();

        connectToTrainer();
        startTime = System.nanoTime();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Error during channel shutdown", e);
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
        
        // Method 1: Using command.length as request size, command.length = 1(op type, 1 byte) + 4(size of requests, 4 byte) + requestSize bytes
        //totalRequestSize += command.length;
        
        // Method 2: Using actual request data size
        ByteBuffer buffer = ByteBuffer.wrap(command);
        Operation op = Operation.getOperation(buffer.get());
        if (op == Operation.PUT) {
            int requestSize = buffer.getInt();
            totalRequestSize += requestSize;
        }
        
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
        
        // Method 1: Using command.length as request size
        //totalRequestSize += command.length;
        
        // Method 2: Using actual request data size
        ByteBuffer buffer = ByteBuffer.wrap(command);
        Operation op = Operation.getOperation(buffer.get());
        
        //对于 GET 操作不增加 totalRequestSize，因为它没有携带实际数据
        if (op == Operation.GET) {
            int requestSize = buffer.getInt();
            totalRequestSize += requestSize;
        }
        
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
            double throughput = numRequests / deltaTime;
            if (throughput > maxThroughput) {
                maxThroughput = throughput;
            }
            
            if (isConnected) {
                try {
                    MetricsRequest request = MetricsRequest.newBuilder()
                            .addThroughput((int)throughput)
                            .addLatency((int)(delta / numRequests))
                            .addRequests((int)numRequests)
                            .addRequestsSize((int)totalRequestSize)
                            .addBatchSize(config.getMaxBatchSize())
                            .addBatchTimeout(config.getBatchTimeout())
                            .addLeader(msgCtx != null ? msgCtx.getLeader() : -1)
                            .build();
                    
                    MetricsResponse response = blockingStub.sendMetrics(request);
                    updateConfigurations(response.getBatchSize(), response.getBatchTimeout());
                } catch (Exception e) {
                    logger.error("Failed to send metrics to trainer", e);
                    isConnected = false;
                    connectToTrainer();
                }
            }

            logger.info("M:(clients[#]|requests[#]|delta[ns]|throughput[ops/s], max[ops/s])>({}|{}|{}|{}|{})",
                    senders.size(), numRequests, delta, throughput, maxThroughput);
            
            numRequests = 0;
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