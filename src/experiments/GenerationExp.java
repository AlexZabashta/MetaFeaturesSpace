package experiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

import clsf.Dataset;
import clsf.WekaConverter;
import clsf.ndse.gen_op.DatasetCrossover;
import clsf.ndse.gen_op.DatasetMutation;
import clsf.vect.Converter;
import clsf.vect.DirectConverter;
import clsf.vect.GMMConverter;
import fitness_function.Limited;
import fitness_function.MahalanobisDistance;
import fitness_function.MultiObjectiveError;
import fitness_function.SingleObjectiveError;
import mfextraction.CMFExtractor;
import utils.ArrayUtils;
import utils.BlockingThreadPoolExecutor;
import utils.EndSearch;
import utils.FolderUtils;
import utils.MatrixUtils;
import utils.StatUtils;
import utils.ToDoubleArrayFunction;
import weka.core.Instances;

public class GenerationExp {

    final static String[] ignore = { "1059.arff", "1061.arff", "1068.arff", "1448.arff", "1465.arff", "1511.arff", "1520.arff", "1553.arff", "336.arff", "337.arff", "35.arff", "43.arff", "476.arff", "49.arff", "62.arff", "683.arff", "717.arff", "719.arff", "726.arff", "730.arff", "731.arff", "746.arff", "756.arff",
            "758.arff", "770.arff", "812.arff", "813.arff", "826.arff", "829.arff", "845.arff", "855.arff", "861.arff", "865.arff", "867.arff", "896.arff", "902.arff", "907.arff", "908.arff", "922.arff", "931.arff", "934.arff", "936.arff", "941.arff", "951.arff", "955.arff", "968.arff", "998.arff" };

    public static void main(String[] args) throws IOException {

        final int limit = 3000;
        final int cores = 16;

        System.out.println("cores = " + cores);
        System.out.println("limit = " + limit);

        double[][] metaData = new double[1024][];

        List<Dataset> datasets = DataReader.readData("data.csv", new File("data"));
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

        String commonPath = Long.toString(System.currentTimeMillis());
        String targetPath = FolderUtils.buildPath(false, commonPath + "target");
        String resultPath = FolderUtils.buildPath(false, commonPath + "result");

        ExecutorService executor = new BlockingThreadPoolExecutor(cores, false);

        int currentExperimentId = 6000;

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

        Set<String> ignoreData = new HashSet<>(Arrays.asList(ignore));

        for (Dataset targetDataset : datasets) {
            final String targetName = targetDataset.name;

            if (ignoreData.contains(targetName)) {
                continue;
            }

            final double[] target = extractor.apply(targetDataset);

            synchronized (commonPath) {
                try (PrintWriter writer = new PrintWriter(new File(targetPath + targetName))) {
                    writer.print("%");
                    for (int i = 0; i < numMF; i++) {
                        writer.print(' ');
                        writer.print(target[i]);
                    }
                    writer.println();
                    Instances instances = WekaConverter.convert(targetDataset);
                    writer.println(instances);

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

                                        synchronized (commonPath) {

                                            try (PrintWriter writer = new PrintWriter(new File(resultPath + eid + ".arff"))) {
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
