package clsf.direct.gen_op.fun.cat;

import java.util.Random;

public class RandomCat implements CatFunction {

    public final Random random;
    public final int range;

    public RandomCat(int range, Random random) {
        if (range < 1) {
            throw new IllegalArgumentException("range = " + range + " < 1");
        }
        this.range = range;
        this.random = random;
    }

    @Override
    public int applyAsInt(int objectId) {
        return 0;
    }

    @Override
    public int range() {
        return range;
    }

}
