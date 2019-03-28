package clsf.direct.gen_op.fun.rat;

import clsf.ClDataset.Item;

public class Sum implements RatFunction {
    public final RatFunction left, right;
    public final double min, max;

    @Override
    public double min() {
        return min;
    }

    @Override
    public double max() {
        return max;
    }

    public Sum(RatFunction left, RatFunction right) {
        this.left = left;
        this.right = right;

        this.min = left.min() + right.min();
        this.max = left.max() + right.max();

    }

    @Override
    public double applyAsDouble(Item item) {
        return left.applyAsDouble(item) + right.applyAsDouble(item);
    }

}
