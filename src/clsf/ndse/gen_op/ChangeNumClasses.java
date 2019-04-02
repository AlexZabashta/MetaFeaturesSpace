package clsf.ndse.gen_op;

import java.util.Random;

import clsf.Dataset;
import clsf.ndse.gen_op.fun.cat.CatFunction;
import clsf.ndse.gen_op.fun.cat.ClassValue;
import clsf.ndse.gen_op.fun.cat.SumMod;

public class ChangeNumClasses {

    public static Dataset apply(Dataset dataset, Random random, int newNumClasses) {
        int oldNumClasses = dataset.numClasses;

        if (oldNumClasses == newNumClasses) {
            return dataset;
        }

        int numObjects = dataset.numObjects;
        CatFunction randomFun = CatFunction.random(dataset, random, 3, newNumClasses);
        CatFunction classVal = new ClassValue(dataset);

        CatFunction classMaper = new SumMod(randomFun, classVal, newNumClasses, random);

        int[] labels = new int[numObjects];

        for (int oid = 0; oid < numObjects; oid++) {
            labels[oid] = classMaper.applyAsInt(oid);
        }

        return dataset.changeLabels(true, labels);
    }

    public static void main(String[] args) {
        Random random = new Random();
        System.out.println(random.nextInt(2) * 2 - 1);
    }

}
