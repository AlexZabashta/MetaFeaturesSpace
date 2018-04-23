package test;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.imageio.ImageIO;

import nn.Builder;
import nn.NeuralNetwork;
import nn.act.Linear;
import nn.act.ReLU;
import nn.act.Sin;
import nn.act.Tanh;
import nn.fld.Fold;
import nn.fld.SoftMax;
import nn.fld.Sum;

public class TestCNN {
    final static Random random = new Random(237);

    static NeuralNetwork build(int k) {
        NeuralNetwork[] layers = new NeuralNetwork[5];

        Fold fold = new SoftMax(new ReLU());

        layers[0] = Builder.convLayer(256, 256, 3, 64, 64, 4, fold);
        layers[1] = Builder.convLayer(64, 64, 4, 16, 16, 5, fold);
        layers[2] = Builder.convLayer(16, 16, 5, 4, 4, 6, fold);
        layers[3] = Builder.convLayer(4, 4, 6, 1, 1, 42, fold);

        layers[4] = Builder.fullLayer(layers[3].outSize, k, new Sum(new Tanh()));

        return Builder.connect(layers);
    }

    static NeuralNetwork buildP(int k) {
        NeuralNetwork[] layers = new NeuralNetwork[5];

        Fold max = new SoftMax(new Linear());
        Fold sum = new Sum(new ReLU());

        layers[0] = Builder.convLayer(256, 256, 3, 64, 64, 5, sum);
        layers[1] = Builder.convLayer(64, 64, 5, 16, 16, 9, max);
        layers[2] = Builder.convLayer(16, 16, 9, 4, 4, 17, sum);
        layers[3] = Builder.convLayer(4, 4, 17, 1, 1, 30, max);

        // layers[4] = Builder.disperseLayer(layers[3].outSize, k, 10, new Random(), new Sum(new Tanh()));
        layers[4] = Builder.fullLayer(layers[3].outSize, k, new Sum(new Tanh()));
        return Builder.connect(layers);
    }

    static NeuralNetwork build1(int k) {
        NeuralNetwork[] layers = new NeuralNetwork[7];

        layers[0] = Builder.cnnSharedLayer(256, 256, 3, 17, 17, 3, new Sum(new Tanh()));
        layers[1] = Builder.maxPoolLayer(240, 240, 3, 4, 4, 4, new SoftMax(new Linear()));
        layers[2] = Builder.cnnSharedLayer(60, 60, 4, 5, 5, 4, new Sum(new ReLU()));
        layers[3] = Builder.maxPoolLayer(56, 56, 4, 4, 4, 5, new SoftMax(new Linear()));
        layers[4] = Builder.cnnSharedLayer(14, 14, 5, 7, 7, 5, new Sum(new ReLU()));

        layers[5] = Builder.disperseLayer(layers[4].outSize, 40, 10, random, new Sum(new Tanh()));
        layers[6] = Builder.fullLayer(layers[5].outSize, k, new Sum(new Tanh()));

        return Builder.connect(layers);
    }

    static NeuralNetwork buildMNIST() {
        NeuralNetwork[] layers = new NeuralNetwork[5];

        layers[0] = Builder.cnnSharedLayer(28, 28, 1, 9, 9, 3, new SoftMax(new Linear()));
        layers[1] = Builder.cnnSharedLayer(20, 20, 3, 9, 9, 5, new Sum(new ReLU()));
        layers[2] = Builder.cnnSharedLayer(12, 12, 5, 6, 6, 7, new Sum(new ReLU()));
        layers[3] = Builder.disperseLayer(layers[2].outSize, 30, 20, random, new Sum(new Tanh()));
        layers[4] = Builder.fullLayer(layers[3].outSize, 10, new Sum(new Tanh()));

        return Builder.connect(layers);
    }

