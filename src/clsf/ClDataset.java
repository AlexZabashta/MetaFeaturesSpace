package clsf;

import java.util.Arrays;

import utils.ArrayUtils;
import utils.CategoryMapper;

public class ClDataset extends Dataset {

    public static boolean defaultNormValues = Dataset.defaultNormalize;
    public static boolean defaultNormLabels = true;

    private final int hashCode;

    public final int[] labels;
    public final String name;

    public final int numClasses;

    public final double[][][] subData;

    public ClDataset changeValues(boolean normValues, double[][] data) {
        return new ClDataset(name, normValues, data, false, labels);
    }

    public ClDataset changeLabels(boolean normLabels, int[] labels) {
        return new ClDataset(name, false, data, normLabels, labels);
    }

    public ClDataset(String name, boolean normValues, double[][] data, boolean normLabels, int[] labels) {
        super(normValues, data);
        this.name = name;
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

        this.subData = new double[numClasses][][];
        // TODO fill subData
        this.hashCode = super.hashCode() ^ Arrays.hashCode(this.labels);
    }

    public int[] classDistribution() {
        int[] distribution = new int[numClasses];
        for (int oid = 0; oid < numObjects; oid++) {
            ++distribution[labels[oid]];
        }
        return distribution;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public int[][] indices() {
        int[] s = classDistribution().clone();

        int[][] indices = new int[numClasses][];

        for (int label = 0; label < numClasses; label++) {
            indices[label] = new int[s[label]];
            s[label] = 0;
        }

        for (int oid = 0; oid < numObjects; oid++) {
            int c = labels[oid];
            indices[c][s[c]++] = oid;
        }

        return indices;

    }

    public double max(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    public double min(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    public double[] max() {
        // TODO Auto-generated method stub
        return new double[0];
    }

    public double[] min() {
        // TODO Auto-generated method stub
        return new double[0];
    }

    public Dataset[] splitByLabels(boolean normalize) {
        int[] p = new int[numClasses];
        for (int oid = 0; oid < numObjects; oid++) {
            ++p[labels[oid]];
        }
        double[][][] dataPerClass = new double[numClasses][][];
        double[][] data = this.data;
        for (int label = 0; label < numClasses; label++) {
            dataPerClass[label] = new double[p[label]][];
            p[label] = 0;
        }

        for (int oid = 0; oid < numObjects; oid++) {
            int label = labels[oid];
            dataPerClass[label][p[label]++] = data[oid];
        }

        Dataset[] datasets = new Dataset[numClasses];
        for (int label = 0; label < numClasses; label++) {
            datasets[label] = new Dataset(normalize, dataPerClass[label]);
        }

        return datasets;
    }

    public int classValue(int oid) {
        return labels[oid];
    }

    public double value(int oid, int fid) {
        return data[oid][fid];
    }

}
