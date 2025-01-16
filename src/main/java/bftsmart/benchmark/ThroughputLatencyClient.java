package bftsmart.benchmark;

import bftsmart.tests.util.Operation;
import bftsmart.tom.ServiceProxy;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 吞吐量延迟测试客户端 - 支持可变请求大小
 * 可以创建多个客户端实例并发送不同大小的读写请求来测试系统性能
 */

/*

发送大小在1KB到4KB之间，步长为1KB的递增请求
./smartrun.sh bftsmart.benchmark.ThroughputLatencyClient 1001 1 1000 1024 4096 1024 true false true false

发送大小在1KB到4KB之间随机的请求
./smartrun.sh bftsmart.benchmark.ThroughputLatencyClient 1001 1 1000 1024 4096 1024 true false true true

 */
public class ThroughputLatencyClient {
    // 客户端配置参数
    private static int initialClientId;
    private static int minRequestSize;
    private static int maxRequestSize;
    private static int sizeStep;
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 10) {
            System.out.println("USAGE: bftsmart.benchmark.ThroughputLatencyClient " +
                    "<initial client id> " +     // 起始客户端ID
                    "<num clients> " +           // 要创建的客户端数量
                    "<operations per client> " + // 每个客户端发送的操作数
                    "<min request size> " +      // 最小请求大小(字节)
                    "<max request size> " +      // 最大请求大小(字节)
                    "<size step> " +             // 请求大小增长步长
                    "<isWrite?> " +              // 是否是写操作
                    "<use hashed response> " +   // 是否使用哈希响应
                    "<measurement leader?> " +   // 是否是测量领导者
                    "<random size?>");           // 是否使用随机大小
            System.exit(-1);
        }

        // 解析命令行参数
        initialClientId = Integer.parseInt(args[0]);
        int numClients = Integer.parseInt(args[1]);
        int numOperationsPerClient = Integer.parseInt(args[2]);
        minRequestSize = Integer.parseInt(args[3]);
        maxRequestSize = Integer.parseInt(args[4]);
        sizeStep = Integer.parseInt(args[5]);
        boolean isWrite = Boolean.parseBoolean(args[6]);
        boolean useHashedResponse = Boolean.parseBoolean(args[7]);
        boolean measurementLeader = Boolean.parseBoolean(args[8]);
        boolean randomSize = Boolean.parseBoolean(args[9]);
        
        // 用于同步所有客户端启动的闩锁
        CountDownLatch latch = new CountDownLatch(numClients);
        Client[] clients = new Client[numClients];

        // 创建并启动所有客户端线程
        for (int i = 0; i < numClients; i++) {
            clients[i] = new Client(initialClientId + i,
                    numOperationsPerClient, isWrite, useHashedResponse, 
                    measurementLeader, latch, randomSize);
            clients[i].start();
            Thread.sleep(10); // 每个客户端间隔10ms启动，避免瞬时压力过大
        }

        // 等待所有客户端就绪
        latch.await();
        System.out.println("Executing experiment");
    }

    /**
     * 客户端线程类
     * 每个实例代表一个独立的客户端，可以发送不同大小的读写请求
     */
    private static class Client extends Thread {
        private final int clientId;
        private final int numOperations;
        private final boolean isWrite;
        private final ServiceProxy proxy;
        private final CountDownLatch latch;
        private final boolean useHashedResponse;
        private final boolean measurementLeader;
        private final boolean randomSize;
        private final Random random;
        private int currentSize;  // 当前请求大小

        public Client(int clientId, int numOperations, boolean isWrite, 
                     boolean useHashedResponse, boolean measurementLeader, 
                     CountDownLatch latch, boolean randomSize) {
            this.clientId = clientId;
            this.numOperations = numOperations;
            this.isWrite = isWrite;
            this.useHashedResponse = useHashedResponse;
            this.measurementLeader = measurementLeader;
            this.proxy = new ServiceProxy(clientId);
            this.latch = latch;
            this.randomSize = randomSize;
            this.random = new Random();
            this.currentSize = minRequestSize;
        }

        /**
         * 创建指定大小的请求
         * @param isWrite 是否为写请求
         * @return 序列化后的请求数据
         */
        private byte[] createRequest(boolean isWrite) {
            // 确定本次请求的大小
            int requestSize;
            if (randomSize) {
                // 在最小值和最大值之间随机选择
                requestSize = minRequestSize + random.nextInt(maxRequestSize - minRequestSize + 1);
            } else {
                // 使用当前大小并准备下一个大小
                requestSize = currentSize;
                currentSize += sizeStep;
                if (currentSize > maxRequestSize) {
                    currentSize = minRequestSize;  // 重置为最小值
                }
            }
            
            // 生成测试数据
            byte[] data = new byte[requestSize];
            for (int i = 0; i < requestSize; i++) {
                data[i] = (byte) i;
            }
            
            if (isWrite) {
                // 写请求格式: [操作类型(1字节)][数据大小(4字节)][数据(requestSize字节)]
                ByteBuffer writeBuffer = ByteBuffer.allocate(1 + Integer.BYTES + requestSize);
                writeBuffer.put((byte) Operation.PUT.ordinal());
                writeBuffer.putInt(requestSize);
                writeBuffer.put(data);
                return writeBuffer.array();
            } else {
                // 读请求格式: [操作类型(1字节)]
                ByteBuffer readBuffer = ByteBuffer.allocate(1);
                readBuffer.put((byte) Operation.GET.ordinal());
                return readBuffer.array();
            }
        }
        
        /**
         * 验证读请求的响应
         * @param request 原始请求
         * @param response 服务器响应
         * @return 验证是否通过
         */
        private boolean verifyResponse(byte[] request, byte[] response) {
            if (isWrite) {
                return true;  // 写请求不需要验证响应
            }
            
            // 从请求中提取期望的响应大小
            ByteBuffer buffer = ByteBuffer.wrap(request);
            buffer.get(); // 跳过操作类型
            int expectedSize = buffer.getInt();
            
            // 验证响应大小和内容
            if (response.length != expectedSize) {
                return false;
            }
            
            // 验证响应数据内容
            for (int i = 0; i < expectedSize; i++) {
                if (response[i] != (byte) i) {
                    return false;
                }
            }
            
            return true;
        }

        @Override
        public void run() {
            try {
                latch.countDown();
                
                for (int i = 0; i < numOperations; i++) {
                    // 创建新的请求
                    byte[] request = createRequest(isWrite);
                    byte[] response;
                    
                    // 记录开始时间
                    long t1 = System.nanoTime();
                    
                    // 发送请求并获取响应
                    if (isWrite) {
                        if (useHashedResponse) {
                            response = proxy.invokeOrderedHashed(request);
                        } else {
                            response = proxy.invokeOrdered(request);
                        }
                    } else {
                        if (useHashedResponse) {
                            response = proxy.invokeUnorderedHashed(request);
                        } else {
                            response = proxy.invokeUnordered(request);
                        }
                    }
                    
                    // 记录结束时间并计算延迟
                    long t2 = System.nanoTime();
                    long latency = t2 - t1;

                    // 如果是读请求，验证响应
                    if (!isWrite && !useHashedResponse) {
                        if (!verifyResponse(request, response)) {
                            throw new IllegalStateException("Invalid response received");
                        }
                    }
                    
                    // 如果是测量领导者，记录延迟和请求大小
                    if (initialClientId == clientId && measurementLeader) {
                        System.out.printf("M: %d S: %d%n", latency, request.length);
                    }
                }
            } finally {
                proxy.close();
            }
        }
    }
}