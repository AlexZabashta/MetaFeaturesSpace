package clsf.direct.gen_op;

import java.util.Arrays;
import java.util.Random;

import clsf.ClDataset;
import utils.Permutation;
import utils.RandomUtils;

public class ChangeNumObjects {

    public static ClDataset apply(ClDataset dataset, Random random) {
        int numObjects = dataset.numObjects;
        return apply(dataset, random, random.nextInt(numObjects * 2) + 2);
    }

    public static ClDataset addObjects(ClDataset dataset, Random random, int newNumObjects) {
        int numFeatures = dataset.numFeatures;
        int oldNumObjects = dataset.numObjects;

        double[][] values = new double[newNumObjects][];
        int[] labels = Arrays.copyOf(dataset.labels, newNumObjects);

        for (int oid = 0; oid < oldNumObjects; oid++) {
            values[oid] = dataset.data[oid].clone();
        }

        int[][] indices = dataset.indices();

        for (int oid = oldNumObjects; oid < newNumObjects; oid++) {
            values[oid] = new double[numFeatures];
            int label = labels[random.nextInt(oldNumObjects)];
            labels[oid] = label;
            int[] p = Permutation.random(numFeatures, random);
            int[] subsetIndices = indices[label];

            int offset = 0;
            while (offset < numFeatures) {
                int srcObjId = subsetIndices[random.nextInt(subsetIndices.length)];
                int len = random.nextInt(numFeatures - offset) + 1;

                for (int k = 0; k < len; k++) {
                    int fid = p[offset + k];
                    values[oid][fid] = values[srcObjId][fid];
                }

                offset += len;
            }
        }

        return new ClDataset(dataset.name, true, values, false, labels);
    }

    public static ClDataset removeObjects(ClDataset dataset, Random random, int newNumObjects) {
        int numFeatures = dataset.numFeatures;
        int oldNumObjects = dataset.numObjects;

        boolean[] selection = RandomUtils.randomSelection(oldNumObjects, newNumObjects, random);

        double[][] values = new double[newNumObjects][numFeatures];
        int[] labels = new int[newNumObjects];

        for (int noid = 0, oid = 0; oid < oldNumObjects; oid++) {
            if (selection[oid]) {
                values[noid] = dataset.data[oid].clone();
                labels[noid] = dataset.labels[oid];
                ++noid;
            }
        }

        return new ClDataset(dataset.name, true, values, true, labels);
    }

    public static ClDataset apply(ClDataset dataset, Random random, int newNumObjects) {
        int oldNumObjects = dataset.numObjects;

        if (oldNumObjects == newNumObjects) {
            return dataset;
        }

        if (oldNumObjects < newNumObjects) {
            return addObjects(dataset, random, newNumObjects);
        } else {
            return removeObjects(dataset, random, newNumObjects);
        }
    }

}
