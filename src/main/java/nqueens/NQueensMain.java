package nqueens;

import common.ParallelizationType;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

public class NQueensMain {
    public static void main(String[] args) {
        final int size = Integer.parseInt(System.getProperty("N"));
        final int numThreads = Integer.parseInt(System.getProperty("numThreads"));
        final int times = Integer.parseInt(System.getProperty("times"));
        final ParallelizationType type = ParallelizationType.fromString(System.getProperty("type"));
        final boolean showResult = Boolean.parseBoolean(System.getProperty("showResult"));


        NQueens nQueens = new NQueens(size, numThreads, showResult);

        switch (type) {
            case SEQUENTIAL -> runNTimesAndPrint(times, nQueens::solveSequential);
//            case PARALLEL -> runNTimesAndPrint(times, m, m::multiplyParallel);
//            case FORK_JOIN -> runNTimesAndPrint(times, m, m::multiplyForkJoin);
//            case VIRTUAL_PER_ROW -> runNTimesAndPrint(times, m, m::multiplyVirtualThreadsPerRow);
//            case VIRTUAL_PER_CHUNK -> runNTimesAndPrint(times, m, m::multiplyVirtualThreadsPerChunks);
        }
    }

    private static void runNTimesAndPrint(int nTimes, Runnable countSolutions) {
        for (int i = 0; i < nTimes; i++) {
            Instant start = Instant.now();
            countSolutions.run();
            Instant end = Instant.now();
            Duration elapsedTime = Duration.between(start, end);
            Locale.setDefault(Locale.ENGLISH);
            System.out.printf("%.10f\n", elapsedTime.toNanos()/1000000000.0);
        }
    }
}
