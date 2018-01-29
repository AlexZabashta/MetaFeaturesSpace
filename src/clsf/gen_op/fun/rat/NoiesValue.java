package clsf.gen_op.fun.rat;

import java.util.Random;

import clsf.Dataset.Item;

public class NoiesValue implements RatFunction {
    public final Random random;

    public NoiesValue(Random random) {
        this.random = random;
    }

    @Override
    public double applyAsDouble(Item value) {
        return Math.max(-10, Math.min(+10, random.nextGaussian()));
    }

    @Override
    public double min() {
        return -10.0;
    }

    @Override
    public double max() {
        return +10.0;
    }

}
