package nn;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Learner {

    final double[] weights, speed;

    double learningRate, momentum;

    final Random random = new Random();

    final NeuralNetwork nn;

    public Learner(double learningRate, double momentum, NeuralNetwork nn) {
        this.weights = new double[nn.numWeights];

        for (int wid = 0; wid < nn.numWeights; wid++) {
            weights[wid] = random.nextGaussian() * learningRate ;
        }
        nn.setWZ(weights);

        this.speed = new double[nn.numWeights];
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.nn = nn;
    }

    public int[] update(double[] input, double[] output) {
        double[] x = Arrays.copyOf(input, nn.size);
        double[] y = x.clone();
        double[] w;

        synchronized (weights) {
            w = weights.clone();
        }

        nn.forward(x, y, w);

        int[] cm = new int[2];
        cm[0] = random.nextInt(output.length);
        cm[1] = random.nextInt(output.length);

        double maxY = -1;

        double[] e = new double[nn.size];

        for (int i = nn.size - nn.outSize, j = 0; i < nn.size; i++, j++) {
            e[i] = y[i] - output[j];

            if (output[j] > output[cm[0]]) {
                cm[0] = j;
            }

            if (y[i] > maxY) {
                maxY = y[i];
                cm[1] = j;
            }
        }

        double[] e_dy = new double[nn.size];
        nn.backwardError(x, y, e, e_dy, w);

        double[] dw = new double[nn.numWeights];

        nn.weightsError(x, y, e_dy, e_dy, w, dw);

        synchronized (weights) {

            for (int wid = 0; wid < nn.numWeights; wid++) {
                speed[wid] = speed[wid] * (1 - momentum) + momentum * Math.signum(dw[wid]);
                // speed[wid] += Math.signum(dw[wid]);
                weights[wid] -= learningRate * speed[wid];
            }
            learningRate *= 0.9999;

            if (random.nextInt(150) == 0) {
                System.out.println(learningRate);
            }
        }

        return cm;
    }

}
