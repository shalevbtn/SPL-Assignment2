package scheduling;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        Random rand = new Random();
        workers = new TiredThread[numThreads]; // placeholder
        for(int id = 0; id < numThreads; id++) {
            double ff = rand.nextDouble(0.5,1.5);
            workers[id] = new TiredThread(id, ff);
            idleMinHeap.add(workers[id]);
        }
    }

    public void submit(Runnable task) {
        // TODO
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
    }

    public void shutdown() throws InterruptedException {
        // TODO
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        return null;
    }
}
