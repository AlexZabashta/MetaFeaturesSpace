package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.ToDoubleFunction;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.gde3.GDE3Builder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder.Variant;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.paes.PAESBuilder;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import clsf.CMFExtractor;
import clsf.ClDataset;
import clusterization.MetaFeaturesExtractor;
import clusterization.direct.Crossover;
import clusterization.direct.DataSetSolution;
import clusterization.direct.GDSProblem;
import clusterization.direct.Mutation;
import clusterization.vect.GMMProblem;
import clusterization.vect.SimpleProblem;
import utils.ArrayUtils;
import utils.BlockingThreadPoolExecutor;
import utils.EndSearch;
import utils.Experiment;
import utils.FolderUtils;
import utils.Limited;
import utils.MahalanobisDistance;
import utils.MatrixUtils;
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
        int numMF = extractor.lenght;

        for (int i = 0; i < numData; i++) {
            ClDataset dataset = datasets.get(i);
            metaData[i] = new double[-1]; // TODO copy MF dataset
        }

        double[][] cov = StatUtils.covarianceMatrix(numData, numMF, metaData);
        ArrayUtils.print(cov);
        double[][] invCov = MatrixUtils.inv(numMF, cov);
        ArrayUtils.print(invCov);

        MahalanobisDistance distance = new MahalanobisDistance(numMF, invCov);

        String res = FolderUtils.buildPath(false, Long.toString(System.currentTimeMillis()));

        final int size = datasets.size();

        ExecutorService threads = new BlockingThreadPoolExecutor(cores, false);

        for (int targetIndex = 0; targetIndex < size; targetIndex++) {
            List<Experiment> exp = new ArrayList<>();

            ClDataset targetClDataset = datasets.get(targetIndex);
            final double[] target = extractor.apply(targetClDataset);

            ToDoubleFunction<ClDataset> function = new ToDoubleFunction<ClDataset>() {
                @Override
                public double applyAsDouble(ClDataset value) {
                    try {
                        return distance.distance(target, extractor.apply(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 100;
                    }
                }
            };

            final List<ClDataset> adatasets = new ArrayList<>();

            for (ClDataset dataset : datasets) {
                if (function.applyAsDouble(dataset) > 1) {
                    adatasets.add(dataset);
                }
            }

            final String fileName = fileNames.get(targetClDataset).replace('_', '-');

            try (PrintWriter writer = new PrintWriter(new File(res + fileName + ".txt"))) {
                for (int i = 0; i < numMF; i++) {
                    writer.print(target[i]);
                    writer.print(' ');
                }
                writer.println();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new GDE3Builder(problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;

                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new IBEABuilder(problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;

                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new MOCellBuilder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new MOCellBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new MOEADBuilder(problem, Variant.MOEAD).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new NSGAIIBuilder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new OMOPSOBuilder(problem, new SequentialSolutionListEvaluator<>()).setMaxIterations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new PAESBuilder<DataSetSolution>(problem).setMutationOperator(new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new PAESBuilder<DoubleSolution>(problem).setMutationOperator(new PolynomialMutation()).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new PESA2Builder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new PESA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new RandomSearchBuilder<DataSetSolution>(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new RandomSearchBuilder<DoubleSolution>(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }
            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new SMSEMOABuilder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new SMSEMOABuilder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxEvaluations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    GDSProblem problem = (GDSProblem) prob;

                    Algorithm<?> algorithm = new SPEA2Builder<DataSetSolution>(problem, new Crossover(extractor), new Mutation(targetClDataset.numObjects, targetClDataset.numFeatures, extractor)).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);

                } catch (ClassCastException ifNotGDS) {
                }

                try {
                    DoubleProblem problem = (DoubleProblem) prob;
                    Algorithm<?> algorithm = new SPEA2Builder<DoubleSolution>(problem, new SBXCrossover(0.9, 20.0), new PolynomialMutation()).setMaxIterations(10000000).setPopulationSize(32).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            for (Experiment probFunAlg : problems(targetClDataset.numObjects, targetClDataset.numFeatures, function, limit, adatasets, extractor)) {
                Problem<?> prob = probFunAlg.problem;
                probFunAlg.name = fileName;

                try {
                    DoubleProblem problem = (DoubleProblem) prob;

                    if (problem.getNumberOfVariables() > 500) {
                        continue;
                    }

                    Algorithm<?> algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(problem).setMaxEvaluations(10000000).build();
                    probFunAlg.algorithm = algorithm;
                    exp.add(probFunAlg);
                } catch (ClassCastException ifNotDouble) {
                }
            }

            System.out.println("EXP = " + exp.size());

            for (final Experiment probFunAlg : exp) {
                final Algorithm<?> algorithm = probFunAlg.algorithm;
                if (algorithm == null) {
                    continue;
                }
                final Problem<?> problem = probFunAlg.problem;
                final String file = probFunAlg.name;
                final Limited fun = probFunAlg.function;

                String name = algorithm.getClass().getSimpleName() + "_" + problem.getName() + "_" + file;

                threads.submit(new Runnable() {
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

                        ClDataset dataset = fun.dataset;
                        if (dataset != null) {
                            Instances instances = dataset.toInstances();
                            synchronized (res) {
                                try (PrintWriter writer = new PrintWriter(new File(res + name))) {
                                    writer.println("% " + fun.best);
                                    writer.print("%");

                                    double[] mf = dataset.metaFeatures();

                                    for (int i = 0; i < numMF; i++) {
                                        writer.print(' ');
                                        writer.print(mf[i]);
                                    }

                                    writer.println();

                                    writer.println(instances);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(name + " " + fun.best);
                                System.out.flush();
                            }

                        }
                    }

                });

            }

        }
        threads.shutdown();
    }

    static List<Experiment> problems(int numObjects, int numFeatures, ToDoubleFunction<ClDataset> ef, int limit, List<ClDataset> datasets, MetaFeaturesExtractor extractor) {
        final List<Experiment> problems = new ArrayList<Experiment>();

        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(numObjects, numFeatures, lef, datasets, extractor));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GDSProblem(numObjects, numFeatures, lef, null, extractor));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(numObjects, numFeatures, lef, datasets, extractor));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new SimpleProblem(numObjects, numFeatures, lef, null, extractor));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GMMProblem(numObjects, numFeatures, lef, datasets, extractor));
            problems.add(new Experiment(problem, lef));
        }
        {
            Limited lef = new Limited(ef, limit);
            Problem<?> problem = (new GMMProblem(numObjects, numFeatures, lef, null, extractor));
            problems.add(new Experiment(problem, lef));
        }

        return problems;
    }

}
