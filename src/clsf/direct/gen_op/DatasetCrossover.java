package clsf.direct.gen_op;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.operator.CrossoverOperator;

import clsf.ClDataset;
import clsf.direct.DataSetSolution;
import utils.ArrayUtils;
import utils.RandomUtils;

public class DatasetCrossover implements CrossoverOperator<DataSetSolution> {

    private static final long serialVersionUID = 1L;

    @Override
    public List<DataSetSolution> execute(List<DataSetSolution> source) {
        if (source.size() != 2) {
            throw new IllegalArgumentException("Source should have two datasets.");
        }

        ClDataset realAncestA = source.get(0).getClDataset(), realAncestB = source.get(1).getClDataset();

        ClDataset ancestorA = realAncestA, ancestorB = realAncestB;

        long seed = 1L * ancestorA.hashCode() * ancestorB.hashCode();

        Random random = new Random(seed);

        int numClasses = RandomUtils.randomFromSegment(random, ancestorA.numClasses, ancestorB.numClasses);

        for (int attempt = 0; attempt < 10; attempt++) {
            if (ancestorA.numClasses == numClasses && ancestorB.numClasses == numClasses) {
                break;
            }
            ancestorA = ChangeNumClasses.apply(ancestorA, random, numClasses);
            ancestorB = ChangeNumClasses.apply(ancestorB, random, numClasses);
            numClasses = RandomUtils.randomFromSegment(random, ancestorA.numClasses, ancestorB.numClasses);
        }

        if (ancestorA.numClasses != numClasses || ancestorB.numClasses != numClasses) {
            return Arrays.asList(new DataSetSolution(realAncestA), new DataSetSolution(realAncestB));
        }

        int[] classDistrA = ancestorA.classDistribution();
        int[] classDistrB = ancestorB.classDistribution();

        int[] orderA = ArrayUtils.order(classDistrA);
        int[] orderB = ArrayUtils.order(classDistrB);

        int sumNumFeatures = ancestorA.numFeatures + ancestorB.numFeatures;

        int numFeaturesA = RandomUtils.randomFromSegment(random, ancestorA.numFeatures, ancestorB.numFeatures);
        int numFeaturesB = sumNumFeatures - numFeaturesA;

        boolean[] featuresDistr = RandomUtils.randomBinarySelection(sumNumFeatures, numFeaturesA, random);

        int newNumObjects = 0;

        int[] subNumObjects = new int[numClasses];

        for (int label = 0; label < numClasses; label++) {
            subNumObjects[label] = RandomUtils.randomFromSegment(random, classDistrA[orderA[label]], classDistrB[orderB[label]]);
            newNumObjects += subNumObjects[label];
        }

        int[] labels = new int[newNumObjects];

        double[][] newDataA = new double[newNumObjects][numFeaturesA];
        double[][] newDataB = new double[newNumObjects][numFeaturesB];

        int[][] indicesA = ancestorA.indices();
        int[][] indicesB = ancestorB.indices();

        for (int oid = 0, label = 0; label < numClasses; label++) {
            int labelA = orderA[label];
            int labelB = orderB[label];

            for (int subId = 0; subId < subNumObjects[label]; subId++, oid++) {
                labels[oid] = label;

                int fidA = 0, fidB = 0;

                int oidA = indicesA[labelA][random.nextInt(classDistrA[labelA])];
                int oidB = indicesB[labelB][random.nextInt(classDistrB[labelB])];

                for (int fid = 0; fid < sumNumFeatures; fid++) {
                    double value;

                    if (fid < numFeaturesA) {
                        value = ancestorA.get(oidA, fid);
                    } else {
                        value = ancestorB.get(oidB, fid - numFeaturesA);
                    }

                    if (featuresDistr[fid]) {
                        newDataA[oid][fidA++] = value;
                    } else {
                        newDataB[oid][fidB++] = value;
                    }
                }
            }
        }

        ClDataset offspringA = (new ClDataset(ancestorA.name, true, newDataA, false, labels));
        if (offspringA.numObjects < Math.min(ancestorA.numObjects, ancestorB.numObjects)) {
            offspringA = ancestorA;
        }
        if (offspringA.numObjects > Math.max(ancestorA.numObjects, ancestorB.numObjects)) {
            offspringA = ancestorA;
        }

        ClDataset offspringB = (new ClDataset(ancestorB.name, true, newDataB, false, labels));
        if (offspringB.numObjects < Math.min(ancestorA.numObjects, ancestorB.numObjects)) {
            offspringB = ancestorB;
        }

        if (offspringB.numObjects > Math.max(ancestorA.numObjects, ancestorB.numObjects)) {
            offspringB = ancestorB;
        }

        return Arrays.asList(new DataSetSolution(offspringA), new DataSetSolution(offspringB));
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

}
