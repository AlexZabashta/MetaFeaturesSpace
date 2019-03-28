package clsf.direct.gen_op;

import java.util.Random;

import clsf.ClDataset;
import clsf.direct.gen_op.fun.cat.CatFunction;
import clsf.direct.gen_op.fun.cat.ClassValue;
import clsf.direct.gen_op.fun.cat.SumMod;

public class ChangeNumClasses {

    public static ClDataset apply(ClDataset dataset, Random random) {
        int n = dataset.numClasses;
        if (n == 2) {
            return apply(dataset, random, 3);
        } else {
            return apply(dataset, random, n + random.nextInt(2) * 2 - 1);
        }
    }

    public static ClDataset apply(ClDataset dataset, Random random, int newNumClasses) {
        int oldNumClasses = dataset.numClasses;

        if (oldNumClasses == newNumClasses) {
            return dataset;
        }

        int numObjects = dataset.numObjects;
        CatFunction randomFun = CatFunction.random(dataset, random, 2);
        CatFunction classVal = new ClassValue(dataset);

        CatFunction classMaper = new SumMod(randomFun, classVal, newNumClasses, random);

        int[] labels = new int[numObjects];

        for (int i = 0; i < numObjects; i++) {
            labels[i] = classMaper.applyAsInt(dataset.item(i));
        }

        return dataset.changeLabels(true, labels);
    }

    public static void main(String[] args) {
        Random random = new Random();
        System.out.println(random.nextInt(2) * 2 - 1);
    }

}
