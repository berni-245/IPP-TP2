package matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class MatrixMultiplication {
    private final int size;
    private final int numThreads;
    private final double[][] left;
    private final double[][] right;
    private final double[][] result;

    public MatrixMultiplication(int size, int seed, int numThreads) {
        this.size = size;
        this.numThreads = numThreads;
        left = new double[size][size];
        right = new double[size][size];
        result = new double[size][size];
        Random rand = new Random(seed);
        for (int rowIdx = 0; rowIdx < size; rowIdx++) {
            for (int colIdx = 0; colIdx < size; colIdx++) {
                left[rowIdx][colIdx] = rand.nextDouble();
                right[rowIdx][colIdx] = rand.nextDouble();
                result[rowIdx][colIdx] = 0;
            }
        }
    }

    public void multiplySequential() {
        multiplyRowInRange(0, size);

        System.out.println(result[0][0]);
    }

    public void multiplyParallel() {
        if (numThreads > size) {
            multiplySequential();
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        distributeChunksForAllThreads(
                (start, end) -> futures.add(executor.submit(() -> multiplyRowInRange(start, end)))
        );

        waitForAll(futures);
        shutdownExecutor(executor);

        System.out.println(result[0][0]);
    }

    public void multiplyForkJoin() {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        int threshold = Math.max(20, size/numThreads);
        pool.invoke(new MultiplyTask(0, size, threshold));

        shutdownExecutor(pool);

        System.out.println(result[0][0]);
    }

    public void multiplyForkJoinIterative() {
        if (numThreads > size) {
            multiplySequential();
            return;
        }

        List<MultiplyTaskIterative> tasks = new ArrayList<>();
        distributeChunksForAllThreads(
                (start, end) -> tasks.add(new MultiplyTaskIterative(start, end))
        );

        MultiplyTaskIterative.invokeAll(tasks);

        System.out.println(result[0][0]);
    }

    private class MultiplyTask extends RecursiveAction {
        private final int startRow;
        private final int endRow;
        private final int threshold;

        MultiplyTask(int startRow, int endRow, int threshold) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (endRow - startRow <= threshold) {
                multiplyRowInRange(startRow, endRow);
                return;
            }
            int middleRow = (startRow + endRow) / 2;
            invokeAll(new MultiplyTask(startRow, middleRow, threshold), new MultiplyTask(middleRow, endRow, threshold));
        }
    }

    private class MultiplyTaskIterative extends RecursiveAction {
        private final int startRow;
        private final int endRow;

        MultiplyTaskIterative(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            multiplyRowInRange(startRow, endRow);
        }
    }

    // endIdx exclusive
    private void multiplyRowInRange(int startIdx, int endIdx) {
        for (int rowIdx = startIdx; rowIdx < endIdx; rowIdx++) {
            for (int colIdx = 0; colIdx < size; colIdx++) {
                for (int k = 0; k < size; k++) {
                    result[rowIdx][colIdx] += left[rowIdx][k] * right[k][colIdx];
                }
            }
        }
    }

    public void resetResultMatrix() {
        for (int rowIdx = 0; rowIdx < size; rowIdx++) {
            for (int colIdx = 0; colIdx < size; colIdx++) {
                result[rowIdx][colIdx] = 0;
            }
        }
    }

    private void distributeChunksForAllThreads(BiConsumer<Integer, Integer> chunkForThreadHandler) {
        int rowsPerThread = size / numThreads;
        int remainder = size % numThreads;

        int start = 0;
        for (int i = 0; i < numThreads; i++) {
            int end = start + rowsPerThread + (i < remainder ? 1 : 0); // handle remainder
            // Borrar comentario: LA idea es que por cada tarea que sobre, se da una a cada thread hasta que no queden
            chunkForThreadHandler.accept(start, end);
            start = end;
        }
    }

    private void waitForAll(List<Future<?>> futures) {
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


    private void shutdownExecutor(ExecutorService executor) {
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

