package clsf.gen_op.fun.cat;

import clsf.Dataset;
import clsf.Dataset.Item;

public class ClassValue implements CatFunction {

    public final int range;

    public ClassValue(Dataset dataset) {
        this.range = dataset.numClasses();
    }

    @Override
    public int applyAsInt(Item item) {
        return item.classValue();
    }

    @Override
    public int range() {
        return range;
    }

}
