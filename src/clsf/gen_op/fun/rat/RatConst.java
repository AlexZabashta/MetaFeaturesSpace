package clsf.gen_op.fun.rat;

import clsf.Dataset.Item;

public class RatConst implements RatFunction {

    public final double value;

    public RatConst(double value) {
        this.value = value;
    }

    @Override
    public double applyAsDouble(Item item) {
        return value;
    }

    @Override
    public double min() {
        return value;
    }

    @Override
    public double max() {
        return value;
    }
}
