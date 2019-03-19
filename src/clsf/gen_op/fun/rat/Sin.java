package clsf.gen_op.fun.rat;

import clsf.aDataset.Item;

public class Sin implements RatFunction {
    public final RatFunction node;

    public Sin(RatFunction node) {
        this.node = node;
    }

    @Override
    public double applyAsDouble(Item item) {
        return Math.sin(node.applyAsDouble(item));
    }

    @Override
    public double min() {
        return -1.0;
    }

    @Override
    public double max() {
        return +1.0;
    }

}
