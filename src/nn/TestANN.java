package nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import nn.act.Tanh;
import nn.fld.Sum;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.Standardize;

public class TestANN {
    public static void main(String[] args) throws IOException {

        Random random = new Random();
        for (File file : new File("data").listFiles()) {
            // for (int fff = 0; fff < 1; fff++) {
            // File file = new File("data\\1011.arff");
            Instances dataset = new Instances(new FileReader(file));
            dataset.setClassIndex(dataset.numAttributes() - 1);

            if (dataset.numAttributes() > 30 || dataset.numInstances() > 1000) {
                continue;
            }

            if (dataset.classAttribute().isNominal()) {
                if (dataset.numClasses() < 25) {
                    try {

                        int rep = 100;

                        double lr = 0.3;

                        Filter filter = new NominalToBinary();
                        filter.setInputFormat(dataset);
                        dataset = Filter.useFilter(dataset, filter);
                        if (dataset.numAttributes() > 30 || dataset.numInstances() > 1000) {
                            continue;
                        }

                        Filter std = new Standardize();
                        std.setInputFormat(dataset);
                        dataset = Filter.useFilter(dataset, std);

                        int n = dataset.numAttributes() - 1;
                        int m = dataset.numClasses();

                        int h1 = (int) Math.round(Math.pow(1.0 * n * n * m, 1.0 / 3.0));
                        int h2 = (int) Math.round(Math.pow(1.0 * n * m * m, 1.0 / 3.0));

                        Evaluation evaluation = new Evaluation(dataset);
                        MultilayerPerceptron perceptron = new MultilayerPerceptron();

                        perceptron.setLearningRate(lr);
                        perceptron.setTrainingTime(rep);
                        perceptron.setMomentum(0);
                        perceptron.setHiddenLayers(String.format("%d,%d", n, h1, h2, m));

                        evaluation.crossValidateModel(perceptron, dataset, 5, random);

                        double[][] wcm = evaluation.confusionMatrix();

                        System.out.println(file);
                        for (double[] row : wcm) {
                            for (double val : row) {
                                System.out.printf("%.0f ", val);
                            }
                            System.out.println();
                        }
                        System.out.println();

                        Collections.shuffle(dataset, random);

                        List<BPLearn> neurons = new ArrayList<>();
                        int numWeights = 0;

                        double[] w = new double[4096];
                        for (int i = 0; i < w.length; i++) {
                            w[i] = random.nextGaussian() / 10;
                        }

                        // System.out.println(Arrays.toString(w));

                        w = Arrays.copyOf(w, numWeights);
                        NeuralNetwork network = new NeuralNetwork(n, m, numWeights, neurons.toArray(new Neuron[0]));

                        for (int iter = 0; iter < rep; iter++) {
                            for (Instance instance : dataset) {
                                double[] input = new double[n];

                                for (int i = 0; i < n; i++) {
                                    input[i] = instance.value(i);
                                }

                                double[] output = new double[m];
                                Arrays.fill(output, -0.9);
                                output[(int) instance.classValue()] *= -1;

                                network.update(input, output, w, lr);
                            }
                        }

                        double[][] cm = new double[m][m];
                        for (Instance instance : dataset) {
                            double[] input = new double[n];

                            for (int i = 0; i < n; i++) {
                                input[i] = instance.value(i);
                            }

                            double[] output = network.get(input, w);

                            int c = 0;

                            for (int i = 0; i < m; i++) {
                                if (output[i] > output[c]) {
                                    c = i;
                                }
                            }

                            cm[(int) instance.classValue()][c] += 1;
                            // System.out.println(Arrays.toString(output));
                        }
                        //

                        for (double[] row : cm) {
                            for (double val : row) {
                                System.out.printf("%.0f ", val);
                            }
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println();
                        System.out.flush();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
