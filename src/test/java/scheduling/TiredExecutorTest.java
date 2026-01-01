package scheduling;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class TiredExecutorTest {

    @Test
    void testExecution() throws InterruptedException {
        int numThreads = 2;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        Runnable task = () -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            counter.incrementAndGet();
        };

        for(int i = 0; i < 5; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        assertEquals(5, counter.get());
    }
}