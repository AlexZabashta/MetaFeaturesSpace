package clsf.gen_op.fun.cat;

import java.util.Random;

import clsf.aDataset;
import clsf.aDataset.Item;

public class RandomCat implements CatFunction {

    public final int range;
    public final Random random;

    public RandomCat(int range, Random random) {
        if (range < 1) {
            throw new IllegalArgumentException("range = " + range + " < 1");
        }
        this.range = range;
        this.random = random;
    }

    @Override
    public int applyAsInt(Item item) {
        return random.nextInt(range);
    }

    @Override
    public int range() {
        return range;
    }

}
