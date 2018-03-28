package nn.act;

import nn.Activation;

public class ReLU implements Activation {

    @Override
    public double activate(double x) {
        return Math.max(0, x);
    }

    @Override
    public double derivative(double x) {
        return (x < 0) ? 0 : 1;
    }

}
