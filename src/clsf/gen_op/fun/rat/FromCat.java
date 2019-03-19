package clsf.gen_op.fun.rat;

import java.util.Random;

import org.apache.commons.math3.stat.StatUtils;

import clsf.aDataset.Item;
import clsf.gen_op.fun.cat.CatFunction;

public class FromCat implements RatFunction {

    public final CatFunction node;
    private final double[] map;
    public final double min, max;

    public FromCat(CatFunction node, Random random) {
        this.node = node;
        int range = node.range();
        map = new double[range];

        double tmpMin = Double.POSITIVE_INFINITY;
        double tmpMax = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < range; i++) {
            map[i] = random.nextGaussian();
            tmpMin = Math.min(tmpMin, map[i]);
            tmpMax = Math.max(tmpMax, map[i]);
        }

        this.min = tmpMin;
        this.max = tmpMax;

        StatUtils.min(map);
    }

    @Override
    public double applyAsDouble(Item item) {
        return map[node.applyAsInt(item)];
    }

    @Override
    public double min() {
        return min;
    }

    @Override
    public double max() {
        return max;
    }

}