    static Map<String, List<BufferedImage>> read(String path) throws IOException {

        Map<String, List<BufferedImage>> raw = new HashMap<>();

        for (File folder : new File(path).listFiles()) {
            String name = folder.getName();
            List<BufferedImage> images = new ArrayList<>();

            BufferedImage fake = null;

            for (File img : folder.listFiles()) {
                if (fake == null) {
                    fake = resize(ImageIO.read(img), 256, 256);
                }

                BufferedImage image = fake;
                images.add(image);
            }

            raw.put(name, images);

            System.out.println("Read from " + name);

        }
        return raw;
    }

    static void transpose(double[][] matrix) {
        int n = matrix.length;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < x; y++) {
                double tmp = matrix[x][y];
                matrix[x][y] = matrix[y][x];
                matrix[y][x] = tmp;
            }
        }
    }

    static void flipV(double[][] matrix) {
        int n = matrix.length;
        for (int x = 0; x < n; x++) {
            int l = 0, r = n - 1;
            while (l < r) {

                double tmp = matrix[x][l];
                matrix[x][l] = matrix[x][r];
                matrix[x][r] = tmp;

                ++l;
                --r;
            }
        }
    }

    static void flipH(double[][] matrix) {
        int n = matrix.length;
        for (int y = 0; y < n; y++) {
            int l = 0, r = n - 1;
            while (l < r) {

                double tmp = matrix[l][y];
                matrix[l][y] = matrix[r][y];
                matrix[r][y] = tmp;

                ++l;
                --r;
            }
        }
    }

    public static void main(String[] args) {
        NeuralNetwork nn = TestCNN.buildP(101);

        System.out.println(nn.inpSize);
        System.out.println(nn.outSize);
        System.out.println(nn.numWeights);
    }

    public static void pretest() throws IOException {
        int n = 400, k = 8, m = 256;
        Map<String, List<BufferedImage>> raw = read("imgdata\\101_ObjectCategories.tar\\101_ObjectCategories");
        List<Entry<String, List<BufferedImage>>> list = new ArrayList<>(raw.entrySet());

        Collections.sort(list, new Comparator<Entry<String, List<BufferedImage>>>() {
            @Override
            public int compare(Entry<String, List<BufferedImage>> u, Entry<String, List<BufferedImage>> v) {
                return -Integer.compare(u.getValue().size(), v.getValue().size());
            }
        });

        String[] name = new String[k];
        List<double[][]> data = new ArrayList<>();
        double[][][] rgb = new double[3][m][m];

        for (int i = 0; i < k; i++) {
            name[i] = list.get(i).getKey();
            double[] v = new double[k];
            Arrays.fill(v, -0.9999987654321);
            v[i] *= -1;

            List<BufferedImage> images = list.get(i).getValue();

            for (int j = 0; j < n; j++) {
                BufferedImage image = images.get(random.nextInt(images.size()));

                for (int x = 0; x < m; x++) {
                    for (int y = 0; y < m; y++) {
                        int color = image.getRGB(x, y);

                        rgb[0][x][y] = (color & 0xFF) / 255.0;
                        color >>>= 8;
                        rgb[1][x][y] = (color & 0xFF) / 255.0;
                        color >>>= 8;
                        rgb[2][x][y] = (color & 0xFF) / 255.0;
                    }
                }

                if (random.nextBoolean()) {
                    for (int z = 0; z < 3; z++) {
                        transpose(rgb[z]);
                    }
                }
                if (random.nextBoolean()) {
                    for (int z = 0; z < 3; z++) {
                        flipH(rgb[z]);
                    }
                }
                if (random.nextBoolean()) {
                    for (int z = 0; z < 3; z++) {
                        flipV(rgb[z]);
                    }
                }

                double[] f = new double[m * m * 3];

                int p = 0;

                for (int x = 0; x < m; x++) {
                    for (int y = 0; y < m; y++) {
                        for (int z = 0; z < 3; z++) {
                            f[p++] = rgb[z][x][y];
                        }
                    }
                }

                double[][] array = { f, v };
                data.add(array);
            }
            System.out.println(Arrays.toString(v));

        }

        list = null;
        System.gc();

    }

    static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
