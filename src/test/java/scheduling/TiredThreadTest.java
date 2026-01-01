package scheduling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {

    @Test
    @DisplayName("Fatigue Calculation and Timing")
    void testFatigueAndTiming() throws InterruptedException {
        // Create a thread with a known fatigue factor
        double fatigueFactor = 1.5;
        TiredThread thread = new TiredThread(1, fatigueFactor);
        thread.start();

        // Initial state
        assertEquals(0, thread.getTimeUsed());
        assertEquals(0.0, thread.getFatigue());

        // Assign a task that takes some time
        long sleepMillis = 100;
        Runnable task = () -> {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        thread.newTask(task);

        // Wait a bit for the thread to finish the task
        Thread.sleep(sleepMillis + 50);

        // Verify timing (Time used should be roughly sleepMillis converted to nanoseconds)
        assertTrue(thread.getTimeUsed() > 0, "Time used should have increased");
        
        // Verify fatigue calculation (fatigue = factor * timeUsed)
        double expectedFatigue = fatigueFactor * thread.getTimeUsed();
        assertEquals(expectedFatigue, thread.getFatigue(), 0.001);

        // Cleanup
        thread.shutdown();
        thread.join(1000);
        assertFalse(thread.isAlive(), "Thread should have shut down after poison pill");
    }

    @Test
    @DisplayName("Busy State Transitions")
    void testBusyState() throws InterruptedException {
        TiredThread thread = new TiredThread(2, 1.0);
        thread.start();

        assertFalse(thread.isBusy());

        // A task that blocks so we can check the 'busy' state
        Object lock = new Object();
        Runnable blockingTask = () -> {
            synchronized (lock) {
                try { lock.wait(); } catch (InterruptedException e) {}
            }
        };

        thread.newTask(blockingTask);
        Thread.sleep(50); // Give it time to start

        assertTrue(thread.isBusy(), "Thread should be busy while executing");

        synchronized (lock) {
            lock.notify(); // Release the task
        }

        Thread.sleep(50);
        assertFalse(thread.isBusy(), "Thread should be idle after task completion");

        thread.shutdown();
        thread.join();
    }
}