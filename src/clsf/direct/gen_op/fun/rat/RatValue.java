package clsf.direct.gen_op.fun.rat;

import clsf.ClDataset;

public class RatValue implements RatFunction {
    public final ClDataset dataset;
    public final int index;
    public final double min, max;

    public RatValue(int index, ClDataset dataset) {
        this.index = index;
        this.dataset = dataset;
        this.min = dataset.min(index);
        this.max = dataset.max(index);
    }

    @Override
    public double applyAsDouble(int objectId) {
        return dataset.value(objectId, index);
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double min() {
        return min;
    }

}
