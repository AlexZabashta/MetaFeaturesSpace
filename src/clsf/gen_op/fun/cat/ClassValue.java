package clsf.gen_op.fun.cat;

import clsf.aDataset;
import clsf.aDataset.Item;

public class ClassValue implements CatFunction {

    public final int range;

    public ClassValue(aDataset dataset) {
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
