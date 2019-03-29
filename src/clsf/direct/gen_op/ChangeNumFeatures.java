package clsf.direct.gen_op;

import java.util.Random;

import clsf.ClDataset;
import clsf.direct.gen_op.fun.rat.RatFunction;
import utils.RandomUtils;

public class ChangeNumFeatures {

    public static ClDataset addFeatures(ClDataset dataset, Random random, int newNumFeatures) {
        int oldNumFeatures = dataset.numFeatures;

        int numObjects = dataset.numObjects;

        double[][] values = new double[numObjects][newNumFeatures];

        for (int oid = 0; oid < numObjects; oid++) {
            for (int fid = 0; fid < oldNumFeatures; fid++) {
                values[oid][fid] = dataset.value(oid, fid);
            }
        }

        for (int fid = oldNumFeatures; fid < newNumFeatures; fid++) {
            int d = random.nextInt(3) + 1;
            RatFunction function = RatFunction.random(dataset, random, d);

            for (int oid = 0; oid < numObjects; oid++) {
                values[oid][fid] = function.applyAsDouble(oid);
            }
        }

        return dataset.changeValues(true, values);
    }

    public static ClDataset apply(ClDataset dataset, Random random, int newNumFeatures) {
        if (dataset.numFeatures == newNumFeatures) {
            return dataset;
        }

        if (dataset.numFeatures < newNumFeatures) {
            return addFeatures(dataset, random, newNumFeatures);
        } else {
            return removeFeatures(dataset, random, newNumFeatures);
        }

    }

    public static ClDataset removeFeatures(ClDataset dataset, Random random, int newNumFeatures) {
        int oldNumFeatures = dataset.numFeatures;
        int numObjects = dataset.numObjects;

        double[][] values = new double[numObjects][newNumFeatures];

        boolean[] selection = RandomUtils.randomSelection(oldNumFeatures, newNumFeatures, random);

        for (int oid = 0; oid < numObjects; oid++) {
            for (int nfid = 0, fid = 0; fid < oldNumFeatures; fid++) {
                if (selection[fid]) {
                    values[oid][nfid++] = dataset.value(oid, fid);
                }
            }
        }
        return dataset.changeValues(true, values);
    }

}
