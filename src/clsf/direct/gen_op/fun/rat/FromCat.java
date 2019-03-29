package clsf.direct.gen_op.fun.rat;

import java.util.Random;

import org.apache.commons.math3.stat.StatUtils;

import clsf.direct.gen_op.fun.cat.CatFunction;

public class FromCat implements RatFunction {

    private final double[] map;
    public final double min, max;
    public final CatFunction node;

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
    public double applyAsDouble(int objectId) {
        return map[node.applyAsInt(objectId)];
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double min() {
        return min;
    }

}
