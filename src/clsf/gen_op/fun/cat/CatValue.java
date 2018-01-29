package clsf.gen_op.fun.cat;

import clsf.Dataset;
import clsf.Dataset.Item;

public class CatValue implements CatFunction {

    public final int range;
    public final int index;

    public CatValue(int index, Dataset dataset) {
        this.index = index;
        this.range = dataset.categorySize(index);
    }

    @Override
    public int applyAsInt(Item item) {
        return item.catValue(index);
    }

    @Override
    public int range() {
        return range;
    }

}
