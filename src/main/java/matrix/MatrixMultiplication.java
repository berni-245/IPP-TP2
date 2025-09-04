package matrix;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {
    private final int size;
    private final Random rand;
    private final double[][] left;
    private final double[][] right;
    private final double[][] result;

    public MatrixMultiplication(int size, int seed) {
        this.size = size;
        left = new double[size][size];
        right = new double[size][size];
        result = new double[size][size];
        rand = new Random(seed);
    }

    public void multiplySequential() {
        initializeAllMatrix();

        multiplyRowInRange(0, size);

        System.out.println(result[0][0]);
    }

    public void multiplyParallel(int numThreads) {
        if (numThreads > size) {
            multiplySequential();
            return;
        }

        initializeAllMatrix();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int rowsPerThread = size / numThreads;
        int remainder = size % numThreads;

        int start = 0;
        for (int i = 0; i < numThreads; i++) {
            int end = start + rowsPerThread + (i < remainder ? 1 : 0); // hand remainder
            int finalStart = start;
            executor.submit(() -> multiplyRowInRange(finalStart, end));
            start = end;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println(result[0][0]);
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

    private void initializeAllMatrix() {
        for (int rowIdx = 0; rowIdx < size; rowIdx++) {
            for (int colIdx = 0; colIdx < size; colIdx++) {
                left[rowIdx][colIdx] = rand.nextDouble();
                right[rowIdx][colIdx] = rand.nextDouble();
                result[rowIdx][colIdx] = 0;
            }
        }
    }
}
