package clsf.vect;

import java.util.List;
import java.util.Random;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import clsf.ClDataset;
import clsf.MetaFeaturesExtractor;
import features_inversion.classification.dataset.RelationsGenerator;
import tmp.ToDoubleArrayFunction;
import utils.MatrixUtils;
import utils.StatUtils;

public class GMMProblem implements DoubleProblem {

    private static final long serialVersionUID = 1L;
    final ToDoubleArrayFunction<ClDataset> errorFunction;
    final List<ClDataset> datasets;
    final Random random = new Random();
    final int numFeatures, numObjects;
    public final MetaFeaturesExtractor extractor;

    public GMMProblem(int numObjects, int numFeatures, ToDoubleArrayFunction<ClDataset> errorFunction, List<ClDataset> datasets, MetaFeaturesExtractor extractor) {
        this.numObjects = numObjects;
        this.numFeatures = numFeatures;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
        this.extractor = extractor;
    }

    final static double lowerBound = -10;
    final static double upperBound = +10;

    @Override
    public int getNumberOfVariables() {
        return numFeatures * (numFeatures + 1) / 2;
    }

    @Override
    public int getNumberOfObjectives() {
        return errorFunction.length();
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    public DoubleSolution build(ClDataset dataset) {
        DoubleSolution solution = new DefaultDoubleSolution(this);

        double[][] data = dataset.data();
        data = RelationsGenerator.changeNumFeatures(data, dataset.numFeatures, numFeatures, random);
        double[][] cov = StatUtils.covarianceMatrix(data.length, numFeatures, data);

        for (int i = 0; i < numFeatures; i++) {
            cov[i][i] += 1e-3;
        }

        try {
            double[][] sqrt = MatrixUtils.sqrt(numFeatures, cov);
            for (int index = 0, i = 0; i < numFeatures; i++) {
                for (int j = 0; j <= i; j++) {
                    solution.setVariableValue(index++, sqrt[i][j]);
                }
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return solution;
    }

    public ClDataset build(DoubleSolution solution) {
        double[][] sqrt = new double[numFeatures][numFeatures];
        for (int index = 0, i = 0; i < numFeatures; i++) {
            for (int j = 0; j <= i; j++) {
                sqrt[i][j] = solution.getVariableValue(index++);
            }
        }

        double[][] rand = new double[numObjects][numFeatures];

        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                rand[i][j] = random.nextGaussian();
            }
        }
        double[][] data = MatrixUtils.mul(numObjects, numFeatures, numFeatures, rand, sqrt);

        return new ClDataset(data, extractor);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        int length = errorFunction.length();
        double[] error = errorFunction.apply(build(solution));
        for (int i = 0; i < length; i++) {
            solution.setObjective(i, error[i]);
        }
    }

    @Override
    public DoubleSolution createSolution() {
        if (datasets == null || datasets.isEmpty()) {
            return new DefaultDoubleSolution(this);
        } else {
            return build(datasets.get(random.nextInt(datasets.size())));
        }
    }

    @Override
    public Double getLowerBound(int index) {
        return lowerBound;
    }

    @Override
    public Double getUpperBound(int index) {
        return upperBound;
    }

}
