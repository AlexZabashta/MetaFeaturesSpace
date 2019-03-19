package clsf;

import utils.ArrayUtils;
import utils.CategoryMapper;

public class ClDataset extends Dataset {

    public final int numClasses;
    final int[] labels;

    public final String name;

    public ClDataset(String name, boolean normalize, double[][] data, int[] labels) {
        super(normalize, data);
        this.name = name;
        if (labels.length != numObjects) {
            throw new IllegalArgumentException("Number of objects must be equal to labels length");
        }
        this.labels = labels;
        CategoryMapper mapper = new CategoryMapper(labels.clone());
        this.numClasses = mapper.range();
        for (int oid = 0; oid < numObjects; oid++) {
            this.labels[oid] = mapper.applyAsInt(labels[oid]);
        }
    }

    public ClDataset(String name, boolean normalize, double[][]... data) {
        super(normalize, ArrayUtils.merge(data));
        this.name = name;
        this.labels = new int[numObjects];
        this.numClasses = data.length;

        for (int oid = 0, label = 0; label < numClasses; label++) {
            for (int i = 0; i < data.length; i++, oid++) {
                labels[oid] = label;
            }
        }
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

}
