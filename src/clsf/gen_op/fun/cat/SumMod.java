package clsf.gen_op.fun.cat;

import java.util.Random;

import clsf.Dataset;
import clsf.Dataset.Item;
import utils.RandomUtils;

public class SumMod implements CatFunction {

    private final int[] p, q, r;
    public final CatFunction lft, rgt;

    public SumMod(CatFunction lft, CatFunction rgt, int range, Random random) {
        this.lft = lft;
        this.rgt = rgt;

        this.p = RandomUtils.randomPermutation(lft.range(), random);
        this.q = RandomUtils.randomPermutation(rgt.range(), random);
        this.r = RandomUtils.randomPermutation(range, random);
    }

    @Override
    public int applyAsInt(Item item) {
        int x = p[lft.applyAsInt(item)];
        int y = q[rgt.applyAsInt(item)];        
        return r[(x + y) % range()];
    }

    @Override
    public int range() {
        return r.length;
    }

}
