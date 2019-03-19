package clsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import utils.ArraysUtils;
import utils.CategoryMapper;
import utils.StatUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;

public class aDataset {
    private final int[][] catValues;
    private final double[][] ratValues;
    private final int[] categorySizes;
    private final int numCatAttr, numRatAttr, numObjects;
    // protected final int[] classSizes;
    // private final int hashCode = 0;

    private final double[] min, max;

    public int[][] catValues() {
        return ArraysUtils.copy(catValues);
    }

    public double[][] ratValues() {
        return ArraysUtils.copy(ratValues);
    }

    public aDataset(int numObjects, int numCatAttr, int[][] catValues, int numRatAttr, double[][] ratValues) {
        if (numObjects <= 0) {
            throw new IllegalArgumentException("numObjects <= 0");
        }

        if (numCatAttr < 0) {
            throw new IllegalArgumentException("numCatAttr < 0");
        }
        if (numRatAttr < 0) {
            throw new IllegalArgumentException("numRatAttr < 0");
        }

        this.numObjects = numObjects;
        this.numCatAttr = numCatAttr;
        this.categorySizes = new int[numCatAttr + 1];
        this.numRatAttr = numRatAttr;

        if (numObjects <= 0) {
            throw new IllegalArgumentException("numObjects <= 0");
        }

        this.catValues = catValues;
        for (int cid = 0; cid <= numCatAttr; cid++) {
            int[] values = new int[numObjects];

            for (int oid = 0; oid < numObjects; oid++) {
                values[oid] = catValues[oid][cid];
            }
            CategoryMapper mapper = new CategoryMapper(values);
            categorySizes[cid] = mapper.range();

            for (int oid = 0; oid < numObjects; oid++) {
                catValues[oid][cid] = mapper.applyAsInt(catValues[oid][cid]);
            }
        }

        // if (categorySizes[numCatAttr] == 1 && numObjects > 1) {
        // categorySizes[numCatAttr] = 2;
        // for (int i = 0; i < numObjects; i++) {
        // catValues[i][numCatAttr] = i % 2;
        // }
        // }

        this.ratValues = ratValues;

        min = new double[numRatAttr];
        max = new double[numRatAttr];

        for (int rid = 0; rid < numRatAttr; rid++) {
            for (int oid = 0; oid < numObjects; oid++) {
                double value = ratValues[oid][rid];
                if (Double.isInfinite(value)) {
                    throw new IllegalArgumentException("ratValues[" + oid + "][" + rid + "] is Infinite");
                }

                if (Double.isNaN(value)) {
                    throw new IllegalArgumentException("ratValues[" + oid + "][" + rid + "] is NaN");
                }
            }

            double sum1 = 0, sum2 = 0;
            for (int oid = 0; oid < numObjects; oid++) {
                double value = ratValues[oid][rid];
                sum1 += value;
                sum2 += value * value;
            }

            double mean = StatUtils.mean(numObjects, sum1);
            double std = StatUtils.std(numObjects, mean, sum2);

            if (std < 1e-9) {
                std = 1;
            }

            min[rid] = Double.MAX_VALUE;
            max[rid] = Double.MIN_VALUE;

            for (int oid = 0; oid < numObjects; oid++) {
                double value = (ratValues[oid][rid] - mean) / std;
                ratValues[oid][rid] = value;
                min[rid] = Math.min(min[rid], value);
                max[rid] = Math.max(max[rid], value);
            }
        }
    }

    public class Item {
        final int objectIndex;

        public Item(int objectIndex) {
            this.objectIndex = objectIndex;
        }

        public int catValue(int catAttrIndex) {
            checkCatAttrIndex(catAttrIndex);
            return catValues[objectIndex][catAttrIndex];
        }

        public double ratValue(int ratAttrIndex) {
            checkRatAttrIndex(ratAttrIndex);
            return ratValues[objectIndex][ratAttrIndex];
        }

        public int classValue() {
            return catValues[objectIndex][numCatAttr];
        }
    }

    public Item item(int objectIndex) {
        checkObjectIndex(objectIndex);
        return new Item(objectIndex);
    }

    public int numAttr() {
        return numCatAttr() + numRatAttr();
    }

    protected void checkObjectIndex(int objectIndex) {
        if (objectIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("objectIndex=%d must be non negative", objectIndex));
        }
        if (objectIndex >= numObjects) {
            throw new IndexOutOfBoundsException(String.format("objectIndex=%d must be less than numObjects=%d", objectIndex, numObjects));
        }
    }

    protected void checkRatAttrIndex(int ratAttrIndex) {
        if (ratAttrIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("ratAttrIndex=%d must be non negative", ratAttrIndex));
        }
        if (ratAttrIndex >= numRatAttr) {
            throw new IndexOutOfBoundsException(String.format("ratAttrIndex=%d must be less than numRatAttr=%d", ratAttrIndex, numRatAttr));
        }
    }

    protected void checkCatAttrIndex(int catAttrIndex) {
        if (catAttrIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("catAttrIndex=%d must be non negative", catAttrIndex));
        }
        if (catAttrIndex >= numCatAttr) {
            throw new IndexOutOfBoundsException(String.format("catAttrIndex=%d must be less than numCatAttr=%d", catAttrIndex, numCatAttr));
        }
    }

    protected void checkClassIndex(int classIndex) {
        if (classIndex < 0) {
            throw new IndexOutOfBoundsException(String.format("classIndex=%d must be non negative", classIndex));
        }
        if (classIndex >= numClasses()) {
            throw new IndexOutOfBoundsException(String.format("classIndex=%d must be less than numClasses=%d", classIndex, numClasses()));
        }
    }

    public double min(int ratAttrIndex) {
        checkRatAttrIndex(ratAttrIndex);
        return min[ratAttrIndex];
    }

    public double max(int ratAttrIndex) {
        checkRatAttrIndex(ratAttrIndex);
        return max[ratAttrIndex];
    }

    public int numCatAttr() {
        return numCatAttr;
    }

    public int numRatAttr() {
        return numRatAttr;
    }

    public int numClasses() {
        return categorySizes[numCatAttr];
    }

    public int numObjects() {
        return numObjects;
    }

    // public int classSize(int classIndex) {
    // checkClassIndex(classIndex);
    // return classSizes[classIndex];
    // }

    public int categorySize(int catAttrIndex) {
        checkCatAttrIndex(catAttrIndex);
        return categorySize(catAttrIndex);
    }

    public int catValue(int objectIndex, int catAttrIndex) {
        checkObjectIndex(objectIndex);
        checkCatAttrIndex(catAttrIndex);
        return catValues[objectIndex][catAttrIndex];
    }

    public double ratValue(int objectIndex, int ratAttrIndex) {
        checkObjectIndex(objectIndex);
        checkRatAttrIndex(ratAttrIndex);
        return ratValues[objectIndex][ratAttrIndex];
    }

    public int classValue(int objectIndex) {
        checkObjectIndex(objectIndex);
        return catValues[objectIndex][numCatAttr];
    }

}
