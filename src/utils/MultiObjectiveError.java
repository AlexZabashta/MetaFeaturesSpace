package utils;

import java.util.Arrays;

import clsf.ClDataset;

public class MultiObjectiveError implements ToDoubleArrayFunction<ClDataset> {

    final ToDoubleArrayFunction<ClDataset> extractor;

    final int length;

    final double[] target, invSigma;

    public MultiObjectiveError(ToDoubleArrayFunction<ClDataset> extractor, double[] target, double[] invSigma) {
        this.length = extractor.length();
        if (target.length != this.length) {
            throw new IllegalArgumentException("target.length != extractor.length()");
        }
        if (invSigma.length != this.length) {
            throw new IllegalArgumentException("invSigma.length != extractor.length()");
        }
        this.target = target;
        this.invSigma = invSigma;
        this.extractor = extractor;
    }

    @Override
    public double[] apply(ClDataset dataset) {
        double[] diffs = new double[length];
        Arrays.fill(diffs, 100);
        try {
            double[] result = extractor.apply(dataset);
            for (int i = 0; i < length; i++) {
                diffs[i] = Math.abs(target[i] - result[i]) * invSigma[i];
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return diffs;
    }

    @Override
    public int length() {
        return length;
    }

}
