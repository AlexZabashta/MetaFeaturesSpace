package test;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BinImgFileReader implements Closeable, Iterable<LabledImg>, Iterator<LabledImg> {

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

    private boolean open = true;

    final BlockingQueue<LabledImg> queue = new ArrayBlockingQueue<>(16, false);

    final Thread thread;

    final int n;

    public BinImgFileReader(String path, long seed) {

        final List<File[]> list = new ArrayList<>();

        for (File folder : new File(path).listFiles()) {
            list.add((folder.listFiles()));
        }

        n = list.size();

        final int k = 256;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Random random = new Random(seed);
                try {
                    while (open) {

                        int c = random.nextInt(n);
                        File[] files = list.get(c);
                        File file = files[random.nextInt(files.length)];

                        double[] vector = new double[256 * 256 * 3];

                        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                            int p = 0;
                            for (int x = 0; x < k; x++) {
                                for (int y = 0; y < k; y++) {
                                    int color = raf.readInt();
                                    vector[p++] = (color & 0xFF) / 255.0;
                                    color >>>= 8;
                                    vector[p++] = (color & 0xFF) / 255.0;
                                    color >>>= 8;
                                    vector[p++] = (color & 0xFF) / 255.0;
                                }
                            }
                        } catch (IOException e) {
                            System.err.println(e);
                            open = false;
                            break;
                        }

                        queue.put(new LabledImg(vector, c));
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
        open = false;
    }

    @Override
    public Iterator<LabledImg> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return open || !queue.isEmpty();
    }

    @Override
    public LabledImg next() {
        if (open) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                open = false;
                return null;
            }
        } else {
            return queue.poll();
        }
    }
}
