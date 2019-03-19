package clsf.gen_op;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import clsf.aDataset;
import utils.AddClassMapper;
import utils.RandomUtils;
import utils.RemoveClassMapper;

public class ChangeNumObjects implements UnaryOperator<aDataset> {

    private final Random random;

    public ChangeNumObjects(Random random) {
        this.random = random;
    }

    public static aDataset apply(aDataset dataset, Random random) {
        int n = dataset.numObjects();
        return apply(dataset, random, random.nextInt(n * 2) + 2);
    }

    public static aDataset apply(aDataset dataset, Random random, int newNumObjects) {
        int oldNumObjects = dataset.numObjects();

        if (oldNumObjects == newNumObjects) {
            return dataset;
        }

        IntUnaryOperator maper;
        if (oldNumObjects < newNumObjects) {
            maper = new RemoveClassMapper(newNumObjects, oldNumObjects, random);
        } else {
            maper = new AddClassMapper(newNumObjects, oldNumObjects, random);
        }

        int c = dataset.numCatAttr();
        int r = dataset.numRatAttr();

        int[][] cat = new int[newNumObjects][c + 1];
        double[][] rat = new double[newNumObjects][r];

        for (int i = 0; i < newNumObjects; i++) {
            int j = maper.applyAsInt(i);

            for (int cid = 0; cid < c; cid++) {
                cat[i][cid] = dataset.catValue(j, cid);
            }

            for (int rid = 0; rid < r; rid++) {
                rat[i][rid] = dataset.ratValue(j, rid);
            }
            cat[i][c] = dataset.classValue(j);
        }

        return new aDataset(newNumObjects, c, cat, r, rat);
    }

    @Override
    public aDataset apply(aDataset dataset) {
        return apply(dataset, random);
    }

}
