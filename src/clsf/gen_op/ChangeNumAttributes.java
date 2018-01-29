package clsf.gen_op;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import clsf.Dataset;
import clsf.gen_op.fun.cat.CatFunction;
import clsf.gen_op.fun.rat.RatFunction;
import utils.AddClassMapper;
import utils.RandomUtils;
import utils.RemoveClassMapper;

public class ChangeNumAttributes implements UnaryOperator<Dataset> {

    private final Random random;

    public ChangeNumAttributes(Random random) {
        this.random = random;
    }

    public static Dataset apply(Dataset dataset, Random random) {
        if (random.nextBoolean()) {

        } else {

        }
        return null;
    }

    public static Dataset changeNumCatAttr(Dataset dataset, Random random) {
        return null;
    }

    public static Dataset addCatAttr(Dataset dataset, Random random, int newNumCatAttr) {
        int oldNumCatAttr = dataset.numCatAttr();

        int numObjects = dataset.numObjects();

        int numRatAttr = dataset.numRatAttr();
        double[][] ratValues = dataset.ratValues();

        int[][] catValues = new int[numObjects][newNumCatAttr + 1];

        for (int i = 0; i < numObjects; i++) {
            catValues[i][newNumCatAttr] = dataset.classValue(i);
            for (int j = 0; j < oldNumCatAttr; j++) {
                catValues[i][j] = dataset.catValue(i, j);
            }
        }

        for (int j = oldNumCatAttr; j < newNumCatAttr; j++) {
            int d = random.nextInt(3) + 1;
            CatFunction function = CatFunction.random(dataset, random, d);
            for (int i = 0; i < numObjects; i++) {
                catValues[i][j] = function.applyAsInt(dataset.item(i));
            }
        }
        return new Dataset(numObjects, newNumCatAttr, catValues, numRatAttr, ratValues);
    }

    public static Dataset addRatAttr(Dataset dataset, Random random, int newNumRatAttr) {
        int oldNumRatAttr = dataset.numRatAttr();

        int numObjects = dataset.numObjects();

        double[][] ratValues = new double[numObjects][newNumRatAttr];

        for (int i = 0; i < numObjects; i++) {
            for (int j = 0; j < oldNumRatAttr; j++) {
                ratValues[i][j] = dataset.ratValue(i, j);
            }
        }

        for (int j = oldNumRatAttr; j < newNumRatAttr; j++) {
            int d = random.nextInt(3) + 1;
            RatFunction function = RatFunction.random(dataset, random, d);

            for (int i = 0; i < numObjects; i++) {
                ratValues[i][j] = function.applyAsDouble(dataset.item(i));
            }
        }

        int numCatAttr = dataset.numCatAttr();
        int[][] catValues = dataset.catValues();
        return new Dataset(numObjects, numCatAttr, catValues, newNumRatAttr, ratValues);
    }

    @Override
    public Dataset apply(Dataset dataset) {
        return apply(dataset, random);
    }

}
