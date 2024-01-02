import java.util.*;

class CPUQueue {
    Queue<CPUProcess> queue = new LinkedList<>();
    private int maxSize;

    public CPUQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public void enqueue(CPUProcess process) {
        if (queue.size() < maxSize) {
            queue.add(process);
        } else {
            System.out.println("Queue is full. Unable to enqueue process.");
        }
    }

    public CPUProcess dequeue() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class CPUProcess {
    private int generationInterval;
    private int serviceTime;

    public CPUProcess(int generationInterval, int serviceTime) {
        this.generationInterval = generationInterval;
        this.serviceTime = serviceTime;
    }

    public int getGenerationInterval() {
        return generationInterval;
    }

    public int getServiceTime() {
        return serviceTime;
    }
}

class CPU {
    private int processCount;
    private int maxQueueLength;
    private Map<Integer, Integer> processorsUsage;

    public CPU(int processCount, int fixedQueueSize, int lowerBound, int upperBound) {
        this.processCount = 0;
        this.maxQueueLength = 0;
        this.processorsUsage = new HashMap<>();

        CPUQueue fixedQueue = new CPUQueue(fixedQueueSize);
        CPUQueue unlimitedQueue = new CPUQueue(Integer.MAX_VALUE);

        Random random = new Random();
        int currentTime = 0;
        int n = 0;  // Кількість запитів з fixedQueue перед опрацюванням unlimitedQueue

        while (true) {
            // Генерація процесів для fixedQueue
            if (random.nextDouble() < 0.5) {
                int generationInterval = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
                fixedQueue.enqueue(new CPUProcess(generationInterval, random.nextInt(upperBound - lowerBound + 1) + lowerBound));
            }

            // Генерація процесів для unlimitedQueue
            if (random.nextDouble() < 0.5) {
                int generationInterval = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
                unlimitedQueue.enqueue(new CPUProcess(generationInterval, random.nextInt(upperBound - lowerBound + 1) + lowerBound));
            }

            // Опрацювання процесів з fixedQueue
            if (!fixedQueue.isEmpty()) {
                CPUProcess currentProcess = fixedQueue.dequeue();
                processorsUsage.put(1, processorsUsage.getOrDefault(1, 0) + 1);
                currentTime += currentProcess.getServiceTime();
                n++;
            }

            // Опрацювання процесів з unlimitedQueue кожні n запитів з fixedQueue
            if (n == processCount && !unlimitedQueue.isEmpty()) {
                CPUProcess currentProcess = unlimitedQueue.dequeue();
                processorsUsage.put(2, processorsUsage.getOrDefault(2, 0) + 1);
                currentTime += currentProcess.getServiceTime();
                n = 0;  // Скидання лічильника
            }

            maxQueueLength = Math.max(maxQueueLength, unlimitedQueue.isEmpty() ? 0 : unlimitedQueue.queue.size());

            if (processorsUsage.size() == 2) {
                break;
            }
        }
        this.processCount = currentTime;
    }

    public int getProcessCount() {
        return processCount;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public Map<Integer, Integer> getProcessorsUsage() {
        return processorsUsage;
    }
}

public class kp2_2 {
    public static void main(String[] args) {
        int processCount = 3;
        int fixedQueueSize = 5;  // Розмір черги для першого потоку
        int lowerBound = 1;
        int upperBound = 10;

        CPU cpu = new CPU(processCount, fixedQueueSize, lowerBound, upperBound);

        System.out.println("Number of processors: " + cpu.getProcessCount());
        System.out.println("Maximum queue length: " + cpu.getMaxQueueLength());
        System.out.println("Processor usage:");

        for (Map.Entry<Integer, Integer> processor : cpu.getProcessorsUsage().entrySet()) {
            double percentage = (double) processor.getValue() / cpu.getProcessCount() * 100;
            System.out.printf("Processor %d: %d processes (%.2f%%)%n", processor.getKey(), processor.getValue(), percentage);
        }
    }
}
