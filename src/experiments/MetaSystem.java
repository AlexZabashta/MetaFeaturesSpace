package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import clsf.Dataset;
import utils.StatUtils;
import utils.ToDoubleArrayFunction;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MetaSystem {
    public final Instances instancesFormat;
    public final ToDoubleArrayFunction<Dataset> extractor;
    public final REPTree[] trees;

    public final int size = 30;

    Instance convert(Dataset dataset, double classValue) {
        Instance instance = new DenseInstance(instancesFormat.numAttributes());
        instance.setDataset(instancesFormat);

        double[] x = extractor.apply(dataset);

        for (int i = 0; i < x.length; i++) {
            instance.setValue(i, x[i]);
        }

        if (Double.isFinite(classValue)) {
            instance.setClassValue(classValue);
        }

        return instance;
    }

    public MetaSystem(List<Dataset> train, ToDoubleArrayFunction<Dataset> extractor, ToDoubleFunction<Dataset> target) {
        Random random = new Random();
        int numMetaFeatures = extractor.length();
        ArrayList<Attribute> attributes = new ArrayList<>(numMetaFeatures + 1);
        for (int i = 0; i < numMetaFeatures; i++) {
            attributes.add(new Attribute("mf" + i));
        }
        attributes.add(new Attribute("target"));
        instancesFormat = new Instances("meta", attributes, 0);
        instancesFormat.setClassIndex(numMetaFeatures);

        this.extractor = extractor;
        this.trees = new REPTree[size];
        for (int i = 0; i < size; i++) {
            trees[i] = new REPTree();
            trees[i].setNoPruning(true);
            trees[i].setMaxDepth(10);
        }

        Instances[] instances = new Instances[size];
        for (int i = 0; i < size; i++) {
            instances[i] = new Instances(instancesFormat);
        }

        int id = 0;
        Collections.shuffle(train, random);
        for (Dataset dataset : train) {
            Instance instance = convert(dataset, target.applyAsDouble(dataset));
            for (int i = 0; i < size; i++) {
                if (i == id || random.nextInt(5) == 0) {
                    instances[i].add(instance);
                }
            }
            id = (id + 1) % size;
        }

        for (int i = 0; i < size; i++) {
            try {
                trees[i].buildClassifier(instances[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double[] classifyDataset(Dataset dataset) {
        Instance instance = convert(dataset, Double.NaN);
        double[] result = new double[size];
        int p = 0;

        for (int i = 0; i < size; i++) {
            try {
                result[p] = trees[i].classifyInstance(instance);
                ++p;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Arrays.copyOf(result, p);
    }

    public double rmse(List<Dataset> test, ToDoubleFunction<Dataset> target) {
        double sumSquareErrors = 0;

        for (Dataset dataset : test) {
            double real = target.applyAsDouble(dataset);

            double mean = StatUtils.mean(classifyDataset(dataset));
            double diff = real - mean;
            sumSquareErrors += diff * diff;

        }
        return Math.sqrt(sumSquareErrors / test.size());
    }

}
