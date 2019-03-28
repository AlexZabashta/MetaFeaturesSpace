package clsf.direct.gen_op.fun.rat;

import clsf.ClDataset;
import clsf.ClDataset.Item;

public class RatValue implements RatFunction {
    public final int index;
    public final double min, max;

    public RatValue(int index, ClDataset dataset) {
        this.index = index;
        this.min = dataset.min(index);
        this.max = dataset.max(index);
    }

    @Override
    public double applyAsDouble(Item item) {
        return item.value(index);
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
