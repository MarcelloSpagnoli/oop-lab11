package it.unibo.oop.workers02;

import java.util.stream.IntStream;

/**
 * SumMatrix implemented with threads.
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final Integer threads;

    /**
     * Public constructor.
     * @param threads number of threads to use.
     */
    public MultiThreadedSumMatrix(final Integer threads) {
        this.threads = threads;
    }

    private static class Worker extends Thread {
        private final int startColumn;
        private final int finishColumn;
        private final double[][] matrix;

        Worker(final double[][] matrix, final int startColumn, final int finishColumn) {
            this.startColumn = startColumn;
            this.finishColumn = finishColumn;
            this.matrix = matrix.clone();
        }

        public long getRes() {
            long sum = 0;
            for (int i = startColumn; i < finishColumn && i < matrix[0].length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                    sum += matrix[i][j];
                }
            }
            return sum;
        }

    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix[0].length % threads + matrix[0].length / threads;
        return IntStream.iterate(0, start -> start + size)
        .limit(threads)
        .mapToObj(start -> new Worker(matrix, start, start + size))
        .peek(Thread::start)
        .peek(MultiThreadedSumMatrix::joinUninterruptibly)
        .mapToLong(Worker::getRes)
        .sum();

    }


    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
