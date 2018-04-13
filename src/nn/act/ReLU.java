package nn.act;

import nn.Activation;

public class ReLU implements Activation {

    double a = 0.01;

    @Override
    public double activate(double x) {
        if (x < 0) {
            return x * a;
        } else {
            return x;
        }

        // return Math.max(0, x);
    }

    @Override
    public double derivative(double x) {
        return (x < 0) ? a : 1;
    }

}
