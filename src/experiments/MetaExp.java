package experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

import clsf.CMFExtractor;
import clsf.Dataset;
import clsf.WekaConverter;
import clsf.ndse.gen_op.ChangeNumClasses;
import clsf.ndse.gen_op.ChangeNumFeatures;
import clsf.ndse.gen_op.ChangeNumObjects;
import clsf.ndse.gen_op.DatasetCrossover;
import clsf.ndse.gen_op.DatasetMutation;
import clsf.vect.Converter;
import clsf.vect.DirectConverter;
import clsf.vect.GMMConverter;
import utils.ArrayUtils;
import utils.BlockingThreadPoolExecutor;
import utils.EndSearch;
import utils.FolderUtils;
import utils.Limited;
import utils.MahalanobisDistance;
import utils.MatrixUtils;
import utils.MultiObjectiveError;
import utils.SingleObjectiveError;
import utils.StatUtils;
import utils.ToDoubleArrayFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class MetaExp {

    public static final int MAX_FEATURES = 16;
    public static final int MAX_OBJECTS = 256;
    public static final int MAX_CLASSES = 5;

    public static List<Dataset> readData(String description, File folder) throws IOException {

        Map<String, String> target = new HashMap<>();

        try (CSVParser parser = new CSVParser(new FileReader(description), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : parser) {
                target.put(record.get("id") + ".arff", record.get("target"));
            }
        }

        List<Dataset> datasets = new ArrayList<>();

        for (File file : folder.listFiles()) {

            String className = target.get(file.getName());
            if (className == null) {
                continue;
            }
            className = className.toLowerCase();

            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);

                for (int i = 0; i < instances.numAttributes(); i++) {
                    if (instances.attribute(i).name().toLowerCase().equals(className)) {
                        instances.setClassIndex(i);
                    }
                }

                if (instances.classIndex() < 0) {
                    continue;
                }

                if (instances.numClasses() < 2 || instances.numInstances() < 20 || instances.numAttributes() < 2) {
                    continue;
                }

                if (instances.numAttributes() > MAX_FEATURES * 10 || instances.numInstances() > MAX_OBJECTS * 10) {
                    continue;
                }

                Filter rmv = new ReplaceMissingValues();
                rmv.setInputFormat(instances);
                instances = Filter.useFilter(instances, rmv);

                Filter ntb = new NominalToBinary();
                ntb.setInputFormat(instances);
                instances = Filter.useFilter(instances, ntb);

                if (instances.numAttributes() > MAX_FEATURES * 10 || instances.numInstances() > MAX_OBJECTS * 10) {
                    continue;
                }

                int numObjects = instances.numInstances();
                int numFeatures = instances.numAttributes() - 1;

                double[][] data = new double[numObjects][numFeatures];
                int[] labels = new int[numObjects];

                for (int oid = 0; oid < numObjects; oid++) {
                    Instance instance = instances.get(oid);
                    for (int fid = 0, aid = 0; aid < instances.numAttributes(); aid++) {
                        if (aid == instances.classIndex()) {
                            continue;
                        }
                        data[oid][fid++] = instance.value(aid);
                    }

                    labels[oid] = (int) instance.classValue();
                }

                Dataset dataset = new Dataset(file.getName(), Dataset.defaultNormValues, data, Dataset.defaultNormLabels, labels);

                if (dataset.numObjects > MAX_OBJECTS) {
                    dataset = ChangeNumObjects.apply(dataset, new Random(dataset.hashCode()), MAX_OBJECTS);
                }

                if (dataset.numFeatures > MAX_FEATURES) {
                    dataset = ChangeNumFeatures.apply(dataset, new Random(dataset.hashCode()), MAX_FEATURES);
                }

                if (dataset.numClasses > MAX_CLASSES) {
                    dataset = ChangeNumClasses.apply(dataset, new Random(dataset.hashCode()), MAX_CLASSES);
                }

                boolean cnt = false;
                for (int sub : dataset.classDistribution) {
                    cnt |= sub < 5;
                }
                if (cnt) {
                    continue;
                }

                datasets.add(dataset);

                System.out.println(file.getName());
                // System.out.println(Arrays.toString(mf));
                System.out.flush();

            } catch (Exception e) {
                System.err.println(file.getName());
                e.printStackTrace();
            }
        }

        return datasets;
    }

    public static void main(String[] args) throws IOException {

        final int limit = 3000;
        final int cores = 10;

        System.out.println("cores = " + cores);
        System.out.println("limit = " + limit);

        double[][] metaData = new double[1024][];

        List<Dataset> datasets = readData("data.csv", new File("data"));
        final int numData = datasets.size();

        CMFExtractor extractor = new CMFExtractor();
        int numMF = extractor.length();

        for (int i = 0; i < numData; i++) {
            metaData[i] = extractor.apply(datasets.get(i));
        }

        double[][] cov = StatUtils.covarianceMatrix(numData, numMF, metaData);
        ArrayUtils.print(cov);
        double[][] invCov = MatrixUtils.inv(numMF, cov);
        ArrayUtils.print(invCov);

        double[] invSigma = new double[numMF];

        for (int i = 0; i < numMF; i++) {
            invSigma[i] = invCov[i][i];
        }

        MahalanobisDistance distance = new MahalanobisDistance(numMF, invCov);

        String res = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));

        ExecutorService executor = new BlockingThreadPoolExecutor(cores, false);

        int currentExperimentId = 0;

        DatasetCrossover crossover = new DatasetCrossover();
        int minNumObjects = Integer.MAX_VALUE;
        int maxNumObjects = 0;
        int minNumFeatures = Integer.MAX_VALUE;
        int maxNumFeatures = 0;
        int minNumClasses = Integer.MAX_VALUE;
        int maxNumClasses = 0;

        Set<Integer> numObjectsDistributionList = new TreeSet<>();
        Set<Integer> numFeaturesDistributionList = new TreeSet<>();
        Set<Integer> numClassesDistributionList = new TreeSet<>();

        for (Dataset dataset : datasets) {
            minNumObjects = Math.min(minNumObjects, dataset.numObjects);
            maxNumObjects = Math.max(maxNumObjects, dataset.numObjects);

            minNumFeatures = Math.min(minNumFeatures, dataset.numFeatures);
            maxNumFeatures = Math.max(maxNumFeatures, dataset.numFeatures);

            minNumClasses = Math.min(minNumClasses, dataset.numClasses);
            maxNumClasses = Math.max(maxNumClasses, dataset.numClasses);

            for (int sub : dataset.classDistribution) {
                numObjectsDistributionList.add(sub);
            }
            numFeaturesDistributionList.add(dataset.numFeatures);
            numClassesDistributionList.add(dataset.numClasses);
        }

        int[] numObjectsDistribution = numObjectsDistributionList.stream().mapToInt(x -> x.intValue()).toArray();
        int[] numFeaturesDistribution = numFeaturesDistributionList.stream().mapToInt(x -> x.intValue()).toArray();
        int[] numClassesDistribution = numClassesDistributionList.stream().mapToInt(x -> x.intValue()).toArray();

        System.out.println(Arrays.toString(numObjectsDistribution));
        System.out.println(Arrays.toString(numFeaturesDistribution));
        System.out.println(Arrays.toString(numClassesDistribution));

        DatasetMutation mutation = new DatasetMutation(minNumObjects, maxNumObjects, minNumFeatures, maxNumFeatures, minNumClasses, maxNumClasses);
        Converter direct = new DirectConverter(numObjectsDistribution, numFeaturesDistribution, numClassesDistribution);
        Converter gmmcon = new GMMConverter(numObjectsDistribution, numFeaturesDistribution, numClassesDistribution);

        Collections.shuffle(datasets, new Random(42));

        for (Dataset targetDataset : datasets) {
            final String targetName = targetDataset.name;

            final double[] target = extractor.apply(targetDataset);

            synchronized (res) {
                try (PrintWriter writer = new PrintWriter(new File(res + targetName + ".txt"))) {
                    for (int i = 0; i < numMF; i++) {
                        writer.print(target[i]);
                        writer.print(' ');
                    }
                    writer.println();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            SingleObjectiveError sError = new SingleObjectiveError(distance, extractor, target);
            MultiObjectiveError mError = new MultiObjectiveError(extractor, target, invSigma);

            List<Dataset> realPopulation = new ArrayList<>();
            for (Dataset dataset : datasets) {
                if ((dataset.name.hashCode() & 3) == (targetDataset.name.hashCode() & 3)) {
                    continue;
                }

                if (sError.applyAsDouble(dataset) < 1) {
                    continue;
                }
                realPopulation.add(dataset);
            }
            List<Dataset> nullPopulation = null;

            for (ToDoubleArrayFunction<Dataset> errorFunction : Arrays.asList(sError, mError)) {
                boolean singleObjective = errorFunction instanceof ToDoubleFunction;

                for (List<Dataset> initPopulation : Arrays.asList(realPopulation, nullPopulation)) {

                    boolean realInitialPopulation = initPopulation != null;

                    for (Function<Limited, Problem<?>> problemBuilder : Problems.list(direct, gmmcon, mutation, initPopulation)) {
                        for (Function<Problem<?>, Algorithm<?>> algorithmBuilder : Algorithms.list(singleObjective, crossover, mutation)) {
                            Limited limited = new Limited(errorFunction, sError, limit);
                            Problem<?> problem = problemBuilder.apply(limited);
                            if (problem == null) {
                                continue;
                            }

                            Algorithm<?> algorithm = algorithmBuilder.apply(problem);
                            if (algorithm == null) {
                                continue;
                            }

                            int eid = currentExperimentId++;

                            executor.submit(new Runnable() {
                                @Override
                                public void run() {

                                    String tag = targetName + " " + problem.getName() + " " + algorithm.getName() + " " + singleObjective + " " + realInitialPopulation;

                                    System.out.println("START " + tag);
                                    System.out.flush();

                                    try {
                                        long startTime = System.currentTimeMillis();
                                        try {
                                            algorithm.run();
                                            algorithm.getResult();
                                        } catch (EndSearch e) {
                                            System.out.println("FINISH " + tag);
                                            System.out.flush();
                                        }
                                        long finishTime = System.currentTimeMillis();

                                        Dataset result = limited.dataset;
                                        Instances instances = WekaConverter.convert(result);

                                        synchronized (res) {

                                            try (PrintWriter writer = new PrintWriter(new File(res + eid + ".arff"))) {
                                                printLine(writer, targetName);
                                                printLine(writer, Boolean.toString(singleObjective));
                                                printLine(writer, Boolean.toString(realInitialPopulation));
                                                printLine(writer, problem.getName());
                                                printLine(writer, algorithm.getName());
                                                printLine(writer, Long.toString(finishTime - startTime));
                                                printLine(writer, Double.toString(limited.best));

                                                writer.print("%");
                                                double[] mf = extractor.apply(result);
                                                for (int i = 0; i < mf.length; i++) {
                                                    writer.print(' ');
                                                    writer.print(mf[i]);
                                                }
                                                writer.println();
                                                writer.println(instances);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println();
                                            System.out.flush();
                                        }
                                    } catch (RuntimeException exception) {
                                    }
                                }

                                void printLine(PrintWriter writer, String line) {
                                    writer.println("% " + line);
                                    System.out.println(line);
                                }
                            });

                        }

                    }
                }
            }

        }
        executor.shutdown();
    }

}
