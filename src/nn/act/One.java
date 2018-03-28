package nn.act;

import nn.Activation;

public class One implements Activation {

    @Override
    public double activate(double value) {
        return 1;
    }

    @Override
    public double derivative(double value) {
        return 0;
    }

}
