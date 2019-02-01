package nn.act;

import nn.Activation;

public class SoftSign implements Activation {
    @Override
    public double activate(double x) {
        return x / (1 + Math.abs(x));
    }

    @Override
    public double derivative(double x) {
        double t = 1 + Math.abs(x);
        return 1 / (t * t);
    }
}