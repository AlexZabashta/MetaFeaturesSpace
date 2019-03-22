package utils;

import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import clsf.ClDataset;
import tmp.ToDoubleArrayFunction;

public class SingleObjectiveError implements ToDoubleArrayFunction<ClDataset>, ToDoubleFunction<ClDataset> {

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
        return new double[] { applyAsDouble(dataset) };
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public double applyAsDouble(ClDataset dataset) {
        try {
            return distance.applyAsDouble(extractor.apply(dataset), target);
        } catch (RuntimeException exception) {
            return 100;
        }
    }

}