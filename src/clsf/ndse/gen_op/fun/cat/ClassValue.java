package clsf.ndse.gen_op.fun.cat;

import clsf.Dataset;

public class ClassValue implements CatFunction {

    public final Dataset dataset;
    public final int range;

    public ClassValue(Dataset dataset) {
        this.dataset = dataset;
        this.range = dataset.numClasses;
    }

    @Override
    public int applyAsInt(int objectId) {
        return dataset.labels[objectId];
    }

    @Override
    public int range() {
        return range;
    }

}
