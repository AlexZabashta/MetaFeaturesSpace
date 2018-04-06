package nn.fld;

import nn.Activation;
import nn.Neuron;

public class Max implements Fold {
    final Activation activation;

    public Max(Activation activation) {
        this.activation = activation;
    }

    @Override
    public void forward(int length, int[] sid, int[] wid, int id, double[] x, double[] y, double[] w) {
        x[id] = w[wid[length]];
        for (int d = 0; d < length; d++) {
            x[id] = Math.max(x[id], y[sid[d]] * w[wid[d]]);
        }
        y[id] = activation.activate(x[id]);
    }

    @Override
    public void backwardError(int length, int[] sid, int[] wid, int id, double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        e_dy[id] = e[id] * activation.derivative(x[id]);
        for (int d = 0; d < length; d++) {
            if (x[id] - y[sid[d]] * w[wid[d]] < 1e-3) {
                e[sid[d]] += w[wid[d]] * e_dy[id];
            }
        }
    }
}
