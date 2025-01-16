package bftsmart.monitor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bftsmart.reconfiguration.util.Configuration;
import bftsmart.monitor.service.MetricsServiceImpl;
import bftsmart.monitor.grpc.MetricsServiceGrpc;
import bftsmart.monitor.grpc.Timestamp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MetricsServer {
    private static final Logger logger = LoggerFactory.getLogger(MetricsServer.class);

    private final Server server;
    private final int port;
    private final MetricsServiceImpl metricsService;
    private ManagedChannel pythonChannel;
    private MetricsServiceGrpc.MetricsServiceBlockingStub pythonStub;

    public MetricsServer(int port, Configuration configuration) {
        this.port = port;
        this.metricsService = new MetricsServiceImpl(configuration);
        this.server = ServerBuilder.forPort(port)
                .addService(metricsService)
                .build();
    }

    /**
     * Connects to the Python trainer server
     */
    private void connectToPythonServer() {
        try {
            // 创建到Python服务器的连接
            pythonChannel = ManagedChannelBuilder.forAddress("localhost", 32767)
                    .usePlaintext()
                    .build();
            
            pythonStub = MetricsServiceGrpc.newBlockingStub(pythonChannel);
            
            // 发送Connect请求
            Timestamp request = Timestamp.newBuilder()
                    .setTimestamp(System.currentTimeMillis())
                    .build();
                    
            logger.info("Attempting to connect to Python trainer server...");
            Timestamp response = pythonStub.connect(request);
            logger.info("Successfully connected to Python trainer server. Response timestamp: {}", 
                       response.getTimestamp());
            
        } catch (Exception e) {
            logger.error("Failed to connect to Python trainer server", e);
            throw e;  // 重新抛出异常，因为这是一个关键的初始化步骤
        }
    }

    /**
     * Start serving requests.
     */
    public void start() throws IOException {
        // 首先启动gRPC服务器
        server.start();
        logger.info("Metrics Server started, listening on port {}", port);

        // 然后连接到Python trainer
        connectToPythonServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Shutting down Metrics Server due to JVM shutdown");
                try {
                    MetricsServer.this.stop();
                } catch (InterruptedException e) {
                    logger.error("Error during server shutdown", e);
                }
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            logger.info("Metrics Server shut down successfully");
        }
        
        if (pythonChannel != null) {
            pythonChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            logger.info("Python channel shut down successfully");
        }
    }

    /**
     * Await termination on the main thread.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Get the service implementation for configuration updates
     */
    public MetricsServiceImpl getMetricsService() {
        return metricsService;
    }

    /**
     * Get the Python server stub for sending metrics
     */
    public MetricsServiceGrpc.MetricsServiceBlockingStub getPythonStub() {
        return pythonStub;
    }

    /**
     * Main method to start the server directly
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: MetricsServer <port> <process-id>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        int processId = Integer.parseInt(args[1]);
        
        Configuration config = new Configuration(processId, null);
        MetricsServer server = new MetricsServer(port, config);
        server.start();
        server.blockUntilShutdown();
    }
}