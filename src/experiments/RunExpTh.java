package experiments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearchBuilder;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.algorithm.singleobjective.differentialevolution.DifferentialEvolutionBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.algorithm.singleobjective.particleswarmoptimization.StandardPSO2011;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BestSolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import clsf.ClDataset;
import clusterization.direct.Crossover;
import clusterization.direct.DataSetSolution;
import clusterization.direct.GDSProblem;
import clusterization.direct.Mutation;
import clusterization.vect.GMMProblem;
import tmp.ToDoubleArrayFunction;
import utils.EndSearch;
import utils.Limited;
import weka.core.Instances;

public class RunExpTh {

    static void submitExp(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Algorithm<?> algorithm) {
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

                ClDataset dataset = fun.dataset;
                if (dataset != null) {
                    Instances instances = dataset.toInstances();
                    synchronized (res) {
                        try (PrintWriter writer = new PrintWriter(new File(res + name + ".txt"))) {
                            writer.println("% " + fun.best);
                            writer.print("%");

                            double[] mf = extractor.apply(dataset);

                            for (int i = 0; i < mf.length; i++) {
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

    static void submitToAllProblem(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor) {

    }

    static void submitToCMAES(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        if (fun.baseFunction instanceof ToDoubleFunction) {
            try {
                Algorithm<?> algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder((GMMProblem) problem).setMaxEvaluations(10000000).build();
                submitExp(res, name + "CMAES", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
        }
    }

    static void submitToCR(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        if (fun.baseFunction instanceof ToDoubleFunction) {
            try {
                Algorithm<?> algorithm = new CoralReefsOptimizationBuilder<DoubleSolution>((DoubleProblem) problem, new BestSolutionSelection<>(new DominanceComparator<DoubleSolution>()), new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                submitExp(res, name + "CR", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
            try {
                Algorithm<?> algorithm = new CoralReefsOptimizationBuilder<DataSetSolution>((GDSProblem) problem, new BestSolutionSelection<>(new DominanceComparator<DataSetSolution>()), crossover, mutation).setMaxEvaluations(10000000).build();
                submitExp(res, name + "CR", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
        }
    }

    static void submitToDE(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        if (fun.baseFunction instanceof ToDoubleFunction) {
            try {
                Algorithm<?> algorithm = new DifferentialEvolutionBuilder((DoubleProblem) problem).setMaxEvaluations(10000000).build();
                submitExp(res, name + "DE", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
        }
    }

    static void submitToGA(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        if (fun.baseFunction instanceof ToDoubleFunction) {
            try {
                Algorithm<?> algorithm = new GeneticAlgorithmBuilder<DoubleSolution>((DoubleProblem) problem, new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
                submitExp(res, name + "GA", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
            try {
                Algorithm<?> algorithm = new GeneticAlgorithmBuilder<DataSetSolution>((GDSProblem) problem, crossover, mutation).setMaxEvaluations(10000000).build();
                submitExp(res, name + "GA", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
        }
    }

    static void submitToGDE3(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new GDE3Builder((DoubleProblem) problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
            submitExp(res, name + "GDE3", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToIBEA(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new IBEABuilder((DoubleProblem) problem).setMaxEvaluations(10000000).setPopulationSize(32).build();
            submitExp(res, name + "IBEA", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToMOCell(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new MOCellBuilder<DataSetSolution>((GDSProblem) problem, crossover, mutation).setMaxEvaluations(10000000).build();
            submitExp(res, name + "MOCell", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new MOCellBuilder<DoubleSolution>((DoubleProblem) problem, new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
            submitExp(res, name + "MOCell", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToMOEAD(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new MOEADBuilder((DoubleProblem) problem, Variant.MOEAD).setMaxEvaluations(10000000).setPopulationSize(32).build();
            submitExp(res, name + "MOEAD", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToNSGAII(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new NSGAIIBuilder<DataSetSolution>((GDSProblem) problem, crossover, mutation).setMaxEvaluations(10000000).build();
            submitExp(res, name + "NSGAII", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new NSGAIIBuilder<DoubleSolution>((DoubleProblem) problem, new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
            submitExp(res, name + "NSGAII", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToOMOPSO(String res, String name, ExecutorService executor, Limited fun, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new OMOPSOBuilder((DoubleProblem) problem, new SequentialSolutionListEvaluator<>()).setMaxIterations(10000000).build();
            submitExp(res, name + "OMOPSO", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToPAES(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new PAESBuilder<DataSetSolution>((GDSProblem) problem).setMutationOperator(mutation).setMaxEvaluations(10000000).build();
            submitExp(res, name + "PAES", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new PAESBuilder<DoubleSolution>((DoubleProblem) problem).setMutationOperator(new PolynomialMutation()).setMaxEvaluations(10000000).build();
            submitExp(res, name + "PAES", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToPSO(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        if (fun.baseFunction instanceof ToDoubleFunction) {
            try {
                Algorithm<?> algorithm = new StandardPSO2011((DoubleProblem) problem, 100, 10000000, 10, new SequentialSolutionListEvaluator<DoubleSolution>());
                submitExp(res, name + "PSO", executor, fun, extractor, algorithm);
            } catch (ClassCastException cce) {
            }
        }
    }

    static void submitToRAND(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new RandomSearchBuilder<DataSetSolution>((GDSProblem) problem).setMaxEvaluations(10000000).build();
            submitExp(res, name + "RAND", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new RandomSearchBuilder<DoubleSolution>((DoubleProblem) problem).setMaxEvaluations(10000000).build();
            submitExp(res, name + "RAND", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToSMSEMOA(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new SMSEMOABuilder<DataSetSolution>((GDSProblem) problem, crossover, mutation).setMaxEvaluations(10000000).build();
            submitExp(res, name + "SMSEMOA", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new SMSEMOABuilder<DoubleSolution>((DoubleProblem) problem, new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxEvaluations(10000000).build();
            submitExp(res, name + "SMSEMOA", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }

    static void submitToSPEA2(String res, String name, ExecutorService executor, Limited fun, Crossover crossover, Mutation mutation, ToDoubleArrayFunction<ClDataset> extractor, Problem<?> problem) {
        try {
            Algorithm<?> algorithm = new SPEA2Builder<DataSetSolution>((GDSProblem) problem, crossover, mutation).setMaxIterations(10000000).build();
            submitExp(res, name + "SPEA2", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }

        try {
            Algorithm<?> algorithm = new SPEA2Builder<DoubleSolution>((DoubleProblem) problem, new SBXCrossover(1.0, 10.0), new PolynomialMutation()).setMaxIterations(10000000).build();
            submitExp(res, name + "SPEA2", executor, fun, extractor, algorithm);
        } catch (ClassCastException cce) {
        }
    }
}
