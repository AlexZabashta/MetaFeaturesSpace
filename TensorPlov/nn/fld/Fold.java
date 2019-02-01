package nn.fld;

import nn.Neuron;

public interface Fold {
    void forward(int dendrites, int[] from, int[] wid, int to, double[] x, double[] y, double[] w);

    void backwardError(int dendrites, int[] from, int[] wid, int to, double[] x, double[] y, double[] e, double[] e_dy, double[] w);
}
