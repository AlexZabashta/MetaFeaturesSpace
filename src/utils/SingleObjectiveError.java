package utils;

import java.util.function.ToDoubleBiFunction;

import clsf.ClDataset;
import tmp.ToDoubleArrayFunction;

public class SingleObjectiveError implements ToDoubleArrayFunction<ClDataset> {

    final double[] target;

    final ToDoubleArrayFunction<ClDataset> extractor;
    final ToDoubleBiFunction<double[], double[]> distance;

    public SingleObjectiveError(ToDoubleBiFunction<double[], double[]> distance, ToDoubleArrayFunction<ClDataset> extractor, double[] target) {
        if (target.length != extractor.length()) {
            throw new IllegalArgumentException("target.length != extractor.length()");
        }

        this.target = target;
        this.extractor = extractor;
        this.distance = distance;
    }

    @Override
    public double[] apply(ClDataset dataset) {
        try {
            return new double[] { distance.applyAsDouble(extractor.apply(dataset), target) };
        } catch (RuntimeException exception) {
            return new double[] { 100 };
        }
    }

    @Override
    public int length() {
        return 1;
    }

}
