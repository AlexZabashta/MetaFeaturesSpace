package clsf.gen_op.fun.rat;

import clsf.Dataset.Item;

public class Abs implements RatFunction {
    public final RatFunction node;

    public final double max;

    public Abs(RatFunction node) {
        this.node = node;
        this.max = Math.max(-node.min(), node.max());
    }

    @Override
    public double applyAsDouble(Item item) {
        return Math.abs(node.applyAsDouble(item));
    }

    @Override
    public double min() {
        return 0;
    }

    @Override
    public double max() {
        return max;
    }

}
