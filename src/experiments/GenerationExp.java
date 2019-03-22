package experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

import clsf.CMFExtractor;
import clsf.ClDataset;
import clusterization.direct.Crossover;
import clusterization.direct.Mutation;
import tmp.ToDoubleArrayFunction;
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
import weka.core.Instances;

public class GenerationExp {

    public static List<ClDataset> readData(File folder) {
        List<ClDataset> datasets = new ArrayList<>();

        for (File file : folder.listFiles()) {
            try (Reader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);

                int numObjects = instances.numInstances();
                int numFeatures = instances.numAttributes() - 1;

                // TODO SET CLASS

                double[][] data = new double[numObjects][numFeatures];
                int[] labels = new int[numObjects];

                // TODO COPY DATA

                datasets.add(new ClDataset(file.getName(), true, data, labels));

                System.out.println(file.getName());
                System.out.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return datasets;
    }

    public static void main(String[] args) {

        int paramLimit = 100;
        int paramCores = 4;

        try {
            paramLimit = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.err.println("default limit = " + paramLimit);
        }

        try {
            paramLimit = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("default cores = " + paramCores);
        }
        final int limit = paramLimit;
        final int cores = paramCores;

        double[][] metaData = new double[1024][];

        List<ClDataset> datasets = readData(new File("pdata"));
        final int numData = datasets.size();

        CMFExtractor extractor = new CMFExtractor();
        int numMF = extractor.length();

        for (int i = 0; i < numData; i++) {
            metaData[i] = extractor.extract(datasets.get(i));
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

        final int size = datasets.size();

        ExecutorService executor = new BlockingThreadPoolExecutor(cores, false);

        int currentExperimentId = 0;

        for (int targetIndex = 0; targetIndex < size; targetIndex++) {
            ClDataset targetDataset = datasets.get(targetIndex);
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

            List<ClDataset> realPopulation = new ArrayList<>();
            for (ClDataset dataset : datasets) {
                if (sError.applyAsDouble(dataset) > 1) {
                    realPopulation.add(dataset);
                }
            }
            List<ClDataset> nullPopulation = null;

            for (ToDoubleArrayFunction<ClDataset> errorFunction : Arrays.asList(sError, mError)) {
                boolean singleObjective = errorFunction instanceof ToDoubleFunction;

                for (List<ClDataset> initPopulation : Arrays.asList(realPopulation, nullPopulation)) {

                    boolean realInitialPopulation = initPopulation != null;

                    Crossover crossover = new Crossover();
                    Mutation mutation = new Mutation(-1, -1, -1, -1); // TODO set limits

                    for (Function<Limited, Problem<?>> problemBuilder : Problems.list(errorFunction, initPopulation)) {
                        for (Function<Problem<?>, Algorithm<?>> algorithmBuilder : Algorithms.list(singleObjective, crossover, mutation)) {
                            Limited limited = new Limited(errorFunction, sError, limit);
                            Problem<?> problem = problemBuilder.apply(limited);
                            Algorithm<?> algorithm = algorithmBuilder.apply(problem);

                            int eid = currentExperimentId++;

                            executor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        algorithm.run();
                                        algorithm.getResult();
                                    } catch (RuntimeException exception) {
                                        if (!(exception instanceof EndSearch)) {
                                            exception.printStackTrace();
                                        }
                                    }
                                    ClDataset result = limited.dataset;

                                    if (result == null) {
                                        return;
                                    }

                                    synchronized (res) {

                                        try (PrintWriter writer = new PrintWriter(new File(res + eid + ".txt"))) {
                                            printLine(writer, targetName);
                                            printLine(writer, Boolean.toString(singleObjective));
                                            printLine(writer, Boolean.toString(realInitialPopulation));
                                            printLine(writer, (problem.getClass().getSimpleName()));
                                            printLine(writer, (algorithm.getClass().getSimpleName()));
                                            printLine(writer, Double.toString(limited.best));

                                            writer.print("%");
                                            double[] mf = extractor.apply(result);
                                            for (int i = 0; i < mf.length; i++) {
                                                writer.print(' ');
                                                writer.print(mf[i]);
                                            }
                                            writer.println();
                                            writer.println(result.toInstances());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println();
                                        System.out.flush();
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
