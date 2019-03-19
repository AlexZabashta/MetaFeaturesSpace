package clsf.gen_op;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import clsf.aDataset;
import clsf.gen_op.fun.cat.CatFunction;
import clsf.gen_op.fun.rat.RatFunction;
import utils.AddClassMapper;
import utils.RandomUtils;
import utils.RemoveClassMapper;

public class ChangeNumAttributes implements UnaryOperator<aDataset> {

    private final Random random;

    public ChangeNumAttributes(Random random) {
        this.random = random;
    }

    public static aDataset apply(aDataset dataset, Random random) {
        if (random.nextBoolean()) {

        } else {

        }
        return null;
    }

    public static aDataset changeNumCatAttr(aDataset dataset, Random random) {
        return null;
    }

    public static aDataset addCatAttr(aDataset dataset, Random random, int newNumCatAttr) {
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
        return new aDataset(numObjects, newNumCatAttr, catValues, numRatAttr, ratValues);
    }

    public static aDataset addRatAttr(aDataset dataset, Random random, int newNumRatAttr) {
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
        return new aDataset(numObjects, numCatAttr, catValues, newNumRatAttr, ratValues);
    }

    @Override
    public aDataset apply(aDataset dataset) {
        return apply(dataset, random);
    }

}
