package clsf.ndse.gen_op.fun.rat;

import clsf.Dataset;

public class RatValue implements RatFunction {
    public final Dataset dataset;
    public final int index;

    public RatValue(int index, Dataset dataset) {
        this.index = index;
        this.dataset = dataset;
    }

    @Override
    public double applyAsDouble(int objectId) {
        return dataset.data[objectId][index];
    }

    @Override
    public double max() {
        return dataset.max[index];
    }

    @Override
    public double min() {
        return dataset.min[index];
    }

}
