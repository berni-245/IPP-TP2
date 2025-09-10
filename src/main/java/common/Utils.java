package common;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static void waitForAll(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get(); // bloquea hasta que la tarea termine
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    public static void shutdownExecutor(ExecutorService executor) {
        // Note: I don't use try-with-resources because that calls only shutdown() and I want
        // to make sure the platform threads are free between methods using shutdownNow() as last resource
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
