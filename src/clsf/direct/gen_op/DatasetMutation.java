package clsf.direct.gen_op;

import java.util.Random;

import org.uma.jmetal.operator.MutationOperator;

import clsf.ClDataset;
import clsf.direct.DataSetSolution;
import utils.RandomUtils;

public class DatasetMutation implements MutationOperator<DataSetSolution> {
    private static final long serialVersionUID = 1L;
    public static void main(String[] args) {
        ClDataset.defaultNormalize = false;

        Random random = new Random();

        int numObjects = random.nextInt(10) + 1;
        int numFeatures = random.nextInt(10) + 1;

        double[][] data = new double[numObjects][numFeatures];

        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < numFeatures; j++) {
                data[i][j] = i + j / 10.0;
            }
        }
    }
    public final int minNumClasses, maxNumClasses;
    public final int minNumFeatures, maxNumFeatures;

    public final int minNumObjects, maxNumObjects;

    public DatasetMutation(int minNumObjects, int maxNumObjects, int minNumFeatures, int maxNumFeatures, int minNumClasses, int maxNumClasses) {
        this.minNumObjects = minNumObjects;
        this.maxNumObjects = maxNumObjects;
        this.minNumFeatures = minNumFeatures;
        this.maxNumFeatures = maxNumFeatures;
        this.minNumClasses = minNumClasses;
        this.maxNumClasses = maxNumClasses;
    }

    public ClDataset execute(ClDataset dataset) {
        Random random = new Random(dataset.hashCode());
        ClDataset mutant = executeAny(dataset, random);
        if (mutant.numClasses < minNumClasses || mutant.numClasses > maxNumClasses) {
            return dataset;
        }
        if (mutant.numFeatures < minNumFeatures || mutant.numFeatures > maxNumFeatures) {
            return dataset;
        }
        if (mutant.numObjects < minNumObjects || mutant.numObjects > maxNumObjects) {
            return dataset;
        }
        return mutant;
    }

    @Override
    public DataSetSolution execute(DataSetSolution source) {
        return new DataSetSolution(execute(source.getClDataset()));
    }

    public ClDataset executeAny(ClDataset dataset, Random random) {

        if (random.nextBoolean()) {
            int newNumObjects = RandomUtils.randomLocal(random, dataset.numObjects, 10, minNumObjects, maxNumObjects);
            return ChangeNumObjects.apply(dataset, random, newNumObjects);
        } else {
            if (random.nextBoolean()) {
                int newNumFeatures = RandomUtils.randomLocal(random, dataset.numFeatures, 5, minNumFeatures, maxNumFeatures);
                return ChangeNumFeatures.apply(dataset, random, newNumFeatures);
            } else {
                int newNumClasses = RandomUtils.randomLocal(random, dataset.numClasses, 1, minNumClasses, maxNumClasses);
                return ChangeNumClasses.apply(dataset, random, newNumClasses);
            }
        }
    }

    public ClDataset generate(Random random) {
        return null; // TODO
    }
}
