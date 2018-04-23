package test;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FastBinImg implements Iterable<LabledImg>, Iterator<LabledImg> {

    final int n;

    final Random random;
    final List<List<int[]>> list = new ArrayList<>();

    public FastBinImg(String path, long seed) {
        random = new Random(seed);

        for (File folder : new File(path).listFiles()) {
            List<int[]> subList = new ArrayList<>();
            for (File file : folder.listFiles()) {
                int[] array = new int[256 * 256];

                byte[] bufer = new byte[4096];

                try (InputStream reader = new FileInputStream(file)) {
                    int p = 0;
                    int c = 0;

                    int r = 0;

                    int len;

                    while ((len = (reader.read(bufer))) > 0) {
                        for (int i = 0; i < len; i++) {
                            r <<= 8;
                            r |= bufer[i] & (0xFF);
                            ++c;

                            if (c == 4) {
                                array[p++] = r;
                                c = r = 0;
                            }

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                // for (int i = 0; i < array.length; i++) {
                // array[i] = raf.readInt();
                // }
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                subList.add(array);
            }
            System.out.println(folder.getName());
            list.add(subList);
        }

        n = list.size();

    }

    @Override
    public Iterator<LabledImg> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public LabledImg next() {
        int label = random.nextInt(n);
        List<int[]> subList = list.get(label);

        int[] array = subList.get(random.nextInt(subList.size()));

        double[] vector = new double[array.length * 3];

        int p = 0;
        for (int i = 0; i < array.length; i++) {
            int color = array[i];
            vector[p++] = (color & 0xFF) / 255.0;
            color >>>= 8;
            vector[p++] = (color & 0xFF) / 255.0;
            color >>>= 8;
            vector[p++] = (color & 0xFF) / 255.0;
        }

        return new LabledImg(vector, label);

    }
}
