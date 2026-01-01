package scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {

    @Test
    void testFatigueAndTiming() throws InterruptedException {
        double fatigueFactor = 1.5;
        TiredThread thread = new TiredThread(1, fatigueFactor);
        thread.start();

        assertEquals(0, thread.getTimeUsed());
        assertEquals(0.0, thread.getFatigue());

        long sleepMillis = 100;
        Runnable task = () -> {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        thread.newTask(task);

        Thread.sleep(sleepMillis + 50);

        assertTrue(thread.getTimeUsed() > 0, "Time used should have increased");
        
        double expectedFatigue = fatigueFactor * thread.getTimeUsed();
        assertEquals(expectedFatigue, thread.getFatigue(), 0.001);

        thread.shutdown();
        thread.join(1000);
        assertFalse(thread.isAlive(), "Thread should have shut down after poison pill");
    }

    @Test
    void testBusyState() throws InterruptedException {
        TiredThread thread = new TiredThread(2, 1.0);
        thread.start();

        assertFalse(thread.isBusy());

        Object lock = new Object();
        Runnable blockingTask = () -> {
            synchronized (lock) {
                try { lock.wait(); } catch (InterruptedException e) {}
            }
        };

        thread.newTask(blockingTask);
        Thread.sleep(50);

        assertTrue(thread.isBusy(), "Thread should be busy while executing");

        synchronized (lock) {
            lock.notify(); 
        }

        Thread.sleep(50);
        assertFalse(thread.isBusy(), "Thread should be idle after task completion");

        thread.shutdown();
        thread.join();
    }
}