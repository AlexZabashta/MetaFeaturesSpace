package nn.fld;

import nn.Activation;
import nn.Neuron;

public class Sum extends Neuron {
    final Activation activation;

    public Sum(int dendrites, int[] from, int[] wid, int to, Activation activation) {
        super(dendrites, from, wid, to);
        this.activation = activation;
    }

    public void forward(double[] x, double[] y, double[] w) {
        for (int d = 0; d < dendrites; d++) {
            x[to] += y[from[d]] * w[wid[d]];
        }
        y[to] = activation.activate(x[to]);
    }

    public void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        e_dy[to] = e[to] * activation.derivative(x[to]);
        for (int d = 0; d < dendrites; d++) {
            e[from[d]] += w[wid[d]] * e_dy[to];
        }
    }

}