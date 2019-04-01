package utils;

import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import clsf.ClDataset;

public class SingleObjectiveError implements ToDoubleArrayFunction<ClDataset>, ToDoubleFunction<ClDataset> {

    final ToDoubleBiFunction<double[], double[]> distance;

    final ToDoubleArrayFunction<ClDataset> extractor;
    final double[] target;

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
        return new double[] { applyAsDouble(dataset) };
    }

    @Override
    public double applyAsDouble(ClDataset dataset) {
        try {
            return distance.applyAsDouble(extractor.apply(dataset), target);
        } catch (RuntimeException exception) {
            return 100;
        }
    }

    @Override
    public int length() {
        return 1;
    }

}
