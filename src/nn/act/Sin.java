package nn.act;

import nn.Activation;

public class Sin implements Activation {

    @Override
    public double activate(double value) {
        return Math.sin(value);
    }

    @Override
    public double derivative(double value) {
        return Math.cos(value);
    }

}
