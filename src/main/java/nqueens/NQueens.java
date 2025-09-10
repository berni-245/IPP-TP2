package nqueens;

import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class NQueens {
    private final int N;
    private final int numThreads;
    private final boolean showResult;
    private final AtomicInteger solutions;

    public NQueens(int N, int numThreads, boolean showResult) {
        this.N = N;
        this.numThreads = numThreads;
        this.showResult = showResult;
        solutions = new AtomicInteger(0);
    }

    public void solveSequential() {
        solutions.set(0);
        solve(0, new int[N]);
        showResultIfNeeded();
    }

    public void solveParallel() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();



        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showResultIfNeeded();
    }

    public void solveForkJoin() {

    }

    public void solveVirtualThreadsPerRow() {

    }

    public void solveVirtualThreadsPerChunk() {

    }

    private boolean isSafe(int[] board, int row, int col) {
        for (int i = 0; i < row; i++) {
            if (board[i] == col ||
                    board[i] - i == col - row ||
                    board[i] + i == col + row)
                return false;
        }
        return true;
    }

    private void solve(int row, int[] board) {
        if (row == N) {
            solutions.incrementAndGet();
            return;
        }
        for (int col = 0; col < N; col++) {
            if (isSafe(board, row, col)) {
                board[row] = col;
                solve(row + 1, board);
            }
        }
    }

    private void showResultIfNeeded() {
        if (showResult)
            System.out.printf("Number of solutions %d\n", solutions.get());
    }
}
