package test;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import nn.Learner;
import nn.NeuralNetwork;

public class TestImgs extends JFrame {

    final int n = 20;
    private double[][] lm = new double[n][n];
    private double la = 1.0 / n;

    final double alpha = 0.95;

    public void draw(int iter, int[][] cm) {

        int all = 0;
        int cor = 0;

        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += cm[i][j];
            }

            all += sum;
            cor += cm[i][i];

            if (sum > 0) {
                for (int j = 0; j < n; j++) {
                    lm[i][j] = lm[i][j] * alpha + cm[i][j] * (1.0 - alpha) / sum;
                }
            }
        }

        la = la * alpha + cor * (1.0 - alpha) / all;

        String state = String.format(Locale.ENGLISH, "%3d %8.3f", iter, la * 100);

        setTitle(state);
        System.out.println(state);

        int s = 800 / n;
        int m = s * n;
        BufferedImage image = new BufferedImage(m, m, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < m; x++) {
            for (int y = 0; y < m; y++) {
                float g = (float) (1 - lm[y / s][x / s]);
                Color color = new Color(g, g, g);
                image.setRGB(x, y, color.getRGB());
            }
        }

        for (int i = 0; i < n; i++) {
            int rgb = Color.RED.getRGB();

            int x = i * s, y = i * s;

            for (int dx = 0; dx < s; dx++) {
                image.setRGB(x + dx, y + s - s, rgb);
                image.setRGB(x + dx, y + s - 1, rgb);
            }

            for (int dy = 0; dy < s; dy++) {
                image.setRGB(x + s - s, y + dy, rgb);
                image.setRGB(x + s - 1, y + dy, rgb);
            }

        }

        graph.setIcon(new ImageIcon(image));

        repaint();

    }

    class NNTester implements Runnable {
        int n = 20, m = 72;

        double[][][] imgs = new double[n][m][128 * 128];

        final TestImgs testImgs;

        public NNTester(TestImgs testImgs) {
            this.testImgs = testImgs;
        }

        @Override
        public void run() {

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    try {
                        File file = new File("imgdata\\coil20\\obj" + (i + 1) + "__" + (j) + ".png");
                        BufferedImage image = ImageIO.read(file);

                        int p = 0;
                        for (int x = 0; x < 128; x++) {
                            for (int y = 0; y < 128; y++) {
                                imgs[i][j][p++] = (image.getRGB(x, y) & 0xFF) / 255.0;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(i);
            }

            NeuralNetwork nn = TestCNN.buildCoil();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            final Learner learner = new Learner(0.001, 0.5, nn);

            Random random = new Random();

            int bs = 100;

            for (int iter = 1; iter <= 15000; iter++) {
                int[][] cm = new int[n][n];

                double target = 0.999999;// Math.tanh(iter - 1);

                Future<int[]>[] futures = new Future[bs];

                for (int i = 0; i < bs; i++) {

                    final int e = random.nextInt(n);
                    final double[] input = imgs[e][random.nextInt(m)];

                    futures[i] = executor.submit(new Callable<int[]>() {
                        @Override
                        public int[] call() {
                            double[] output = new double[n];
                            Arrays.fill(output, -target);
                            output[e] *= -1;
                            return learner.update(input, output);
                        }
                    });
                }

                for (int i = 0; i < bs; i++) {
                    try {
                        int[] r = futures[i].get();
                        cm[r[0]][r[1]] += 1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                testImgs.draw(iter, cm);
            }

            executor.shutdown();
        }

    }

    class MNIST implements Runnable {
        final TestImgs testImgs;

        public MNIST(TestImgs testImgs) {
            this.testImgs = testImgs;
        }

        @Override
        public void run() {

            List<List<Digit>> digits = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                digits.add(new ArrayList<>());
            }

            for (Digit digit : Digit.readDigits("imgdata\\data.bin")) {
                digits.get(digit.label).add(digit);
            }

            final int m = digits.size();

            NeuralNetwork nn = TestCNN.buildMNIST();

            Random random = new Random();

            double[] w = new double[nn.numWeights];

            for (int i = 0; i < w.length; i++) {
                w[i] = random.nextGaussian() / 10;
            }

            for (int iter = 1; iter <= 1000; iter++) {
                int[][] cm = new int[n][n];
                for (int cnt = 0; cnt < 100; cnt++) {

                    if (cnt % 10 == 11) {
                        double[] array = w.clone();
                        for (int i = 0; i < array.length; i++) {
                            array[i] = Math.abs(array[i]);
                        }
                        Arrays.sort(array);
                        System.out.println(Arrays.toString(Arrays.copyOfRange(array, array.length - 30, array.length)));

                    }

                    int e = random.nextInt(n);
                    List<Digit> subList = digits.get(e);
                    Digit digit = subList.get(random.nextInt(subList.size()));

                    double[] input = new double[28 * 28];

                    for (int i = 0; i < input.length; i++) {
                        input[i] = digit.convert(digit.pixels[i]);
                    }

                    double[] output = new double[n];
                    Arrays.fill(output, -0.9999987654321);
                    output[e] *= -1;

                    output = nn.update(input, output, w, 0.001);

                    int r = random.nextInt(n);

                    for (int i = 0; i < n; i++) {
                        if (output[i] > output[r]) {
                            r = i;
                        }
                    }

                    cm[e][r] += 1;

                }
                testImgs.draw(iter, cm);
            }
        }

    }

    JLabel graph = new JLabel();

    public TestImgs() {

        // setLayout(null);

        graph.setHorizontalAlignment(JLabel.CENTER);
        graph.setVerticalAlignment(JLabel.CENTER);

        add(graph);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        setBounds(640, 320, 640, 640);
        setVisible(true);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                lm[i][j] = la;
            }
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                final FastBinImg reader = new FastBinImg("imgdata\\bin", 1234);

                try {
                    while (true) {

                        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

                        double[] vector = reader.next().vector;

                        int p = 0;

                        for (int x = 0; x < 256; x++) {
                            for (int y = 0; y < 256; y++) {
                                float b = (float) vector[p++];
                                float g = (float) vector[p++];
                                float r = (float) vector[p++];
                                Color color = new Color(r, g, b);
                                image.setRGB(x, y, color.getRGB());
                            }
                        }

                        graph.setIcon(new ImageIcon(image));
                        repaint();

                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        (new Thread(new NNTester(this))).start();
        // (new Thread(new MNIST(this))).start();
    }

    public static void main(String[] args) {
        JFrame frame = new TestImgs();
    }

}
