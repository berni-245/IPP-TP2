package matrix;

import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class MatrixMultiplication {
    private final int size;
    private final int numThreads;
    private final boolean showFirstCell;
    private final double[][] left;
    private final double[][] right;
    private final double[][] result;

    public MatrixMultiplication(int size, int seed, int numThreads, boolean showFirstCell) {
        this.size = size;
        this.numThreads = numThreads;
        this.showFirstCell = showFirstCell;
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

        showFirstCellIfNeeded();
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

        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showFirstCellIfNeeded();
    }

    public void multiplyForkJoin() {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        int threshold = Math.max(20, size/numThreads);
        pool.invoke(new MultiplyTask(0, size, threshold));

        Utils.shutdownExecutor(pool);
        showFirstCellIfNeeded();
    }

    public void multiplyVirtualThreadsPerRow() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            final int rowIdx = row;
            futures.add(executor.submit(() -> multiplyRowInRange(rowIdx, rowIdx + 1)));
        }

        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showFirstCellIfNeeded();
    }

    public void multiplyVirtualThreadsPerChunk() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        distributeChunksForAllThreads(
                (start, end) -> futures.add(executor.submit(() -> multiplyRowInRange(start, end)))
        );

        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showFirstCellIfNeeded();
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
            chunkForThreadHandler.accept(start, end);
            start = end;
        }
    }

    private void showFirstCellIfNeeded() {
        if (showFirstCell)
            System.out.printf("The content of [0][0] in the matrix is %.2f\n", result[0][0]);
    }
}