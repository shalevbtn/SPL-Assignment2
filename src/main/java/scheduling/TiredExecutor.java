package scheduling;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        Random rand = new Random();
        workers = new TiredThread[numThreads];

        for(int id = 0; id < numThreads; id++) {
            double ff = rand.nextDouble(0.5,1.5);
            workers[id] = new TiredThread(id, ff);
            idleMinHeap.add(workers[id]);
        }
    }

    public void submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        while(idleMinHeap.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TiredThread currT = idleMinHeap.remove();
        currT.newTask(task);
        currT.start();
        this.inFlight.incrementAndGet();
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for(Runnable task : tasks) {
            submit(task);
        }
    }

    public void shutdown() throws InterruptedException {
        for(TiredThread t : workers) {
            t.shutdown();
        }
    }

    public synchronized String getWorkerReport() {
        StringBuilder report = new StringBuilder();

        for (TiredThread worker : workers) {
            report.append(String.format(
                "Worker %d | Busy: %s | Time Used: %d ns | Time Idle: %d ns | Fatigue: %.2f%n",
                worker.getWorkerId(),
                worker.isBusy(),
                worker.getTimeUsed(),
                worker.getTimeIdle(),
                worker.getFatigue()
            ));
        }

        return report.toString();
    }

}
