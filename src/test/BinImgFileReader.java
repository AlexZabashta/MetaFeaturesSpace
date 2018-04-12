package test;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BinImgFileReader implements Closeable {

    static void flipH(int[][] matrix) {
        int n = matrix.length;
        for (int y = 0; y < n; y++) {
            int xl = 0, xr = n - 1;
            while (xl < xr) {
                swap(matrix, xl++, y, xr--, y);
            }
        }
    }

    static void flipV(int[][] matrix) {
        int n = matrix.length;
        for (int x = 0; x < n; x++) {
            int yl = 0, yr = n - 1;
            while (yl < yr) {
                swap(matrix, x, yl++, x, yr--);
            }
        }
    }

    static void swap(int[][] matrix, int x1, int y1, int x2, int y2) {
        matrix[x1][y1] ^= matrix[x2][y2];
        matrix[x2][y2] ^= matrix[x1][y1];
        matrix[x1][y1] ^= matrix[x2][y2];
    }

    static void transpose(int[][] matrix) {
        int n = matrix.length;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < x; y++) {
                swap(matrix, x, y, y, x);
            }
        }
    }

    final BlockingQueue<double[][][]> queue = new ArrayBlockingQueue<>(16, false);

    final Thread thread;

    public BinImgFileReader(String path) {

        final List<File[]> list = new ArrayList<>();

        for (File folder : new File(path).listFiles()) {
            list.add((folder.listFiles()));
        }

        final int n = list.size();

        final int k = 256;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Random random = new Random();
                try {
                    while (true) {

                        int c = random.nextInt(n);
                        File[] files = list.get(c);
                        File file = files[random.nextInt(files.length)];

                        int[][] matrix = new int[k][k];

                        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                            for (int x = 0; x < k; x++) {
                                for (int y = 0; y < k; y++) {
                                    matrix[x][y] = raf.readInt();
                                }
                            }
                        } catch (IOException e) {
                            System.err.println(e);
                        }

                        if (random.nextBoolean()) {
                            transpose(matrix);
                        }

                        if (random.nextBoolean()) {
                            flipH(matrix);
                        }

                        if (random.nextBoolean()) {
                            flipV(matrix);
                        }

                        double[][][] rgb = new double[3][256][256];

                        for (int x = 0; x < k; x++) {
                            for (int y = 0; y < k; y++) {
                                int color = matrix[x][y];
                                rgb[2][x][y] = (color & 0xFF) / 255.0;
                                color >>>= 8;
                                rgb[1][x][y] = (color & 0xFF) / 255.0;
                                color >>>= 8;
                                rgb[0][x][y] = (color & 0xFF) / 255.0;
                            }
                        }
                        queue.put(rgb);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    @Override
    public void close() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public double[][][] next() {

        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
