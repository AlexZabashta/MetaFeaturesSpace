package clsf.gen_op.fun.cat;

import clsf.aDataset;
import clsf.aDataset.Item;

public class CatConst implements CatFunction {

    @Override
    public int applyAsInt(Item item) {
        return 0;
    }

    @Override
    public int range() {
        return 1;
    }

}
