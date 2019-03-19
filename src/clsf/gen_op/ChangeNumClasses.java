package clsf.gen_op;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import clsf.aDataset;
import utils.AddClassMapper;
import utils.RandomUtils;
import utils.RemoveClassMapper;

public class ChangeNumClasses implements UnaryOperator<aDataset> {

    private final Random random;

    public ChangeNumClasses(Random random) {
        this.random = random;
    }

    public static aDataset apply(aDataset dataset, Random random) {
        int n = dataset.numClasses();
        if (n == 2) {
            return apply(dataset, random, 3);
        } else {
            return apply(dataset, random, n + random.nextInt(2) * 2 - 1);
        }
    }

    public static aDataset apply(aDataset dataset, Random random, int newNumClasses) {
        int oldNumClasses = dataset.numClasses();

        if (oldNumClasses == newNumClasses) {
            return dataset;
        }

        IntUnaryOperator maper;
        if (oldNumClasses < newNumClasses) {
            maper = new AddClassMapper(oldNumClasses, newNumClasses, random);
        } else {
            maper = new RemoveClassMapper(oldNumClasses, newNumClasses, random);
        }

        int n = dataset.numObjects();

        int c = dataset.numCatAttr();
        int r = dataset.numRatAttr();

        int[][] cat = dataset.catValues();
        double[][] rat = dataset.ratValues();

        for (int i = 0; i < n; i++) {
            cat[i][c] = maper.applyAsInt(cat[i][c]);
        }

        return new aDataset(n, c, cat, r, rat);
    }

    @Override
    public aDataset apply(aDataset dataset) {
        return apply(dataset, random);
    }

    public static void main(String[] args) {
        Random random = new Random();
        System.out.println(random.nextInt(2) * 2 - 1);
    }

}
