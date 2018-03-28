package nn.act;

import nn.Activation;

public class Tanh implements Activation {

    @Override
    public double activate(double x) {
        return Math.tanh(x);
    }

    @Override
    public double derivative(double x) {
        double tanh = Math.tanh(x);
        return 1 - tanh * tanh;
    }
}