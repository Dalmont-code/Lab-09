package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthreads;

    public MultiThreadedSumMatrix(final int nthreads) {
        this.nthreads = nthreads;
    }

    private class Worker extends Thread {

        private final double [][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;
        
        private Worker(final double[][] matrix, final int startpos, final int nelem) {
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }
        
        @Override
        public void run() {
            System.out.println("Working from row " + startpos + " to row " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double elem : this.matrix[i]) {
                    this.res += elem;
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length / nthreads +
                matrix.length % nthreads != 0 ? 1 : 0;
        List<Worker> workers = new ArrayList<>(nthreads);
        for (int i = 0; i < matrix.length; i += size) {
            workers.add(new Worker(matrix, i , size));
        }

        for (final Worker worker : workers) {
            worker.start();
        }

        double res = 0;
        for (final Worker worker : workers) {
            try {
                worker.join();
                res += worker.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return res;
    }
}
