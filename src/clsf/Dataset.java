package clsf;

import java.util.Arrays;

import utils.ArrayUtils;
import utils.CategoryMapper;

public class Dataset {

    public static boolean defaultNormLabels = true;
    public static boolean defaultNormValues = true;

    private final int hashCode;

    public final String name;
    public final int numClasses;
    public final int numFeatures;
    public final int numObjects;

    public final double[][] data;
    public final double[] min;
    public final double[] max;

    public final int[] labels;
    public final int[] classDistribution;
    public final int[][] indices;
    public final double[][][] dataPerClass;

    public Dataset(String name, boolean normValues, double[][] data, boolean normLabels, int[] labels) {
        this.name = name;
        this.data = data;
        this.numObjects = data.length;
        if (numObjects <= 1) {
            throw new IllegalArgumentException("data.length <= 1");
        }
        {
            int numFeatures = -1;
            for (double[] object : data) {
                if (numFeatures == -1) {
                    numFeatures = object.length;
                }
                if (object.length != numFeatures) {
                    throw new IllegalArgumentException("All objects length must be equal");
                }

                for (double value : object) {
                    if (!Double.isFinite(value)) {
                        throw new IllegalArgumentException("All values must be finite, value = " + value);
                    }
                }
            }
            if (numFeatures <= 0) {
                throw new IllegalArgumentException("Number of features must be positive");
            }
            this.numFeatures = numFeatures;
        }
        if (normValues) {
            for (int fid = 0; fid < numFeatures; fid++) {
                double mean = 0;
                for (int oid = 0; oid < numObjects; oid++) {
                    mean += data[oid][fid];
                }
                mean /= numObjects;
                for (int oid = 0; oid < numObjects; oid++) {
                    data[oid][fid] -= mean;
                }

                double var = 0;
                double asymm = 0;
                for (int oid = 0; oid < numObjects; oid++) {
                    double value = data[oid][fid];
                    double value2 = value * value;
                    var += value2;
                    asymm += value * value2;
                }
                var /= numObjects;
                if (var > 1e-6) {
                    double scale = Math.sqrt(1 / var);
                    if (asymm < 0) {
                        scale *= -1;
                    }
                    for (int oid = 0; oid < numObjects; oid++) {
                        data[oid][fid] *= scale;
                    }
                }
            }
        }

        if (labels.length != numObjects) {
            throw new IllegalArgumentException("Number of objects must be equal to labels length");
        }
        this.labels = labels;

        if (normLabels) {
            CategoryMapper mapper = new CategoryMapper(labels.clone());
            this.numClasses = mapper.range();
            for (int oid = 0; oid < numObjects; oid++) {
                this.labels[oid] = mapper.applyAsInt(labels[oid]);
            }
        } else {
            this.numClasses = ArrayUtils.max(labels);
        }
        this.hashCode = Arrays.deepHashCode(data) ^ Arrays.hashCode(this.labels);

        {
            this.classDistribution = new int[numClasses];
            for (int oid = 0; oid < numObjects; oid++) {
                ++classDistribution[labels[oid]];
            }
        }
        {
            this.indices = new int[numClasses][];
            int[] s = classDistribution.clone();
            for (int label = 0; label < numClasses; label++) {
                indices[label] = new int[s[label]];
                s[label] = 0;
            }
            for (int oid = 0; oid < numObjects; oid++) {
                int c = labels[oid];
                indices[c][s[c]++] = oid;
            }
        }

        {
            this.min = new double[numFeatures];
            this.max = new double[numFeatures];
            Arrays.fill(min, Double.POSITIVE_INFINITY);
            Arrays.fill(max, Double.NEGATIVE_INFINITY);

            for (int oid = 0; oid < numObjects; oid++) {
                for (int fid = 0; fid < numFeatures; fid++) {
                    min[fid] = Math.min(min[fid], data[oid][fid]);
                    max[fid] = Math.max(max[fid], data[oid][fid]);
                }
            }
        }

        {
            this.dataPerClass = new double[numClasses][][];
            int[] s = classDistribution.clone();
            for (int label = 0; label < numClasses; label++) {
                dataPerClass[label] = new double[s[label]][];
                s[label] = 0;
            }
            for (int oid = 0; oid < numObjects; oid++) {
                int c = labels[oid];
                dataPerClass[c][s[c]++] = data[oid];
            }
        }

    }

    public Dataset changeLabels(boolean normLabels, int[] labels) {
        return new Dataset(name, false, data, normLabels, labels);
    }

    public Dataset changeValues(boolean normValues, double[][] data) {
        return new Dataset(name, normValues, data, false, labels);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
