package nn.fld;

import nn.Activation;
import nn.Neuron;

public class SoftMax implements Fold {
    final Activation activation;

    public SoftMax(Activation activation) {
        this.activation = activation;
    }

    public static double[] softMax(double[] array) {
        int len = array.length;
        double[] softMax = new double[len];
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += (softMax[i] = Math.exp(array[i]));
        }
        for (int i = 0; i < len; i++) {
            softMax[i] /= sum;
        }
        return softMax;
    }

    double[] activate(int length, int[] sid, int[] wid, int id, double[] x, double[] y, double[] w) {
        double[] array = new double[length + 1];
        array[length] = w[wid[length]];
        for (int d = 0; d < length; d++) {
            array[d] = y[sid[d]] * w[wid[d]];
        }
        return array;
    }

    @Override
    public void forward(int length, int[] sid, int[] wid, int id, double[] x, double[] y, double[] w) {
        double[] array = activate(length, sid, wid, id, x, y, w);
        double[] softm = softMax(array);

        for (int d = 0; d <= length; d++) {
            x[id] += softm[d] * array[d];
        }

        y[id] = activation.activate(x[id]);
    }

    @Override
    public void backwardError(int length, int[] sid, int[] wid, int id, double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        e_dy[id] = e[id] * activation.derivative(x[id]);
        double[] array = activate(length, sid, wid, id, x, y, w);
        double[] softm = softMax(array);

        for (int d = 0; d < length; d++) {
            e[sid[d]] += softm[d] * w[wid[d]] * e_dy[id];
        }
    }
}
