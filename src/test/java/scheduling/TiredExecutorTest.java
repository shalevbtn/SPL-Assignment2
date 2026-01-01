package scheduling;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Shutdown and wait
        executor.shutdown();

        // If shutdown works and join works, counter must be 5
        assertEquals(5, counter.get());
    }

    @Test
    void chooseRight(){
        TiredExecutor executor = new TiredExecutor(99);
        Runnable task = () -> {try{Thread.sleep(1);} catch(InterruptedException e){}};
        for(int i = 0; i < 999; i++) {
            executor.submit(task);
            try{Thread.sleep(1);} catch(InterruptedException e){}
        }
        Pattern pattern = Pattern.compile("Time Used: (\\d+)");
        Matcher matcher = pattern.matcher(executor.getWorkerReport());

        while (matcher.find()) {
            // Extract the number from the first capturing group
            long timeValue = Long.parseLong(matcher.group(1));

            assertNotEquals(timeValue, 0);
        }
    };
}