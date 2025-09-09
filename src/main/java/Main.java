import matrix.MatrixMultiplication;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        MatrixMultiplication m = new MatrixMultiplication(1024, 6834723, 8);
        Instant start = Instant.now();
//        m.multiplySequential();
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);
        System.out.printf("Sequential %d ms\n", elapsedTime.toMillis());

        m.resetResultMatrix();
        start = Instant.now();
        m.multiplyParallel();
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.printf("Parallel %d ms\n", elapsedTime.toMillis());

        m.resetResultMatrix();
        start = Instant.now();
        m.multiplyForkJoin();
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.printf("ForkJoin Rec %d ms\n", elapsedTime.toMillis());

        m.resetResultMatrix();
        start = Instant.now();
        m.multiplyForkJoinIterative();
        end = Instant.now();
        elapsedTime = Duration.between(start, end);
        System.out.printf("ForkJoin Iter %d ms\n", elapsedTime.toMillis());
    }
}