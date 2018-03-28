package nn.fld;

import nn.BPLearn;
import nn.Neuron;

public class One extends Neuron {
    final static int[] empty = new int[0];

    public One(int to) {
        super(0, empty, empty, to);
    }

    @Override
    public void forward(double[] x, double[] y, double[] w) {
        y[to] = 1.0;
    }

    @Override
    public void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
    }

   

}
