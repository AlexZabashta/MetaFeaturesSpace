package clsf.gen_op;

import java.util.Random;

import clsf.ClDataset;
import clsf.gen_op.fun.rat.RatFunction;
import utils.RandomUtils;

public class ChangeNumAttributes {

    public static ClDataset addFeatures(ClDataset dataset, Random random, int newNumFeatures) {
        int oldNumFeatures = dataset.numFeatures;

        int numObjects = dataset.numObjects;

        double[][] values = new double[numObjects][newNumFeatures];

        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < oldNumFeatures; j++) {
                values[i][j] = dataset.value(i, j);
            }
        }

        for (int j = oldNumFeatures; j < newNumFeatures; j++) {
            int d = random.nextInt(3) + 1;
            RatFunction function = RatFunction.random(dataset, random, d);

            for (int i = 0; i < numObjects; i++) {
                values[i][j] = function.applyAsDouble(dataset.item(i));
            }
        }

        return dataset.changeValues(true, values);
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
