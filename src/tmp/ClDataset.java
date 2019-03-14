package tmp;

import clusterization.Dataset;
import clusterization.MetaFeaturesExtractor;
import utils.CategoryMapper;

public class ClDataset extends Dataset {

    public final int numClasses;
    final int[] labels;

    public ClDataset(double[][] data, int[] labels, MetaFeaturesExtractor extractor, boolean normalize) {
        super(data, extractor, normalize);

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

    public Dataset[] splitByLabels(boolean normalize) {
        int[] p = new int[numClasses];
        for (int oid = 0; oid < numObjects; oid++) {
            ++p[labels[oid]];
        }
        double[][][] data = new double[numClasses][][];

        double[][] joint = data();

        for (int label = 0; label < numClasses; label++) {
            data[label] = new double[p[label]][];
            p[label] = 0;
        }

        for (int oid = 0; oid < numObjects; oid++) {
            int label = labels[oid];
            data[label][p[label]++] = joint[oid];
        }

        Dataset[] datasets = new Dataset[numClasses];
        for (int label = 0; label < numClasses; label++) {
            datasets[label] = new Dataset(data[label], extractor, normalize);
        }

        return datasets;
    }

}
