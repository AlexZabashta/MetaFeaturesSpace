package clsf.gen_op.fun.rat;

import clsf.Dataset;
import clsf.Dataset.Item;

public class RatValue implements RatFunction {
    public final int index;
    public final double min, max;

    public RatValue(int index, Dataset dataset) {
        this.index = index;
        this.min = dataset.min(index);
        this.max = dataset.max(index);
    }

    @Override
    public double applyAsDouble(Item item) {
        return item.ratValue(index);
    }

    @Override
    public double min() {
        return min;
    }

    @Override
    public double max() {
        return max;
    }

}
