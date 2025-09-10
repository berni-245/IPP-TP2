package nqueens;

import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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
        solutions.set(0);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        launchFutureInFirstRow(executor, futures);

        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showResultIfNeeded();
    }

    public void solveForkJoin() {
        solutions.set(0);
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        int threshold = 6;
        pool.invoke(new NQueensTask(0, new int[N], threshold));
        Utils.shutdownExecutor(pool);
        showResultIfNeeded();
    }

    private class NQueensTask extends RecursiveAction {
        private final int row;
        private final int[] board;
        private final int threshold;

        NQueensTask(int row, int[] board, int threshold) {
            this.row = row;
            this.board = board;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (row == N) {
                solutions.incrementAndGet();
                return;
            }
            if (row >= threshold) {
                solve(row, board);
                return;
            }
            List<NQueensTask> subtasks = new ArrayList<>();
            for (int col = 0; col < N; col++) {
                if (isSafe(board, row, col)) {
                    int[] newBoard = board.clone();
                    newBoard[row] = col;
                    subtasks.add(new NQueensTask(row + 1, newBoard, threshold));
                }
            }
            if (!subtasks.isEmpty()) {
                invokeAll(subtasks);
            }
        }
    }



    public void solveVirtualThreadsPerRow() {
        solutions.set(0);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        launchFutureInFirstRow(executor, futures);

        Utils.waitForAll(futures);
        Utils.shutdownExecutor(executor);
        showResultIfNeeded();
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

    private void launchFutureInFirstRow(ExecutorService executor, List<Future<?>> futures) {
        for (int col = 0; col < N; col++) {
            final int firstCol = col;
            futures.add(executor.submit(() -> {
                int[] board = new int[N];
                board[0] = firstCol;
                solve(1, board);
            }));
        }
    }

    private void showResultIfNeeded() {
        if (showResult)
            System.out.printf("Number of solutions %d\n", solutions.get());
    }
}
