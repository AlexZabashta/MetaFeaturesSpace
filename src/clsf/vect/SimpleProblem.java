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
import utils.EndSearch;

public class SimpleProblem implements DoubleProblem {

    private static final long serialVersionUID = 1L;
    final ToDoubleArrayFunction<ClDataset> errorFunction;
    final List<ClDataset> datasets;
    final Random random = new Random();
    final int numObjectsPerClass;
    final int numClasses;
    final int numFeatures;

    final int[] numFeaturesDistribution = new int[123];
    final int[] numObjectsDistribution = new int[123];

    public SimpleProblem(int numObjectsPerClass, int numFeatures, int numClasses, ToDoubleArrayFunction<ClDataset> errorFunction, List<ClDataset> datasets) {
        this.numObjectsPerClass = numObjectsPerClass;
        this.numFeatures = numFeatures;
        this.numClasses = numClasses;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
    }

    final static double lowerBound = -10;
    final static double upperBound = 10;

    @Override
    public int getNumberOfVariables() {
        return numFeatures * numObjectsPerClass * numClasses + numClasses + 2;
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
        data = RelationsGenerator.changeNumObjects(data, numObjects, dataset.numFeatures, random);
        data = RelationsGenerator.changeNumFeatures(data, dataset.numFeatures, numFeatures, random);

        for (int index = 0, i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                solution.setVariableValue(index++, data[i][j]);
            }
        }

        return solution;
    }

    public ClDataset build(DoubleSolution solution) {

        // [numObjects] * numClasses
        //

        // numObjectsPerClass;
        // numClasses;
        // numFeatures;

        // numFeaturesDistribution
        // numObjectsDistribution

        double[][] data = new double[numObjects][numFeatures];
        for (int index = 0, i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                data[i][j] = solution.getVariableValue(index++);
            }
        }
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
