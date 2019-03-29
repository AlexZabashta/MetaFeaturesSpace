package clsf.direct.gen_op.fun.cat;

import clsf.ClDataset;

public class ClassValue implements CatFunction {

    public final ClDataset dataset;
    public final int range;

    public ClassValue(ClDataset dataset) {
        this.dataset = dataset;
        this.range = dataset.numClasses;
    }

    @Override
    public int applyAsInt(int objectId) {
        return dataset.classValue(objectId);
    }

    @Override
    public int range() {
        return range;
    }

}
