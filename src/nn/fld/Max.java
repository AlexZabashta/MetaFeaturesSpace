package nn.fld;

import nn.Activation;
import nn.Neuron;

public class Max extends Neuron {
    public Max(int dendrites, int[] from, int[] wid, int to, Activation activation) {
        super(dendrites, from, wid, to);
        this.activation = activation;
    }

    final Activation activation;

    @Override
    public void forward(double[] x, double[] y, double[] w) {
        x[to] = Double.NEGATIVE_INFINITY;
        for (int d = 0; d < dendrites; d++) {
            x[to] = Math.max(x[to], y[from[d]] * w[wid[d]]);
        }
        y[to] = activation.activate(x[to]);
    }

    @Override
    public void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        e_dy[to] = e[to] * activation.derivative(x[to]);
        for (int d = 0; d < dendrites; d++) {
            if (x[to] - y[from[d]] * w[wid[d]] < 1e-3) {
                e[from[d]] += w[wid[d]] * e_dy[to];
            }
        }
    }
}
