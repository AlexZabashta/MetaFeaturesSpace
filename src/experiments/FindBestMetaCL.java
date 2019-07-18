package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import clsf.Dataset;
import mfextraction.CMFExtractor;
import mfextraction.KNNLandMark;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class FindBestMetaCL {
    static int get(String name, String[] args, int index, int defaultValue) {
        try {
            int value = Integer.parseInt(args[index]);
            System.out.println(name + " = " + value);
            return value;
        } catch (RuntimeException e) {
            System.out.println(name + " = default = " + defaultValue);
            return defaultValue;
        }
    }

    static List<Classifier> listClassifiers() {
        List<Classifier> list = new ArrayList<>();

        // for (int p = 1; p <= 4; p *= 2) {
        // for (int k = 3; k < 10; k++) {
        // IBk iBk = new IBk(k);
        // iBk.setDistanceWeighting(new SelectedTag(p, IBk.TAGS_WEIGHTING));
        // list.add(iBk);
        // }
        // }
        {
            IBk iBk = new IBk(4);
            iBk.setDistanceWeighting(new SelectedTag(2, IBk.TAGS_WEIGHTING));
            list.add(iBk);
        }

        {
            SMOreg svm = new SMOreg();
            svm.setC(1.0);
            RBFKernel kernel = new RBFKernel();
            kernel.setGamma(1.0);
            svm.setKernel(kernel);
            list.add(svm);
        }

        {

            for (double m = 0.1; m <= 1; m += 0.1) {

            }

            MultilayerPerceptron mlp = new MultilayerPerceptron();

            mlp.setNormalizeNumericClass(true);
            mlp.setNormalizeAttributes(true);
            list.add(mlp);

        }

        return list;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("args = " + Arrays.toString(args));
        final int limit = get("limit", args, 0, 300);
        final int threads = get("threads", args, 1, 5);
        final int repeats = get("repeats", args, 2, 10);

        double[][] metaData = new double[2048][];

        List<Dataset> datasets = DataReader.readData("data.csv", new File("data"));
        final int numData = datasets.size();

        CMFExtractor extractor = new CMFExtractor();
        int numMF = extractor.length();

        for (int i = 0; i < numData; i++) {
            metaData[i] = extractor.apply(datasets.get(i));
        }

        Collections.sort(datasets, Comparator.comparing(d -> d.name));
        Collections.shuffle(datasets, new Random(42));
        ToDoubleFunction<Dataset> knnScore = new KNNLandMark();

        for (Classifier classifier : listClassifiers()) {
            double mse = 0;
            long time = 0;

            for (int repeat = 0; repeat < repeats; repeat++) {
                ArrayList<Attribute> attributes = new ArrayList<>();
                for (int i = 0; i < numMF; i++) {
                    attributes.add(new Attribute("mf" + i));
                }
                attributes.add(new Attribute("knnScore"));
                Instances header = new Instances("meta", attributes, 0);
                header.setClassIndex(numMF);

                Instances train = new Instances(header);
                Instances test = new Instances(header);

                Random random = new Random(repeat + 42);
                for (Dataset dataset : datasets) {
                    Instance instance = new DenseInstance(numMF + 1);
                    instance.setDataset(header);

                    double[] mf = extractor.apply(dataset);
                    for (int i = 0; i < numMF; i++) {
                        instance.setValue(i, mf[i]);
                    }
                    instance.setClassValue(knnScore.applyAsDouble(dataset));

                    if (random.nextInt(3) == 0) {
                        train.add(instance);
                    } else {
                        test.add(instance);
                    }
                }

                double sum = 0;
                long start = System.currentTimeMillis();

                try {
                    classifier.buildClassifier(train);

                    for (Instance instance : test) {
                        double pr = classifier.classifyInstance(instance);
                        double re = instance.classValue();
                        double diff = pr - re;
                        sum += diff * diff;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                long finish = System.currentTimeMillis();
                time += finish - start;
                mse += sum / test.size();
            }

            // System.out.println(classifier);
            System.out.print(time);
            System.out.print(' ');
            System.out.println(Math.sqrt(mse / repeats));

        }

    }
}
