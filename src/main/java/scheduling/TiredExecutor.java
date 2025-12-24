package scheduling;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        Random rand = new Random();
        workers = new TiredThread[numThreads];

        for(int id = 0; id < numThreads; id++) {
            double ff = rand.nextDouble(0.5,1.5);
            TiredThread thread = new TiredThread(id, ff);
            workers[id] = thread;
            idleMinHeap.add(thread);
            thread.start();
        }
    }

    public void submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        try{
            TiredThread thread = idleMinHeap.take();
            this.inFlight.incrementAndGet();
            Runnable wrappedTask =
                    ()->{
                        try{
                            task.run();
                        } finally {
                            this.inFlight.decrementAndGet();
                            idleMinHeap.add(thread);
                        }
                    };
            thread.newTask(wrappedTask);
        } catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
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
        for(TiredThread t : workers) {
            t.join();
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
