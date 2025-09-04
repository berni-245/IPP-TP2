package matrix;

import java.util.Arrays;
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

    private void multiplyRowInRange(int startRowIdx, int chunkSize) {
        for (int rowIdx = startRowIdx; rowIdx < chunkSize + startRowIdx; rowIdx++) {
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
