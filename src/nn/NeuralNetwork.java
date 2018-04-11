package nn;

import java.util.Arrays;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class NeuralNetwork implements BPLearn {

    final Neuron[] neurons;

    public final int inpSize, outSize, numWeights, size;

    public NeuralNetwork(int inpSize, int outSize, int numWeights, Neuron... neurons) {
        this.inpSize = inpSize;
        this.outSize = outSize;

        this.numWeights = numWeights;
        this.neurons = neurons;
        this.size = inpSize + neurons.length;
    }

    double[] get(double[] input, double[] w) {
        double[] x = Arrays.copyOf(input, size);
        double[] y = x.clone();
        forward(x, y, w);
        return Arrays.copyOfRange(y, size - outSize, size);
    }

    void update(double[] input, double[] output, double[] w, double learningRate) {
        double[] x = Arrays.copyOf(input, size);
        double[] y = x.clone();
        forward(x, y, w);

        double[] e = new double[size];
        for (int i = size - outSize, j = 0; i < size; i++, j++) {
            e[i] = y[i] - output[j];
        }

        double[] e_dy = new double[size];
        backwardError(x, y, e, e_dy, w);

        double[] dw = new double[numWeights];
        double[] cnt = new double[numWeights];

        weightsError(x, y, e_dy, e_dy, w, dw, cnt);

        for (int wid = 0; wid < numWeights; wid++) {
            if (cnt[wid] > 0) {
                w[wid] -= learningRate * dw[wid] / cnt[wid];
            }
        }

    }

    @Override
    public void forward(double[] x, double[] y, double[] w) {
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].forward(x, y, w);
        }
    }

    @Override
    public void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        for (int i = neurons.length - 1; i >= 0; i--) {
            neurons[i].backwardError(x, y, e, e_dy, w);
        }
    }

    @Override
    public void weightsError(double[] x, double[] y, double[] e, double[] e_dy, double[] w, double[] dw, double[] cnt) {
        for (int i = 0; i < neurons.length; i++) {
            neurons[i].weightsError(x, y, e, e_dy, w, dw, cnt);
        }
    }

}
