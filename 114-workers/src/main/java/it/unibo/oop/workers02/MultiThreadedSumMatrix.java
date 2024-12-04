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
    public MultiThreadedSumMatrix(final int threads) {
        this.threads = threads;
    }

    private static long getRes(final double[][] matrix, final int startColumn, final int finishColumn) {
        long sum = 0;
        for (int i = startColumn; i < finishColumn && i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix[0].length % threads + matrix[0].length / threads;
        return IntStream.iterate(0, start -> start + size)
        .limit(threads)
        .parallel()
        .mapToLong(start -> getRes(matrix, start, start + size))
        .sum();
    }
}
