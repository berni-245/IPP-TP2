package matrix;

import common.ParallelizationType;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        final int size = Integer.parseInt(System.getProperty("size"));
        final int seed = 6834723;
        final int numThreads = Integer.parseInt(System.getProperty("numThreads"));
        final int times = Integer.parseInt(System.getProperty("times"));
        final ParallelizationType type = ParallelizationType.fromString(System.getProperty("type"));
        final boolean showFirstCell = Boolean.parseBoolean(System.getProperty("showFirstCell"));


        MatrixMultiplication m = new MatrixMultiplication(size, seed, numThreads, showFirstCell);

        switch (type) {
            case SEQUENTIAL -> runNTimesAndPrint(times, m, m::multiplySequential);
            case PARALLEL -> runNTimesAndPrint(times, m, m::multiplyParallel);
            case FORK_JOIN -> runNTimesAndPrint(times, m, m::multiplyForkJoin);
            case VIRTUAL_PER_ROW -> runNTimesAndPrint(times, m, m::multiplyVirtualThreadsPerRow);
            case VIRTUAL_PER_CHUNK -> runNTimesAndPrint(times, m, m::multiplyVirtualThreadsPerChunks);
        }
    }

    private static void runNTimesAndPrint(int nTimes, MatrixMultiplication m, Runnable multiply) {
        for (int i = 0; i < nTimes; i++) {
            Instant start = Instant.now();
            multiply.run();
            Instant end = Instant.now();
            Duration elapsedTime = Duration.between(start, end);
            System.out.printf("%d\n", elapsedTime.toMillis());
            m.resetResultMatrix();
        }
    }
}